package com.kvest.odessatoday.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.kvest.odessatoday.utils.Constants;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by kvest on 16.07.16.
 */
public class DBCleanerService extends IntentService {
    public static void start(Context context) {
        Intent intent = new Intent(context, DBCleanerService.class);
        context.startService(intent);
    }

    public DBCleanerService() {
        super("DBCleanerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        cleanFilms();
        cleanEvents();
    }

    private void cleanFilms() {
        ContentResolver contentResolver = getContentResolver();

        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        String filmsSelection = Tables.Films.Columns.FILM_ID + " = ?";
        String filmTimetableSelection = Tables.FilmsTimetable.Columns.FILM_ID + " = ?";
        String commentsSelection = Tables.Comments.Columns.TARGET_ID + " = ? AND " +
                Tables.Comments.Columns.TARGET_TYPE + " = " + Constants.TargetType.FILM + " AND " +
                Tables.Comments.Columns.SYNC_STATUS + " = " + Constants.SyncStatus.UP_TO_DATE;

        String selection = Tables.FilmsTimetable.Columns.DATE + " < " +
                           TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        Cursor cursor = contentResolver.query(FILM_TIMETABLE_URI, new String[]{Tables.FilmsTimetable.DISTINCT_FILM_ID}, selection, null, null);
        try {
            int filmIdIndex = cursor.getColumnIndex(Tables.FilmsTimetable.Columns.FILM_ID);
            while (cursor.moveToNext()) {
                String filmId = Long.toString(cursor.getLong(filmIdIndex));

                //add delete film operation
                operations.add(ContentProviderOperation.newDelete(FILMS_URI)
                        .withSelection(filmsSelection, new String[]{filmId})
                        .build());

                //add delete timetable operations
                operations.add(ContentProviderOperation.newDelete(FILM_TIMETABLE_URI)
                        .withSelection(filmTimetableSelection, new String[]{filmId})
                        .build());

                //add delete comments operation
                operations.add(ContentProviderOperation.newDelete(COMMENTS_URI)
                        .withSelection(commentsSelection, new String[]{filmId})
                        .build());
            }
        } finally {
            cursor.close();
        }

        //apply operations
        try {
            contentResolver.applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
        }
    }

    private void cleanEvents() {
        ContentResolver contentResolver = getContentResolver();

        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        String eventsSelection = Tables.Events.Columns.EVENT_ID + " = ?";
        String eventsTimetableSelection = Tables.EventsTimetable.Columns.EVENT_ID + " = ?";
        String commentsSelection = Tables.Comments.Columns.TARGET_ID + " = ? AND " +
                Tables.Comments.Columns.SYNC_STATUS + " = " + Constants.SyncStatus.UP_TO_DATE;

        String selection = Tables.EventsTimetable.Columns.DATE + " < " +
                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        Cursor cursor = contentResolver.query(EVENTS_TIMETABLE_URI, new String[]{Tables.EventsTimetable.DISTINCT_EVENT_ID}, selection, null, null);
        try {
            int eventIdIndex = cursor.getColumnIndex(Tables.EventsTimetable.Columns.EVENT_ID);
            while (cursor.moveToNext()) {
                String eventId = Long.toString(cursor.getLong(eventIdIndex));

                //add delete event operation
                operations.add(ContentProviderOperation.newDelete(EVENTS_URI)
                        .withSelection(eventsSelection, new String[]{eventId})
                        .build());

                //add delete timetable operations
                operations.add(ContentProviderOperation.newDelete(EVENTS_TIMETABLE_URI)
                        .withSelection(eventsTimetableSelection, new String[]{eventId})
                        .build());

                //add delete comments operation
                operations.add(ContentProviderOperation.newDelete(COMMENTS_URI)
                        .withSelection(commentsSelection, new String[]{eventId})
                        .build());
            }
        } finally {
            cursor.close();
        }

        //apply operations
        try {
            ContentProviderResult[] results = contentResolver.applyBatch(CONTENT_AUTHORITY, operations);
            for (ContentProviderResult result : results) {
                Log.d("KVEST_TAG", "res=" + result.toString());
            }
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
        }
    }
}
