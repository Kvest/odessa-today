package com.kvest.odessatoday.io.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.AddCommentResponse;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;

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
        switch (Utils.targetType2Group(targetType)) {
            case Constants.TargetTypeGroup.FILM :
                return NetworkContract.createFilmCommentsUri(targetId).toString();
            case Constants.TargetTypeGroup.CINEMA :
                return NetworkContract.createCinemaCommentsUri(targetId).toString();
            case Constants.TargetTypeGroup.EVENT :
                return NetworkContract.createEventCommentsUri(targetId).toString();
            case Constants.TargetTypeGroup.PLACE :
                return NetworkContract.createPlaceCommentsUri(targetId).toString();
            default:
                throw new RuntimeException("Unknown Constants.CommentTargetTypeGroup");
        }
    }

    public static class Comment {
        @SerializedName("name")
        public String name = "";
        @SerializedName("text")
        public String text = "";
        @SerializedName("rating")
        public Float rating;
    }
}
