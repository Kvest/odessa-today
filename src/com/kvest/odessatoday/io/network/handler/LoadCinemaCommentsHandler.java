package com.kvest.odessatoday.io.network.handler;

import android.content.Context;
import android.content.Intent;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.network.notification.LoadCommentsNotification;
import com.kvest.odessatoday.io.network.request.GetCinemaCommentsRequest;
import com.kvest.odessatoday.io.network.response.GetCommentsResponse;
import com.kvest.odessatoday.utils.Constants;

import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 10.01.2015.
 */
public class LoadCinemaCommentsHandler extends LoadCommentsHandler {
    private static final String CINEMA_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.CINEMA_ID";

    public static void putExtras(Intent intent, long cinemaId) {
        intent.putExtra(CINEMA_ID_EXTRA, cinemaId);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        long cinemaId = intent.getLongExtra(CINEMA_ID_EXTRA, -1);

        RequestFuture<GetCommentsResponse> future = RequestFuture.newFuture();
        GetCinemaCommentsRequest request = new GetCinemaCommentsRequest(cinemaId, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetCommentsResponse response = future.get();
            if (response.isSuccessful()) {
                //save comments
                saveComments(context, response.data.comments, request.getTargetId(), request.getTargetType());

                //notify listeners about successful loading comments
                sendLocalBroadcast(context, LoadCommentsNotification.createSuccessResult(cinemaId, Constants.CommentTargetType.CINEMA));
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading comments
                sendLocalBroadcast(context, LoadCommentsNotification.createErrorsResult(response.error, cinemaId, Constants.CommentTargetType.CINEMA));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            sendLocalBroadcast(context, LoadCommentsNotification.createErrorsResult(e.getLocalizedMessage(), cinemaId, Constants.CommentTargetType.CINEMA));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            sendLocalBroadcast(context, LoadCommentsNotification.createErrorsResult(e.getLocalizedMessage(), cinemaId, Constants.CommentTargetType.CINEMA));
        }
    }
}
