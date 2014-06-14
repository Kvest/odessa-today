package com.kvest.odessatoday.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.GetTodayFilmsData;
import com.kvest.odessatoday.io.request.GetTodayFilmsRequest;
import com.kvest.odessatoday.io.response.GetTodayFilmsResponse;

import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 04.06.14
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
public class NetworkService extends IntentService {
    private static final String ACTION_EXTRA = "com.kvest.odessatoday.EXTRAS.ACTION";
    private static final int ACTION_LOAD_TODAY_FILMS = 0;

    public static void loadTodayFilms(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_LOAD_TODAY_FILMS);

        context.startService(intent);
    }

    public NetworkService() {
        super("NetworkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getIntExtra(ACTION_EXTRA, -1)) {
            case ACTION_LOAD_TODAY_FILMS :
                doLoadTodayFilms(intent);
                break;
        }
    }

    private void doLoadTodayFilms(Intent intent) {
        //send request
        RequestFuture<GetTodayFilmsResponse> future = RequestFuture.newFuture();
        GetTodayFilmsRequest request = new GetTodayFilmsRequest(future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetTodayFilmsResponse response = future.get();
            if (response.isSuccessful()) {
                //save data
                saveTodayFilms(response.data);

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

    private void saveTodayFilms(GetTodayFilmsData response) {
        //TODO
    }
}
