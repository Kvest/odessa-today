package com.kvest.odessatoday.io.network.request;

import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.GetPlacesResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created by kvest on 16.09.15.
 */
public class GetPlacesRequest extends BaseRequest<GetPlacesResponse> {
    private static Gson gson = new Gson();

    public GetPlacesRequest(int type, int offset, int limit,
                            Response.Listener<GetPlacesResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, generateUrl(type, offset, limit).toString(), null, listener, errorListener);
    }

    @Override
    protected Response<GetPlacesResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetPlacesResponse placesResponse = gson.fromJson(json, GetPlacesResponse.class);

            return Response.success(placesResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private static String generateUrl(int type, int offset, int limit) {
        Uri.Builder builder = NetworkContract.PlacesRequest.url.buildUpon();

        builder.appendQueryParameter(NetworkContract.PlacesRequest.Params.TYPE, Integer.toString(type));
        if (offset >= 0) {
            builder.appendQueryParameter(NetworkContract.PlacesRequest.Params.OFFSET, Integer.toString(offset));
        }
        if (limit >= 0) {
            builder.appendQueryParameter(NetworkContract.PlacesRequest.Params.LIMIT, Integer.toString(limit));
        }

        return builder.build().toString();
    }
}
