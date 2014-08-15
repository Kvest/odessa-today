package com.kvest.odessatoday.io.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.io.NetworkContract;
import com.kvest.odessatoday.io.response.AddCommentResponse;
import com.kvest.odessatoday.utils.Constants;

import java.io.UnsupportedEncodingException;

/**
 * User: roman
 * Date: 8/15/14
 * Time: 3:48 PM
 */
public class AddCommentRequest extends BaseRequest<AddCommentResponse> {
    private static Gson gson = new Gson();

    public AddCommentRequest(long targetId, int targetType, Comment comment, Response.Listener<AddCommentResponse> listener,
                             Response.ErrorListener errorListener) {
        super(Method.POST, getUrl(targetId, targetType), gson.toJson(comment), listener, errorListener);

    }

    @Override
    protected Response<AddCommentResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            AddCommentResponse addCommentsResponse  = gson.fromJson(json, AddCommentResponse.class);

            return Response.success(addCommentsResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private static String getUrl(long targetId, int targetType) {
        switch (targetType) {
            case Constants.CommentTargetType.CINEMA : return NetworkContract.createCinemaCommentsUri(targetId).toString();
            case Constants.CommentTargetType.FILM : return NetworkContract.createFilmCommentsUri(targetId).toString();
        }

        return "";
    }

    public static class Comment {
        public String name = "";
        public String text = "";
        public String device_id = "";
    }
}
