package com.kvest.odessatoday.io.network.request;

import android.net.Uri;
import com.android.volley.Response;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.GetCommentsResponse;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 29.07.14
 * Time: 23:32
 * To change this template use File | Settings | File Templates.
 */
public class GetCinemaCommentsRequest extends GetCommentsRequest {
    private long cinemaId;

    public GetCinemaCommentsRequest(long cinemaId, int offset, int limit, Response.Listener<GetCommentsResponse> listener,
                                    Response.ErrorListener errorListener) {
        super(generateUrl(cinemaId, offset, limit).toString(), listener, errorListener);

        this.cinemaId = cinemaId;
    }

    @Override
    public long getTargetId() {
        return cinemaId;
    }

    @Override
    public int getTargetType() {
        return Constants.CommentTargetType.CINEMA;
    }

    private static String generateUrl(long cinemaId, int offset, int limit) {
        Uri.Builder builder = NetworkContract.createCinemaCommentsUri(cinemaId).buildUpon();

        if (offset >= 0) {
            builder.appendQueryParameter(NetworkContract.CinemaCommentsRequest.Params.OFFSET, Integer.toString(offset));
        }
        if (limit >= 0) {
            builder.appendQueryParameter(NetworkContract.CinemaCommentsRequest.Params.LIMIT, Integer.toString(limit));
        }

        return builder.build().toString();
    }
}
