package com.kvest.odessatoday.io.network.request;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.network.NetworkContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseRequest<T> extends JsonRequest<T> {
    protected static Gson gson = new Gson();

    public BaseRequest(String url, String requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(url, requestBody, listener, errorListener);
    }

    public BaseRequest(int method, String url, String requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    @Override
    public Map getHeaders() {
        //add API key
        Map headers = new HashMap();
        headers.put(NetworkContract.API_KEY_HEADER_NAME, TodayApplication.getApplication().getTodayApiKey());
        headers.put(NetworkContract.CLIENT_ID_HEADER_NAME, TodayApplication.getApplication().getClientId());
        return headers;
    }
}
