package com.kvest.odessatoday.io.request;

import android.net.Uri;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.io.NetworkContract;
import com.kvest.odessatoday.io.response.GetFilmsResponse;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.utils.LogUtils.*;
/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class GetFilmsRequest extends BaseRequest<GetFilmsResponse> {
    private static Gson gson = new Gson();

    private long startDate;
    private long endDate;

    public GetFilmsRequest(long startDate, long endDate, Response.Listener<GetFilmsResponse> listener,
                           Response.ErrorListener errorListener) {
        super(Method.GET, generateUrl(startDate, endDate), null, listener, errorListener);

        this.startDate = startDate;
        this.endDate = endDate;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        LOGD("KVEST_TAG", "startDate=" + this.startDate + "(" + sdf.format(TimeUnit.SECONDS.toMillis(this.startDate)) + ")" +
                           ", endDate=" + this.endDate + "(" + sdf.format(TimeUnit.SECONDS.toMillis(this.endDate)) + ")");
    };

    @Override
    protected Response<GetFilmsResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetFilmsResponse getFilmsResponse = gson.fromJson(json, GetFilmsResponse.class);

            return Response.success(getFilmsResponse, HttpHeaderParser.parseCacheHeaders(response));
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

    private static String generateUrl(long startDate, long endDate) {
        Uri.Builder builder = NetworkContract.FilmsRequest.url.buildUpon();
        if (startDate >= 0) {
            builder.appendQueryParameter(NetworkContract.FilmsRequest.Params.START_DATE, Long.toString(startDate));
        }
        if (endDate >= 0) {
            builder.appendQueryParameter(NetworkContract.FilmsRequest.Params.END_DATE, Long.toString(endDate));
        }

        return builder.build().toString();
    }
}
