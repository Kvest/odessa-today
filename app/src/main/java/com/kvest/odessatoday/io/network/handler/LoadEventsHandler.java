package com.kvest.odessatoday.io.network.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.Event;
import com.kvest.odessatoday.io.network.event.EventsLoadedEvent;
import com.kvest.odessatoday.io.network.request.GetEventsRequest;
import com.kvest.odessatoday.io.network.response.GetEventsResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.provider.TodayProviderContract.CONTENT_AUTHORITY;
import static com.kvest.odessatoday.provider.TodayProviderContract.EVENTS_URI;
import static com.kvest.odessatoday.provider.TodayProviderContract.EVENTS_TIMETABLE_URI;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by kvest on 30.11.15.
 */
public class LoadEventsHandler extends RequestHandler {
    private static final long EMPTY_PLACE_ID = -1L;
    private static final int EMPTY_TYPE = -1;
    private static final String EXTRA_START_DATE = "com.kvest.odessatoday.EXTRAS.START_DATE";
    private static final String EXTRA_END_DATE = "com.kvest.odessatoday.EXTRAS.END_DATE";
    private static final String EXTRA_PLACE_ID = "com.kvest.odessatoday.EXTRAS.PLACE_ID";
    private static final String EXTRA_TYPE = "com.kvest.odessatoday.EXTRAS.TYPE";

    private static final String SELECTION_WITH_PLACE_ID = TodayProviderContract.Tables.EventsTimetable.Columns.DATE + ">=? AND " +
                                                          TodayProviderContract.Tables.EventsTimetable.Columns.DATE + "<=? AND " +
                                                          TodayProviderContract.Tables.EventsTimetable.Columns.PLACE_ID + "=?";

    private static final String SELECTION_WITH_TYPE = TodayProviderContract.Tables.EventsTimetable.Columns.DATE + ">=? AND " +
                                                      TodayProviderContract.Tables.EventsTimetable.Columns.DATE + "<=? AND " +
                                                      TodayProviderContract.Tables.EventsTimetable.Columns.EVENT_ID + " in (" + TodayProviderContract.Tables.Events.GET_EVENTS_ID_BY_TYPE_SQL + ")";

    public static void putExtras(Intent intent, long startDate, long endDate, long placeId) {
        intent.putExtra(EXTRA_START_DATE, startDate);
        intent.putExtra(EXTRA_END_DATE, endDate);
        intent.putExtra(EXTRA_PLACE_ID, placeId);
    }

    public static void putExtras(Intent intent, long startDate, long endDate, int type) {
        intent.putExtra(EXTRA_START_DATE, startDate);
        intent.putExtra(EXTRA_END_DATE, endDate);
        intent.putExtra(EXTRA_TYPE, type);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        long startDate = intent.getLongExtra(EXTRA_START_DATE, -1);
        long endDate = intent.getLongExtra(EXTRA_END_DATE, -1);
        long placeId = intent.getLongExtra(EXTRA_PLACE_ID, EMPTY_PLACE_ID);
        int type = intent.getIntExtra(EXTRA_TYPE, EMPTY_TYPE);

        //send request
        RequestFuture<GetEventsResponse> future = RequestFuture.newFuture();
        GetEventsRequest request;
        if (type == EMPTY_TYPE) {
            request = new GetEventsRequest(startDate, endDate, placeId, future, future);
        } else {
            request = new GetEventsRequest(startDate, endDate, type, future, future);
        }
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetEventsResponse response = future.get();
            if (response.isSuccessful()) {
                //save events
                saveEvents(context, response.data.events, request.getStartDate(), request.getEndDate(), request.getPlaceId(), request.getType());

                //notify listeners about successful loading events
                BusProvider.getInstance().post(new EventsLoadedEvent(type, placeId, true));
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading events
                BusProvider.getInstance().post(new EventsLoadedEvent(type, placeId, false));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading events
            BusProvider.getInstance().post(new EventsLoadedEvent(type, placeId, false));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading events
            BusProvider.getInstance().post(new EventsLoadedEvent(type, placeId, false));
        }
    }

    private void saveEvents(Context context, List<Event> events, long startDate, long endDate, long placeId, int type) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        //delete timetable from startDate to endDate
        if (type == EMPTY_TYPE) {
            ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(EVENTS_TIMETABLE_URI)
                    .withSelection(SELECTION_WITH_PLACE_ID, new String[]{Long.toString(startDate), Long.toString(endDate), Long.toString(placeId)})
                    .build();
            operations.add(deleteOperation);
        } else {
            ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(EVENTS_TIMETABLE_URI)
                    .withSelection(SELECTION_WITH_TYPE, new String[]{Long.toString(startDate), Long.toString(endDate), Integer.toString(type)})
                    .build();
            operations.add(deleteOperation);
        }

        for (Event event : events) {
            //insert event
            operations.add(ContentProviderOperation.newInsert(EVENTS_URI).withValues(event.getContentValues()).build());

            //insert timetable
            for (Event.Timetable timetableItem : event.timetable) {
                operations.add(ContentProviderOperation.newInsert(EVENTS_TIMETABLE_URI).withValues(timetableItem.getContentValues(event.id)).build());
            }
        }

        //apply
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
        }
    }
}
