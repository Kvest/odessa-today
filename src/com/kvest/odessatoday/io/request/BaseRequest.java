package com.kvest.odessatoday.io.request;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonRequest;
import com.kvest.odessatoday.io.response.BaseResponse;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseRequest<T> extends JsonRequest<T> {
    public BaseRequest(String url, String requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(url, requestBody, listener, errorListener);
    }

    public BaseRequest(int method, String url, String requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    //TODO add api key here
}
