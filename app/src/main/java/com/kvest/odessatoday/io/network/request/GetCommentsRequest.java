package com.kvest.odessatoday.io.network.request;

import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.GetCommentsResponse;
import com.kvest.odessatoday.utils.Constants;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 28.07.14
 * Time: 23:50
 * To change this template use File | Settings | File Templates.
 */
public class GetCommentsRequest extends BaseRequest<GetCommentsResponse> {
    private static Gson gson = new Gson();

    private long targetId;
    private int targetType;

    public GetCommentsRequest(long targetId, int targetType, int offset, int limit,
                              Response.Listener<GetCommentsResponse> listener,
                              Response.ErrorListener errorListener) {
        super(Method.GET, generateUrl(targetId, targetType, offset, limit), null, listener, errorListener);

        this.targetId = targetId;
        this.targetType = targetType;
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

    public long getTargetId() {
        return targetId;
    }

    public int getTargetType() {
        return targetType;
    }

    private static String generateUrl(long targetId, int targetType, int offset, int limit) {
        Uri.Builder builder;
        if (targetType == Constants.CommentTargetType.FILM) {
            builder = NetworkContract.createFilmCommentsUri(targetId).buildUpon();
        } else if (targetType == Constants.CommentTargetType.CINEMA) {
            builder = NetworkContract.createCinemaCommentsUri(targetId).buildUpon();
        } else if (targetType >= Constants.CommentTargetType.CONCERT && targetType <= Constants.CommentTargetType.WORKSHOP) {
            builder = NetworkContract.createEventCommentsUri(targetId).buildUpon();
        } else if (targetType >= Constants.CommentTargetType.THEATRE && targetType <= Constants.CommentTargetType.BATH) {
            builder = NetworkContract.createPlaceCommentsUri(targetId).buildUpon();
        } else {
            throw new IllegalArgumentException("Unknown targetType of the comment type");
        }

        if (offset >= 0) {
            builder.appendQueryParameter(NetworkContract.CommentsRequest.Params.OFFSET, Integer.toString(offset));
        }
        if (limit >= 0) {
            builder.appendQueryParameter(NetworkContract.CommentsRequest.Params.LIMIT, Integer.toString(limit));
        }

        return builder.build().toString();
    }
}
