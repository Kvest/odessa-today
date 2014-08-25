package com.kvest.odessatoday.io.request;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.Cinema;
import com.kvest.odessatoday.io.NetworkContract;
import com.kvest.odessatoday.io.response.GetCinemasResponse;
import com.kvest.odessatoday.utils.Constants;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.kvest.odessatoday.provider.TodayProviderContract.CINEMAS_URI;
import static com.kvest.odessatoday.provider.TodayProviderContract.CONTENT_AUTHORITY;
import static com.kvest.odessatoday.provider.TodayProviderContract.TIMETABLE_URI;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 16.06.14
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public class GetCinemasRequest extends BaseRequest<GetCinemasResponse> {
    private static Gson gson = new Gson();

    public GetCinemasRequest(Response.Listener<GetCinemasResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, NetworkContract.CinemasRequest.url.toString(), null, listener, errorListener);
    };

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
