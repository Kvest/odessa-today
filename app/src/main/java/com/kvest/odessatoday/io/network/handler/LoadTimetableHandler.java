package com.kvest.odessatoday.io.network.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.TimetableItem;
import com.kvest.odessatoday.io.network.notification.LoadTimetableNotification;
import com.kvest.odessatoday.io.network.request.GetTimetableRequest;
import com.kvest.odessatoday.io.network.response.GetTimetableResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.provider.TodayProviderContract.CONTENT_AUTHORITY;
import static com.kvest.odessatoday.provider.TodayProviderContract.FILM_TIMETABLE_URI;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 10.01.2015.
 */
public class LoadTimetableHandler extends RequestHandler {
    private static final String FILM_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.FILM_ID";

    public static void putExtras(Intent intent, long filmId) {
        intent.putExtra(FILM_ID_EXTRA, filmId);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        long filmId = intent.getLongExtra(FILM_ID_EXTRA, -1);

        RequestFuture<GetTimetableResponse> future = RequestFuture.newFuture();
        GetTimetableRequest request = new GetTimetableRequest(filmId, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetTimetableResponse response = future.get();
            if (response.isSuccessful()) {
                //save timetable
                saveTimetable(context, response.data.timetable, request.getFilmId());

                //notify listeners about successful loading timetable
                sendLocalBroadcast(context, LoadTimetableNotification.createSuccessResult());
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading timetable
                sendLocalBroadcast(context, LoadTimetableNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading timetable
            sendLocalBroadcast(context, LoadTimetableNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading timetable
            sendLocalBroadcast(context, LoadTimetableNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }

    private void saveTimetable(Context context, List<TimetableItem> timetable, long filmId) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        //delete timetable for film with filmId
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(FILM_TIMETABLE_URI)
                .withSelection(TodayProviderContract.Tables.FilmsTimetable.Columns.FILM_ID + "=?", new String[]{Long.toString(filmId)})
                .build();
        operations.add(deleteOperation);

        //insert timetable items
        for (TimetableItem timetableItem : timetable) {
            operations.add(ContentProviderOperation.newInsert(FILM_TIMETABLE_URI).withValues(timetableItem.getContentValues(filmId)).build());
        }

        //apply
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
            re.printStackTrace();
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
            oae.printStackTrace();
        }
    }
}
