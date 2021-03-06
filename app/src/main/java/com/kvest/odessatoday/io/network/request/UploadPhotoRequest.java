package com.kvest.odessatoday.io.network.request;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.response.UploadPhotoResponse;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.FileUtils;
import com.kvest.odessatoday.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by kvest on 03.07.16.
 */
public class UploadPhotoRequest extends BaseRequest<UploadPhotoResponse>  {
    private static final int TIMEOUT = 60 * 1000;
    String LINE_END = "\r\n";
    String TWO_HYPHENS = "--";

    private final String mimeType;
    private final String boundary;
    private final String fileName;

    public UploadPhotoRequest(long targetId, int targetType, String filePath, Response.Listener<UploadPhotoResponse> listener,
                             Response.ErrorListener errorListener) throws IOException {
        super(Method.POST, getUrl(targetId, targetType), null, listener, errorListener);

        //change timeout
        setRetryPolicy(new DefaultRetryPolicy(TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                              DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        boundary = "apiclient-" + System.currentTimeMillis();
        mimeType = "multipart/form-data;boundary=" + boundary;
        fileName = filePath;
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

            //write body
            File file = new File(fileName);
            InputStream in = null;
            try {
                in = FileUtils.openInputStream(file);
                byte[] buffer = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
                int n;
                while (-1 != (n = in.read(buffer))) {
                    dos.write(buffer, 0, n);
                }
            } finally {
                FileUtils.closeQuietly(in);
            }

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
