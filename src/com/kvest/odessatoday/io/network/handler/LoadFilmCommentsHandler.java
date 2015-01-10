package com.kvest.odessatoday.io.network.handler;

import android.content.Context;
import android.content.Intent;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.network.notification.LoadCommentsNotification;
import com.kvest.odessatoday.io.network.request.GetFilmCommentsRequest;
import com.kvest.odessatoday.io.network.response.GetCommentsResponse;
import com.kvest.odessatoday.utils.Constants;

import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 10.01.2015.
 */
public class LoadFilmCommentsHandler extends LoadCommentsHandler {
    private static final String FILM_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.FILM_ID";

    public static void putExtras(Intent intent, long filmId) {
        intent.putExtra(FILM_ID_EXTRA, filmId);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        long filmId = intent.getLongExtra(FILM_ID_EXTRA, -1);

        RequestFuture<GetCommentsResponse> future = RequestFuture.newFuture();
        GetFilmCommentsRequest request = new GetFilmCommentsRequest(filmId, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetCommentsResponse response = future.get();
            if (response.isSuccessful()) {
                //save comments
                saveComments(context, response.data.comments, request.getTargetId(), request.getTargetType());

                //notify listeners about successful loading comments
                sendLocalBroadcast(context, LoadCommentsNotification.createSuccessResult(filmId, Constants.CommentTargetType.FILM));
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading comments
                sendLocalBroadcast(context, LoadCommentsNotification.createErrorsResult(response.error, filmId, Constants.CommentTargetType.FILM));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            sendLocalBroadcast(context, LoadCommentsNotification.createErrorsResult(e.getLocalizedMessage(), filmId, Constants.CommentTargetType.FILM));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            sendLocalBroadcast(context, LoadCommentsNotification.createErrorsResult(e.getLocalizedMessage(), filmId, Constants.CommentTargetType.FILM));
        }
    }
}
