package com.kvest.odessatoday.io.request;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.Film;
import com.kvest.odessatoday.datamodel.TimetableItem;
import com.kvest.odessatoday.io.NetworkContract;
import com.kvest.odessatoday.io.response.GetFilmsResponse;
import com.kvest.odessatoday.utils.Constants;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
        Log.d("KVEST_TAG", "startDate=" + startDate + ", endDate=" + endDate);
    };

    @Override
    protected Response<GetFilmsResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetFilmsResponse getFilmsResponse = gson.fromJson(json, GetFilmsResponse.class);

            //save data
            if (getFilmsResponse.isSuccessful()) {
                saveFilms(getFilmsResponse.data.films);
            }

            return Response.success(getFilmsResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
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

    private void saveFilms(List<Film> films) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        //delete timetable from startDate to endDate
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(TIMETABLE_URI)
                .withSelection(Tables.FilmsTimetable.Columns.DATE + ">=? AND " + Tables.FilmsTimetable.Columns.DATE + "<=?",
                        new String[]{Long.toString(startDate), Long.toString(endDate)})
                .build();
        operations.add(deleteOperation);

        for (Film film : films) {
            //insert film
            operations.add(ContentProviderOperation.newInsert(FILMS_URI).withValues(film.getContentValues()).build());

            //insert timetable
            for (TimetableItem timetableItem : film.timetable) {
                operations.add(ContentProviderOperation.newInsert(TIMETABLE_URI).withValues(timetableItem.getContentValues(film.id)).build());
            }
        }

        //apply
        Context context = TodayApplication.getApplication().getApplicationContext();
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            Log.e(Constants.TAG, re.getMessage());
            re.printStackTrace();
        }catch (OperationApplicationException oae) {
            Log.e(Constants.TAG, oae.getMessage());
            oae.printStackTrace();
        }
    }
}
