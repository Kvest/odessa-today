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
import com.kvest.odessatoday.datamodel.Comment;
import com.kvest.odessatoday.io.response.GetCommentsResponse;
import com.kvest.odessatoday.utils.Constants;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 28.07.14
 * Time: 23:50
 * To change this template use File | Settings | File Templates.
 */
public abstract class GetCommentsRequest extends BaseRequest<GetCommentsResponse> {
    private static Gson gson = new Gson();

    public GetCommentsRequest(String url, Response.Listener<GetCommentsResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);
    }

    @Override
    protected Response<GetCommentsResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetCommentsResponse getCommentsResponse  = gson.fromJson(json, GetCommentsResponse.class);

            return Response.success(getCommentsResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public abstract long getTargetId();
    public abstract int getTargetType();
}