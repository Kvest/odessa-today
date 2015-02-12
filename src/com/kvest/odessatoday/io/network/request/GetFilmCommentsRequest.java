package com.kvest.odessatoday.io.network.request;

import android.net.Uri;
import com.android.volley.Response;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.GetCommentsResponse;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 28.07.14
 * Time: 23:56
 * To change this template use File | Settings | File Templates.
 */
public class GetFilmCommentsRequest extends GetCommentsRequest {
    private long filmId;

    public GetFilmCommentsRequest(long filmId, int offset, int limit, Response.Listener<GetCommentsResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(generateUrl(filmId, offset, limit).toString(), listener, errorListener);

        this.filmId = filmId;
    }

    @Override
    public long getTargetId() {
        return filmId;
    }

    @Override
    public int getTargetType() {
        return Constants.CommentTargetType.FILM;
    }

    private static String generateUrl(long filmId, int offset, int limit) {
        Uri.Builder builder = NetworkContract.createFilmCommentsUri(filmId).buildUpon();

        if (offset >= 0) {
            builder.appendQueryParameter(NetworkContract.FilmCommentsRequest.Params.OFFSET, Integer.toString(offset));
        }
        if (limit >= 0) {
            builder.appendQueryParameter(NetworkContract.FilmCommentsRequest.Params.LIMIT, Integer.toString(limit));
        }

        return builder.build().toString();
    }
}
