package com.kvest.odessatoday.io.network.request;

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

    public GetFilmCommentsRequest(long filmId,Response.Listener<GetCommentsResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(NetworkContract.createFilmCommentsUri(filmId).toString(), listener, errorListener);

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
}
