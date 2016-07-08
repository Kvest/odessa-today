package com.kvest.odessatoday.io.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.UploadPhotoResponse;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.FileUtils;
import com.kvest.odessatoday.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by kvest on 03.07.16.
 */
public class UploadPhotoRequest extends BaseRequest<UploadPhotoResponse>  {
    String LINE_END = "\r\n";
    String TWO_HYPHENS = "--";

    private static Gson gson = new Gson();
    private final String mimeType;
    private final String boundary;
    private final byte[] body;
    private final String fileName;

    public UploadPhotoRequest(long targetId, int targetType, String filePath, Response.Listener<UploadPhotoResponse> listener,
                             Response.ErrorListener errorListener) throws IOException {
        super(Method.POST, getUrl(targetId, targetType), null, listener, errorListener);

        File file = new File(filePath);

        boundary = "apiclient-" + System.currentTimeMillis();
        mimeType = "multipart/form-data;boundary=" + boundary;
        body = FileUtils.readFileToByteArray(file);
        fileName = file.getName();
    }

    private static String getUrl(long targetId, int targetType) {
        switch (Utils.targetType2Group(targetType)) {
            case Constants.TargetTypeGroup.CINEMA :
                return NetworkContract.createCinemaGalleryUri(targetId).toString();
            case Constants.TargetTypeGroup.PLACE :
                return NetworkContract.createPlaceGalleryUri(targetId).toString();
            default:
                throw new RuntimeException("Unknown Constants.CommentTargetTypeGroup");
        }
    }

    @Override
    protected Response<UploadPhotoResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            UploadPhotoResponse getTimetableResponse  = gson.fromJson(json, UploadPhotoResponse.class);

            return Response.success(getTimetableResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String getBodyContentType() {
        return mimeType;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            dos.writeBytes(TWO_HYPHENS + boundary + LINE_END);
            dos.writeBytes("Content-Disposition: form-data; name=\"" +
                    NetworkContract.UploadPhotoRequest.Params.PHOTOS_ARRAY +
                    "\"; filename=\"" + fileName + "\"" + LINE_END);
            dos.writeBytes(LINE_END);

            dos.write(body);

            // send multipart form data necessary after file data...
            dos.writeBytes(LINE_END);
            dos.writeBytes(TWO_HYPHENS + boundary + TWO_HYPHENS + LINE_END);

            return bos.toByteArray();

        }  catch (IOException e) {
            return null;
        } finally {
            try {
                bos.close();
            } catch (IOException e) {}

            try {
                dos.close();
            } catch (IOException e) {}
        }
    }
}
