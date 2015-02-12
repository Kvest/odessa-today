package com.kvest.odessatoday.io.network.request;

import android.net.Uri;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.GetAnnouncementsResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.14
 * Time: 11:53
 * To change this template use File | Settings | File Templates.
 */
public class GetAnnouncementsRequest extends BaseRequest<GetAnnouncementsResponse> {
    private static Gson gson = new Gson();

    public GetAnnouncementsRequest(int offset, int limit, Response.Listener<GetAnnouncementsResponse> listener,
                                   Response.ErrorListener errorListener) {
        super(Method.GET, generateUrl(offset, limit), null, listener, errorListener);
    }

    @Override
    protected Response<GetAnnouncementsResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetAnnouncementsResponse getAnnouncementsResponse = gson.fromJson(json, GetAnnouncementsResponse.class);

            return Response.success(getAnnouncementsResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private static String generateUrl(int offset, int limit) {
        Uri.Builder builder = NetworkContract.AnnouncementRequest.url.buildUpon();
        if (offset >= 0) {
            builder.appendQueryParameter(NetworkContract.AnnouncementRequest.Params.OFFSET, Integer.toString(offset));
        }
        if (limit >= 0) {
            builder.appendQueryParameter(NetworkContract.AnnouncementRequest.Params.LIMIT, Integer.toString(limit));
        }

        return builder.build().toString();
    }
}
