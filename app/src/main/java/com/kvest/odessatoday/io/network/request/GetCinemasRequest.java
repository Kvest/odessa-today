package com.kvest.odessatoday.io.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.GetCinemasResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 16.06.14
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public class GetCinemasRequest extends BaseRequest<GetCinemasResponse> {
    public GetCinemasRequest(Response.Listener<GetCinemasResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, NetworkContract.CinemasRequest.url.toString(), null, listener, errorListener);
    }

    @Override
    protected Response<GetCinemasResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetCinemasResponse cinemasResponse = gson.fromJson(json, GetCinemasResponse.class);

            return Response.success(cinemasResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
