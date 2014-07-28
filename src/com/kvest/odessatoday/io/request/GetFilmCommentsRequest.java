package com.kvest.odessatoday.io.request;

import com.android.volley.Response;
import com.kvest.odessatoday.io.NetworkContract;
import com.kvest.odessatoday.io.response.GetCommentsResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 28.07.14
 * Time: 23:56
 * To change this template use File | Settings | File Templates.
 */
public class GetFilmCommentsRequest extends GetCommentsRequest {
    public GetFilmCommentsRequest(long filmId,Response.Listener<GetCommentsResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(NetworkContract.createFilmCommentsUri(filmId).toString(), listener, errorListener);
    }
}
