package com.kvest.odessatoday.io.network.request;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.OrderTicketsResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created by kvest on 24.08.16.
 */
public class OrderTicketsRequest extends BaseRequest<OrderTicketsResponse> {
    private static final int TIMEOUT = 20 * 1000;

    private final long eventId;
    private final OrderInfo orderInfo;


    public OrderTicketsRequest(long eventId, OrderInfo orderInfo, Response.Listener<OrderTicketsResponse> listener,
                               Response.ErrorListener errorListener) {
        super(Method.POST, NetworkContract.createEventOrderTicketsUri(eventId).toString(),
              gson.toJson(orderInfo), listener, errorListener);

        //change timeout
        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        this.eventId = eventId;
        this.orderInfo = orderInfo;
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

    public long getEventId() {
        return eventId;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public static class OrderInfo {
        @SerializedName(NetworkContract.OrderTicketsRequest.Params.SECTOR)
        public long sectorId;
        @SerializedName(NetworkContract.OrderTicketsRequest.Params.AMOUNT)
        public int ticketsCount;
        @SerializedName(NetworkContract.OrderTicketsRequest.Params.NAME)
        public String name;
        @SerializedName(NetworkContract.OrderTicketsRequest.Params.PHONE)
        public String phone;
    }
}
