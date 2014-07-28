package com.kvest.odessatoday.io.request;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.TimetableItem;
import com.kvest.odessatoday.io.NetworkContract;
import com.kvest.odessatoday.io.response.GetTimetableResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.Constants;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 23.07.14
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class GetTimetableRequest extends BaseRequest<GetTimetableResponse> {
    private static Gson gson = new Gson();

    private long filmId;

    public GetTimetableRequest(long filmId, Response.Listener<GetTimetableResponse> listener, Response.ErrorListener errorListener) {
        super(Method.GET, NetworkContract.createTimetableUri(filmId).toString(), null, listener, errorListener);

        this.filmId = filmId;
    }

    @Override
    protected Response<GetTimetableResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetTimetableResponse getTimetableResponse  = gson.fromJson(json, GetTimetableResponse.class);

            //save data
            if (getTimetableResponse.isSuccessful()) {
                saveTimetable(getTimetableResponse.data.timetable);
            }

            return Response.success(getTimetableResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private void saveTimetable(List<TimetableItem> timetable) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        //delete timetable for film with filmId
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(TIMETABLE_URI)
                .withSelection(Tables.FilmsTimetable.Columns.FILM_ID + "=?", new String[]{Long.toString(filmId)})
                .build();
        operations.add(deleteOperation);

        //insert timetable items
        for (TimetableItem timetableItem : timetable) {
            operations.add(ContentProviderOperation.newInsert(TIMETABLE_URI).withValues(timetableItem.getContentValues(filmId)).build());
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
