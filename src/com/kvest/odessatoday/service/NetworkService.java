package com.kvest.odessatoday.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.notification.LoadCinemasNotification;
import com.kvest.odessatoday.io.notification.LoadFilmsNotification;
import com.kvest.odessatoday.io.request.GetCinemasRequest;
import com.kvest.odessatoday.io.request.GetFilmsRequest;
import com.kvest.odessatoday.io.response.GetCinemasResponse;
import com.kvest.odessatoday.io.response.GetFilmsResponse;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 04.06.14
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
public class NetworkService extends IntentService {

    private static final String ACTION_EXTRA = "com.kvest.odessatoday.EXTRAS.ACTION";
    private static final String START_DATE_EXTRA = "com.kvest.odessatoday.EXTRAS.START_DATE";
    private static final String END_DATE_EXTRA = "com.kvest.odessatoday.EXTRAS.END_DATE";
    private static final int ACTION_LOAD_FILMS = 0;
    private static final int ACTION_LOAD_CINEMAS = 1;

    public static void loadTodayFilms(Context context) {
        //calculate start and end date
        long startDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        long endDate = Utils.getEndOfTheDay(startDate);

        loadFilms(context, startDate, endDate);
    }

    public static void loadFilms(Context context, long startDate, long endDate) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_LOAD_FILMS);
        intent.putExtra(START_DATE_EXTRA, startDate);
        intent.putExtra(END_DATE_EXTRA, endDate);

        context.startService(intent);
    }

    public static void loadCinemas(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_LOAD_CINEMAS);

        context.startService(intent);
    }

    public NetworkService() {
        super("NetworkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getIntExtra(ACTION_EXTRA, -1)) {
            case ACTION_LOAD_FILMS :
                doLoadFilms(intent);
                break;
            case ACTION_LOAD_CINEMAS :
                doLoadCinemas(intent);
                break;
        }
    }

    private void doLoadCinemas(Intent  intent) {
        RequestFuture<GetCinemasResponse> future = RequestFuture.newFuture();
        GetCinemasRequest request = new GetCinemasRequest(future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetCinemasResponse response = future.get();
            if (response.isSuccessful()) {
                //notify listeners about successful loading cinemas
                sendLocalBroadcast(LoadCinemasNotification.createSuccessResult());
            } else {
                Log.e(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading cinemas
                sendLocalBroadcast(LoadCinemasNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading cinemas
            sendLocalBroadcast(LoadCinemasNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading cinemas
            sendLocalBroadcast(LoadCinemasNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }

    private void doLoadFilms(Intent intent) {
        //get extra data
        long startDate = intent.getLongExtra(START_DATE_EXTRA, -1);
        long endDate = intent.getLongExtra(END_DATE_EXTRA, -1);

        //send request
        RequestFuture<GetFilmsResponse> future = RequestFuture.newFuture();
        GetFilmsRequest request = new GetFilmsRequest(startDate, endDate, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetFilmsResponse response = future.get();
            if (response.isSuccessful()) {
                //notify listeners about successful loading films
                sendLocalBroadcast(LoadFilmsNotification.createSuccessResult());

                //update cinemas
                NetworkService.loadCinemas(this);
            } else {
                Log.e(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading films
                sendLocalBroadcast(LoadFilmsNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading films
            sendLocalBroadcast(LoadFilmsNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading films
            sendLocalBroadcast(LoadFilmsNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }

    private void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(NetworkService.this).sendBroadcast(intent);
    }
}
