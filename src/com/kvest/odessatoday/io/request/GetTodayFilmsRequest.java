package com.kvest.odessatoday.io.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.io.NetworkContract;
import com.kvest.odessatoday.io.response.GetTodayFilmsResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class GetTodayFilmsRequest extends BaseRequest<GetTodayFilmsResponse> {
    private static Gson gson = new Gson();

    public GetTodayFilmsRequest(Response.Listener<GetTodayFilmsResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, NetworkContract.FilmsTodayRequest.Url, null, listener, errorListener);
    };

    @Override
    protected Response<GetTodayFilmsResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetTodayFilmsResponse getPostResponse = gson.fromJson(json, GetTodayFilmsResponse.class);

            return Response.success(getPostResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

}
