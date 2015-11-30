package com.kvest.odessatoday.io.network.request;

import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.io.network.response.GetEventsResponse;

import java.io.UnsupportedEncodingException;

import static com.kvest.odessatoday.io.network.NetworkContract.*;
/**
 * Created by kvest on 29.11.15.
 */
public class GetEventsRequest extends BaseRequest<GetEventsResponse> {
    private static Gson gson = new Gson();

    private long startDate;
    private long endDate;
    private long placeId;
    private int type;

    public GetEventsRequest(long startDate, long endDate, int type,
                            Response.Listener<GetEventsResponse> listener,
                            Response.ErrorListener errorListener) {
        super(Method.GET, generateUrl(startDate, endDate, type, -1), null, listener, errorListener);

        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.placeId = -1;
    }

    public GetEventsRequest(long startDate, long endDate, long placeId,
                            Response.Listener<GetEventsResponse> listener,
                            Response.ErrorListener errorListener) {
        super(Method.GET, generateUrl(startDate, endDate, -1, placeId), null, listener, errorListener);

        this.startDate = startDate;
        this.endDate = endDate;
        this.placeId = placeId;
        this.type = -1;
    }

    @Override
    protected Response<GetEventsResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetEventsResponse getEventsResponse = gson.fromJson(json, GetEventsResponse.class);

            return Response.success(getEventsResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public long getPlaceId() {
        return placeId;
    }

    public int getType() {
        return type;
    }

    private static String generateUrl(long startDate, long endDate, int type, long placeId) {
        Uri.Builder builder = EventsRequest.url.buildUpon();
        if (startDate >= 0) {
            builder.appendQueryParameter(EventsRequest.Params.START_DATE, Long.toString(startDate));
        }
        if (endDate >= 0) {
            builder.appendQueryParameter(EventsRequest.Params.END_DATE, Long.toString(endDate));
        }
        if (type >= 0) {
            builder.appendQueryParameter(EventsRequest.Params.TYPE, Integer.toString(type));
        }
        if (placeId >=0) {
            builder.appendQueryParameter(EventsRequest.Params.PLACE_ID, Long.toString(placeId));
        }

        return builder.build().toString();
    }
}
