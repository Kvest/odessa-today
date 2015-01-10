package com.kvest.odessatoday.io.network.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.FilmWithTimetable;
import com.kvest.odessatoday.datamodel.TimetableItem;
import com.kvest.odessatoday.io.network.notification.LoadFilmsNotification;
import com.kvest.odessatoday.io.network.request.GetFilmsRequest;
import com.kvest.odessatoday.io.network.response.GetFilmsResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.provider.TodayProviderContract.CONTENT_AUTHORITY;
import static com.kvest.odessatoday.provider.TodayProviderContract.FILMS_URI;
import static com.kvest.odessatoday.provider.TodayProviderContract.TIMETABLE_URI;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 10.01.2015.
 */
public class LoadFilmsHandler extends RequestHandler {
    private static final String START_DATE_EXTRA = "com.kvest.odessatoday.EXTRAS.START_DATE";
    private static final String END_DATE_EXTRA = "com.kvest.odessatoday.EXTRAS.END_DATE";
    private static final String CINEMA_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.CINEMA_ID";

    public static void putExtras(Intent intent, long startDate, long endDate, long cinemaId) {
        intent.putExtra(START_DATE_EXTRA, startDate);
        intent.putExtra(END_DATE_EXTRA, endDate);
        intent.putExtra(CINEMA_ID_EXTRA, cinemaId);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        long startDate = intent.getLongExtra(START_DATE_EXTRA, -1);
        long endDate = intent.getLongExtra(END_DATE_EXTRA, -1);
        long cinemaId = intent.getLongExtra(CINEMA_ID_EXTRA, -1);

        //send request
        RequestFuture<GetFilmsResponse> future = RequestFuture.newFuture();
        GetFilmsRequest request = new GetFilmsRequest(startDate, endDate, cinemaId, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetFilmsResponse response = future.get();
            if (response.isSuccessful()) {
                //save films
                saveFilms(context, response.data.films, request.getStartDate(), request.getEndDate());

                //notify listeners about successful loading films
                sendLocalBroadcast(context, LoadFilmsNotification.createSuccessResult());

                //update cinemas
                NetworkService.loadCinemas(context);
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading films
                sendLocalBroadcast(context, LoadFilmsNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading films
            sendLocalBroadcast(context, LoadFilmsNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading films
            sendLocalBroadcast(context, LoadFilmsNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }

    private void saveFilms(Context context, List<FilmWithTimetable> films, long startDate, long endDate) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        //delete timetable from startDate to endDate
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(TIMETABLE_URI)
                .withSelection(TodayProviderContract.Tables.FilmsTimetable.Columns.DATE + ">=? AND " + TodayProviderContract.Tables.FilmsTimetable.Columns.DATE + "<=?",
                        new String[]{Long.toString(startDate), Long.toString(endDate)})
                .build();
        operations.add(deleteOperation);

        for (FilmWithTimetable film : films) {
            //insert film
            operations.add(ContentProviderOperation.newInsert(FILMS_URI).withValues(film.getContentValues()).build());

            //insert timetable
            for (TimetableItem timetableItem : film.timetable) {
                operations.add(ContentProviderOperation.newInsert(TIMETABLE_URI).withValues(timetableItem.getContentValues(film.id)).build());
            }
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
