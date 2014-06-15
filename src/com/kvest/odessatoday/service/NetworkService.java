package com.kvest.odessatoday.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.request.GetFilmsRequest;
import com.kvest.odessatoday.io.response.GetFilmsResponse;
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

    public NetworkService() {
        super("NetworkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getIntExtra(ACTION_EXTRA, -1)) {
            case ACTION_LOAD_FILMS :
                doLoadFilms(intent);
                break;
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
                //TODO
            } else {
                //TODO
            }
        } catch (InterruptedException e) {
            //TODO
        } catch (ExecutionException e) {
            //TODO;
        }
    }
}
