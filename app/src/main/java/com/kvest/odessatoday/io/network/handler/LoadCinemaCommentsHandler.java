package com.kvest.odessatoday.io.network.handler;

import android.content.Context;
import android.content.Intent;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.io.network.event.CommentsLoadedEvent;
import com.kvest.odessatoday.io.network.request.GetCinemaCommentsRequest;
import com.kvest.odessatoday.io.network.request.GetCommentsRequest;
import com.kvest.odessatoday.io.network.response.GetCommentsResponse;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created by Kvest on 10.01.2015.
 */
public class LoadCinemaCommentsHandler extends LoadCommentsHandler {
    private static final String CINEMA_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.CINEMA_ID";

    public static void putExtras(Intent intent, long cinemaId) {
        intent.putExtra(CINEMA_ID_EXTRA, cinemaId);
    }

    @Override
    protected GetCommentsRequest createRequest(Intent intent, int offset, int limit, RequestFuture<GetCommentsResponse> future) {
        //get extra data
        long cinemaId = intent.getLongExtra(CINEMA_ID_EXTRA, -1);

        GetCinemaCommentsRequest request = new GetCinemaCommentsRequest(cinemaId, offset, limit, future, future);
        return request;
    }

    @Override
    protected void notifyError(Context context, String message, Intent intent) {
        //get extra data
        long cinemaId = intent.getLongExtra(CINEMA_ID_EXTRA, -1);

        BusProvider.getInstance().post(new CommentsLoadedEvent(cinemaId, Constants.CommentTargetType.CINEMA, message));
    }

    @Override
    protected void notifySuccess(Context context, Intent intent) {
        //get extra data
        long cinemaId = intent.getLongExtra(CINEMA_ID_EXTRA, -1);

        BusProvider.getInstance().post(new CommentsLoadedEvent(cinemaId, Constants.CommentTargetType.CINEMA));
    }
}
