package com.kvest.odessatoday.provider;

import android.content.Context;
import android.content.CursorLoader;
import android.util.Log;
import com.kvest.odessatoday.utils.TimeUtils;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 31.07.14
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class CursorLoaderBuilder {
    public static CursorLoader getFilmsFullTimetable(Context context, long filmId, String[] projection, String order) {
        String selection = Tables.FilmsFullTimetable.Columns.FILM_ID + "=?";
        return new CursorLoader(context, FULL_TIMETABLE_URI, projection,
                                selection, new String[]{Long.toString(filmId)}, order);
    }

    public static CursorLoader getFilmsForPeriod(Context context, long startDate, long endDate, String[] projection, String order) {
        //convert date to utc
        startDate = TimeUtils.toUtcDate(startDate);
        endDate = TimeUtils.toUtcDate(endDate);

Log.d("KVEST_TAG", "getFilmsForPeriod : startDate=" + startDate + ", endDate=" + endDate);

        String selection = Tables.Films.Columns.FILM_ID + " in (" + Tables.FilmsTimetable.GET_FILMS_ID_BY_PERIOD_SQL + ")";
        return new CursorLoader(context, TodayProviderContract.FILMS_URI, projection, selection,
                                new String[]{Long.toString(startDate), Long.toString(endDate)}, order);
    }

    public static CursorLoader getFilm(Context context, long filmId, String[] projection, String order) {
        String selection = Tables.Films.Columns.FILM_ID + "=?";
        return new CursorLoader(context, FILMS_URI, projection, selection, new String[]{Long.toString(filmId)}, order);
    }

    public static CursorLoader getComments(Context context, long targetId, int targetType, String[] projection, String order) {
        String selection = Tables.Comments.Columns.TARGET_ID + "=? AND " + Tables.Comments.Columns.TARGET_TYPE + "=?";
        return new CursorLoader(context, TodayProviderContract.COMMENTS_URI, projection, selection,
                                new String[]{Long.toString(targetId), Integer.toString(targetType)}, order);
    }
}
