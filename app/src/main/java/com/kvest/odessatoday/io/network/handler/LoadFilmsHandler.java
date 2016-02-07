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
import com.kvest.odessatoday.io.network.event.FilmsLoadedEvent;
import com.kvest.odessatoday.io.network.request.GetFilmsRequest;
import com.kvest.odessatoday.io.network.response.GetFilmsResponse;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.SelectionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 10.01.2015.
 */
public class LoadFilmsHandler extends RequestHandler {
    private static final long EMPTY_CINEMA_ID = -1;
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
        long cinemaId = intent.getLongExtra(CINEMA_ID_EXTRA, EMPTY_CINEMA_ID);

        //send request
        RequestFuture<GetFilmsResponse> future = RequestFuture.newFuture();
        GetFilmsRequest request = new GetFilmsRequest(startDate, endDate, cinemaId, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetFilmsResponse response = future.get();
            if (response.isSuccessful()) {
                //save films
                saveFilms(context, response.data.films, request.getStartDate(), request.getEndDate(), request.getCinemaId());

                //notify listeners about successful loading films
                BusProvider.getInstance().post(new FilmsLoadedEvent(true, null));

                //update cinemas
                NetworkService.loadCinemas(context);
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading films
                BusProvider.getInstance().post(new FilmsLoadedEvent(false, response.error));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading films
            BusProvider.getInstance().post(new FilmsLoadedEvent(false, e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading films
            BusProvider.getInstance().post(new FilmsLoadedEvent(false, e.getLocalizedMessage()));
        }
    }

    private void saveFilms(Context context, List<FilmWithTimetable> films, long startDate, long endDate, long cinemaId) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        //delete timetable from startDate to endDate
        SelectionBuilder selectionBuilder = new SelectionBuilder();
        if (startDate >= 0) {
            selectionBuilder.and(Tables.FilmsTimetable.Columns.DATE + ">=?", Long.toString(startDate));
        }
        if (endDate >= 0) {
            selectionBuilder.and(Tables.FilmsTimetable.Columns.DATE + "<=?", Long.toString(endDate));
        }
        if (cinemaId != EMPTY_CINEMA_ID) {
            selectionBuilder.and(Tables.FilmsTimetable.Columns.CINEMA_ID + "=?", Long.toString(cinemaId));
        }
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(FILM_TIMETABLE_URI)
                .withSelection(selectionBuilder.getSelection(), selectionBuilder.getSelectionArgs())
                .build();
        operations.add(deleteOperation);

        for (FilmWithTimetable film : films) {
            //insert film
            operations.add(ContentProviderOperation.newInsert(FILMS_URI).withValues(film.getContentValues()).build());

            //insert timetable
            for (TimetableItem timetableItem : film.timetable) {
                operations.add(ContentProviderOperation.newInsert(FILM_TIMETABLE_URI).withValues(timetableItem.getContentValues(film.id)).build());
            }
        }

        //apply
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
        }
    }
}
