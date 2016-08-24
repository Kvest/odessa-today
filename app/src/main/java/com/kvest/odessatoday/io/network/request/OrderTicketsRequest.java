package com.kvest.odessatoday.io.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.OrderTicketsResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created by kvest on 24.08.16.
 */
public class OrderTicketsRequest extends BaseRequest<OrderTicketsResponse> {
    private final long eventId;
    private final long sectorId;
    private final int ticketsCount;
    private final String name;
    private final String phone;


    public OrderTicketsRequest(long eventId, long sectorId, int ticketsCount, String name, String phone,
                               Response.Listener<OrderTicketsResponse> listener,
                               Response.ErrorListener errorListener) {
        super(Method.POST, NetworkContract.createEventOrderTicketsUri(eventId).toString(),
              createBody(sectorId, ticketsCount, name, phone), listener, errorListener);

        this.eventId = eventId;
        this.sectorId = sectorId;
        this.ticketsCount = ticketsCount;
        this.name = name;
        this.phone = phone;
    }

    @Override
    protected Response<OrderTicketsResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            OrderTicketsResponse orderTicketsResponse  = gson.fromJson(json, OrderTicketsResponse.class);

            return Response.success(orderTicketsResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private static String createBody(long sectorId, int ticketsCount, String name, String phone) {
        //TODO
        return null;
    }

    public long getEventId() {
        return eventId;
    }

    public long getSectorId() {
        return sectorId;
    }

    public int getTicketsCount() {
        return ticketsCount;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
