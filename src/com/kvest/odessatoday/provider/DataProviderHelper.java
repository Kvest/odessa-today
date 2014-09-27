package com.kvest.odessatoday.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.TimeUtils;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
import static com.kvest.odessatoday.utils.LogUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 31.07.14
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class DataProviderHelper {
    public static void addComment(Context context, int targetType, long targetId, String name, String text, long date) {
        //fill ContentValues
        ContentValues values = new ContentValues(6);
        values.put(Tables.Comments.Columns.TARGET_ID, targetId);
        values.put(Tables.Comments.Columns.TARGET_TYPE, targetType);
        values.put(Tables.Comments.Columns.SYNC_STATUS, Constants.SyncStatus.NEED_UPLOAD);
        values.put(Tables.Comments.Columns.DATE, date);
        values.put(Tables.Comments.Columns.NAME, name);
        values.put(Tables.Comments.Columns.TEXT, text);

        //insert comment
        Uri uri = context.getContentResolver().insert(TodayProviderContract.COMMENTS_URI, values);

        //start upload comment to the server
        Long recordId = Long.parseLong(uri.getLastPathSegment());
        NetworkService.uploadComment(context, recordId);
    }


    public static CursorLoader getFilmsFullTimetableLoader(Context context, long filmId, long timetableStartDate,
                                                           long timetableEndDate, String[] projection, String order) {
        //convert date to utc
        timetableStartDate = TimeUtils.toUtcDate(timetableStartDate);
        timetableEndDate = TimeUtils.toUtcDate(timetableEndDate);

        LOGD("KVEST_TAG", "getFilmsFullTimetableLoader : timetableStartDate=" + timetableStartDate + ", endDate=" + timetableEndDate);

        String selection = Tables.FilmsFullTimetable.Columns.FILM_ID + "=? AND " +
                           Tables.FilmsFullTimetable.Columns.DATE + ">= ? AND " +
                           Tables.FilmsFullTimetable.Columns.DATE + "<= ?";
        return new CursorLoader(context, FULL_TIMETABLE_URI, projection,
                                selection, new String[]{Long.toString(filmId), Long.toString(timetableStartDate), Long.toString(timetableEndDate)},
                                order);
    }

    public static CursorLoader getFilmsForPeriodLoader(Context context, long startDate, long endDate, String[] projection, String order) {
        //convert date to utc
        startDate = TimeUtils.toUtcDate(startDate);
        endDate = TimeUtils.toUtcDate(endDate);

LOGD("KVEST_TAG", "getFilmsForPeriodLoader : startDate=" + startDate + ", endDate=" + endDate);

        String selection = Tables.Films.Columns.FILM_ID + " in (" + Tables.FilmsTimetable.GET_FILMS_ID_BY_PERIOD_SQL + ")";
        return new CursorLoader(context, TodayProviderContract.FILMS_URI, projection, selection,
                                new String[]{Long.toString(startDate), Long.toString(endDate)}, order);
    }

    public static CursorLoader getFilmLoader(Context context, long filmId, String[] projection, String order) {
        String selection = Tables.Films.Columns.FILM_ID + "=?";
        return new CursorLoader(context, FILMS_URI, projection, selection, new String[]{Long.toString(filmId)}, order);
    }

    public static CursorLoader getCommentsLoader(Context context, long targetId, int targetType, String[] projection, String order) {
        int targetStatus = Constants.SyncStatus.UP_TO_DATE | Constants.SyncStatus.NEED_UPLOAD | Constants.SyncStatus.NEED_UPDATE;
        String selection = Tables.Comments.Columns.TARGET_ID + "=? AND " + Tables.Comments.Columns.TARGET_TYPE + "=? AND " +
                           "(" + Tables.Comments.Columns.SYNC_STATUS + " | " + targetStatus + " = " + targetStatus + ")";
        return new CursorLoader(context, TodayProviderContract.COMMENTS_URI, projection, selection,
                                new String[]{Long.toString(targetId), Integer.toString(targetType)}, order);
    }

    public static CursorLoader getCinemasLoader(Context context, String[] projection, String order) {
        return new CursorLoader(context, TodayProviderContract.CINEMAS_URI, projection, null, null, order);
    }
}