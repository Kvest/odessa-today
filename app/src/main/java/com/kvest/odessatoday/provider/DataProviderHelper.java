package com.kvest.odessatoday.provider;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.support.v4.content.CursorLoader;
import android.net.Uri;
import android.text.TextUtils;

import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.utils.Constants;

import java.util.ArrayList;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 31.07.14
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class DataProviderHelper {
    public static void addComment(Context context, int targetType, long targetId, String name,
                                  String text, float rating,  long date) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(rating > 0 ? 2 : 1);

        //fill ContentValues
        ContentValues values = new ContentValues(6);
        values.put(Tables.Comments.Columns.TARGET_ID, targetId);
        values.put(Tables.Comments.Columns.TARGET_TYPE, targetType);
        values.put(Tables.Comments.Columns.SYNC_STATUS, Constants.SyncStatus.NEED_UPLOAD);
        values.put(Tables.Comments.Columns.DATE, date);
        values.put(Tables.Comments.Columns.NAME, name);
        values.put(Tables.Comments.Columns.TEXT, text);

        //store comment
        operations.add(ContentProviderOperation.newInsert(TodayProviderContract.COMMENTS_URI).withValues(values).build());

        //rating
        if (rating > 0) {
            ContentProviderOperation operation = ContentProviderOperation.newInsert(TodayProviderContract.COMMENTS_RATING_URI)
                                                        .withValueBackReference(Tables.CommentsRating.Columns.COMMENT_ID, 0)
                                                        .withValue(Tables.CommentsRating.Columns.TARGET_ID, targetId)
                                                        .withValue(Tables.CommentsRating.Columns.RATING, rating)
                    .build();
            operations.add(operation);
        }

        //apply
        try {
            ContentProviderResult[] result = context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);

            //start upload comment to the server
            Long recordId = Long.parseLong(result[0].uri.getLastPathSegment());
            NetworkService.uploadComment(context, recordId);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
        }
    }

    public static CursorLoader getFilmsFullTimetableLoader(Context context, long filmId, long timetableStartDate,
                                                           long timetableEndDate, String[] projection, String order) {
        String selection = Tables.FilmsFullTimetableView.Columns.FILM_ID + "=? AND " +
                           Tables.FilmsFullTimetableView.Columns.DATE + ">= ? AND " +
                           Tables.FilmsFullTimetableView.Columns.DATE + "<= ?";
        return new CursorLoader(context, FULL_TIMETABLE_URI, projection,
                                selection, new String[]{Long.toString(filmId), Long.toString(timetableStartDate), Long.toString(timetableEndDate)},
                                order);
    }

    public static CursorLoader getCinemaTimetableLoader(Context context, long cinemaId, long timetableStartDate,
                                                           long timetableEndDate, String[] projection, String order) {
        String selection = Tables.CinemaTimetableView.Columns.CINEMA_ID + "=? AND " +
                           Tables.CinemaTimetableView.Columns.DATE + ">= ? AND " +
                           Tables.CinemaTimetableView.Columns.DATE + "<= ?";
        return new CursorLoader(context, CINEMA_TIMETABLE_URI, projection,
                                selection, new String[]{Long.toString(cinemaId), Long.toString(timetableStartDate), Long.toString(timetableEndDate)},
                                order);
    }

    public static CursorLoader getFilmsForPeriodLoader(Context context, long startDate, long endDate,
                                                       String filter, String[] projection) {
        String selection = Tables.Films.Columns.FILM_ID + " in (" + Tables.FilmsTimetable.GET_FILMS_ID_BY_PERIOD_SQL + ")";
        if (!TextUtils.isEmpty(filter)) {
            selection += " AND " + Tables.Films.Columns.NAME + " LIKE \'%" + filter + "%\'";
        }

        return new CursorLoader(context, TodayProviderContract.FILMS_URI, projection, selection,
                                new String[]{Long.toString(startDate), Long.toString(endDate)}, null);
    }

    public static CursorLoader getFilmLoader(Context context, long filmId, String[] projection) {
        String selection = Tables.Films.Columns.FILM_ID + "=?";
        return new CursorLoader(context, FILMS_URI, projection, selection, new String[]{Long.toString(filmId)}, null);
    }

    public static CursorLoader getCommentsLoader(Context context, long targetId, int targetType, String[] projection, String order) {
        int targetStatus = Constants.SyncStatus.UP_TO_DATE | Constants.SyncStatus.NEED_UPLOAD | Constants.SyncStatus.NEED_UPDATE;
        String selection = Tables.Comments.Columns.TARGET_ID + "=? AND " + Tables.Comments.Columns.TARGET_TYPE + "=? AND " +
                           "(" + Tables.Comments.Columns.SYNC_STATUS + " | " + targetStatus + " = " + targetStatus + ")";
        return new CursorLoader(context, TodayProviderContract.COMMENTS_URI, projection, selection,
                                new String[]{Long.toString(targetId), Integer.toString(targetType)}, order);
    }

    public static CursorLoader getCinemasLoader(Context context, String[] projection, String order) {
        return new CursorLoader(context, CINEMAS_URI, projection, null, null, order);
    }

    public static CursorLoader getPlacesLoader(Context context, int placeType, String[] projection) {
        Uri uri = Uri.withAppendedPath(PLACES_URI, Integer.toString(placeType));
        return new CursorLoader(context, uri, projection, null, null, null);
    }

    public static CursorLoader getFullEventsForPeriodLoader(Context context, int eventType, long startDate, long endDate, String[] projection, String order) {
        String selection = Tables.EventsTimetableView.Columns.DATE + ">=? AND "
                           + Tables.EventsTimetableView.Columns.DATE + "<=? AND "
                           + Tables.EventsTimetableView.Columns.EVENT_TYPE + "=?";
        return new CursorLoader(context, TodayProviderContract.EVENTS_TIMETABLE_VIEW_URI, projection, selection,
                                new String[]{Long.toString(startDate), Long.toString(endDate), Integer.toString(eventType)}, order);
    }

    public static CursorLoader getFullEventsAnnouncementsLoader(Context context, int eventType, long startDate, String[] projection, String order) {
        String selection = Tables.EventsTimetableView.Columns.DATE + ">=? AND "
                + Tables.EventsTimetableView.Columns.EVENT_TYPE + "=?";
        return new CursorLoader(context, TodayProviderContract.EVENTS_TIMETABLE_VIEW_URI, projection, selection,
                new String[]{Long.toString(startDate), Integer.toString(eventType)}, order);
    }

    public static CursorLoader getCinemaLoader(Context context, long cinemaId, String[] projection) {
        String selection = Tables.Cinemas.Columns.CINEMA_ID + "=?";
        return new CursorLoader(context, CINEMAS_URI, projection, selection, new String[]{Long.toString(cinemaId)}, null);
    }

    public static CursorLoader getAnnouncementFilmsLoader(Context context, String[] projection, String order) {
        return new CursorLoader(context, ANNOUNCEMENT_FILMS_VIEW_URI, projection, null, null, order);
    }

    public static CursorLoader getAnnouncementFilmLoader(Context context, long filmId, String[] projection) {
        String selection = Tables.AnnouncementFilmsView.Columns.FILM_ID + "=?";
        String[] selectionArgs = new String[]{Long.toString(filmId)};
        return new CursorLoader(context, ANNOUNCEMENT_FILMS_VIEW_URI, projection, selection, selectionArgs, null);
    }

    public static CursorLoader getEventLoader(Context context, long eventId, String[] projection) {
        String selection = Tables.Events.Columns.EVENT_ID + "=?";
        return new CursorLoader(context, EVENTS_URI, projection, selection, new String[]{Long.toString(eventId)}, null);
    }

    public static CursorLoader getEventTimetableWithTicketsLoader(Context context, long eventId,
                                                                  long startDate, String[] projection) {
        String selection = Tables.EventsTimetable.Columns.EVENT_ID + "=? AND " +
                           Tables.EventsTimetable.Columns.DATE + ">=? AND " +
                           Tables.EventsTimetable.Columns.HAS_TICKETS + "=?";
        String[] selectionArgs = new String[]{ Long.toString(eventId), Long.toString(startDate), Integer.toString(1)};
        return new CursorLoader(context, EVENTS_TIMETABLE_URI, projection, selection, selectionArgs, null);
    }

    public static CursorLoader getEventTimetableLoader(Context context, long eventId, long startDate,
                                                       String[] projection, String order) {
        String selection = Tables.EventsTimetable.Columns.EVENT_ID + "=? AND " +
                           Tables.EventsTimetable.Columns.DATE + ">=?";
        String[] selectionArgs = new String[]{ Long.toString(eventId), Long.toString(startDate)};
        return new CursorLoader(context, EVENTS_TIMETABLE_URI, projection, selection, selectionArgs, order);
    }

    public static CursorLoader getPlaceLoader(Context context, int placeType, long placeId, String[] projection) {
        Uri uri = Uri.withAppendedPath(PLACES_URI, Integer.toString(placeType));
        String selection = Tables.Places.Columns.PLACE_ID + "=?";
        return new CursorLoader(context, uri, projection, selection, new String[]{Long.toString(placeId)}, null);
    }

    public static CursorLoader getPlaceTimetableLoader(Context context, long placeId, long startDate,
                                                       String[] projection, String sortOrder) {
        String selection = Tables.EventsTimetableView.Columns.PLACE_ID + "=? AND " +
                           Tables.EventsTimetableView.Columns.DATE + ">=?";
        String[] selectionArgs = new String[]{Long.toString(placeId),  Long.toString(startDate)};
        return new CursorLoader(context, EVENTS_TIMETABLE_VIEW_URI, projection, selection, selectionArgs, sortOrder);
    }
}
