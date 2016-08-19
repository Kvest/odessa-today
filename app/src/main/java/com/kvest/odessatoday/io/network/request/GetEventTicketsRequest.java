package com.kvest.odessatoday.io.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.GetEventTicketsResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created by kvest on 19.08.16.
 */
public class GetEventTicketsRequest extends BaseRequest<GetEventTicketsResponse> {
    private final long eventId;

    public GetEventTicketsRequest(long eventId, Response.Listener<GetEventTicketsResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(Method.GET, NetworkContract.createEventTicketsUri(eventId).toString(), null, listener, errorListener);

        this.eventId = eventId;
    }

    @Override
    protected Response<GetEventTicketsResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetEventTicketsResponse getEventTicketsResponse  = gson.fromJson(json, GetEventTicketsResponse.class);

            return Response.success(getEventTicketsResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public long getEventId() {
        return eventId;
    }
}
