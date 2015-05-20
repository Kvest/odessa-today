package com.kvest.odessatoday.io.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.GetTimetableResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 23.07.14
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class GetTimetableRequest extends BaseRequest<GetTimetableResponse> {
    private static Gson gson = new Gson();

    private long filmId;

    public GetTimetableRequest(long filmId, Response.Listener<GetTimetableResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, NetworkContract.createTimetableUri(filmId).toString(), null, listener, errorListener);

        this.filmId = filmId;
    }

    @Override
    protected Response<GetTimetableResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetTimetableResponse getTimetableResponse  = gson.fromJson(json, GetTimetableResponse.class);

            return Response.success(getTimetableResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public long getFilmId() {
        return filmId;
    }
}
