package com.kvest.odessatoday.io.network.handler;

import android.content.Context;
import android.content.Intent;

import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.network.request.GetPlacesRequest;
import com.kvest.odessatoday.io.network.response.GetPlacesResponse;
import com.kvest.odessatoday.utils.Constants;

import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by kvest on 16.09.15.
 */
public class LoadPlacesHandler extends RequestHandler {
    private static final String PLACES_TYPE_EXTRA = "com.kvest.odessatoday.EXTRAS.PLACES_TYPE";

    public static void putExtras(Intent intent, int placesType) {
        intent.putExtra(PLACES_TYPE_EXTRA, placesType);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        int placesType = intent.getIntExtra(PLACES_TYPE_EXTRA, -1);

        RequestFuture<GetPlacesResponse> future = RequestFuture.newFuture();
        GetPlacesRequest request = new GetPlacesRequest(placesType, 0 , 100, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetPlacesResponse response = future.get();

            //TODO
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //TODO
            //notify listeners about unsuccessful loading cinemas
            //sendLocalBroadcast(context, LoadCinemasNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //TODO
            //notify listeners about unsuccessful loading cinemas
            //sendLocalBroadcast(context, LoadCinemasNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }
}
