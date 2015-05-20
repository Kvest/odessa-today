package com.kvest.odessatoday.io.network.handler;

import android.content.Context;
import android.content.Intent;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.io.network.notification.LoadCommentsNotification;
import com.kvest.odessatoday.io.network.request.GetCommentsRequest;
import com.kvest.odessatoday.io.network.request.GetFilmCommentsRequest;
import com.kvest.odessatoday.io.network.response.GetCommentsResponse;
import com.kvest.odessatoday.utils.Constants;


/**
 * Created by Kvest on 10.01.2015.
 */
public class LoadFilmCommentsHandler extends LoadCommentsHandler {
    private static final String FILM_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.FILM_ID";

    public static void putExtras(Intent intent, long filmId) {
        intent.putExtra(FILM_ID_EXTRA, filmId);
    }

    @Override
    protected GetCommentsRequest createRequest(Intent intent, int offset, int limit, RequestFuture<GetCommentsResponse> future) {
        //get extra data
        long filmId = intent.getLongExtra(FILM_ID_EXTRA, -1);

        GetFilmCommentsRequest request = new GetFilmCommentsRequest(filmId, offset, limit, future, future);
        return request;
    }

    @Override
    protected void notifyError(Context context, String message, Intent intent) {
        //get extra data
        long filmId = intent.getLongExtra(FILM_ID_EXTRA, -1);

        sendLocalBroadcast(context, LoadCommentsNotification.createErrorsResult(message, filmId, Constants.CommentTargetType.FILM));
    }

    @Override
    protected void notifySuccess(Context context, Intent intent) {
        //get extra data
        long filmId = intent.getLongExtra(FILM_ID_EXTRA, -1);

        sendLocalBroadcast(context, LoadCommentsNotification.createSuccessResult(filmId, Constants.CommentTargetType.FILM));
    }
}
