package com.kvest.odessatoday.io.network.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;

import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.Place;
import com.kvest.odessatoday.io.network.event.PlacesLoadedEvent;
import com.kvest.odessatoday.io.network.request.GetPlacesRequest;
import com.kvest.odessatoday.io.network.response.GetPlacesResponse;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.provider.TodayProviderContract.CONTENT_AUTHORITY;
import static com.kvest.odessatoday.provider.TodayProviderContract.PLACES_URI;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by kvest on 16.09.15.
 */
public class LoadPlacesHandler extends RequestHandler {
    private static final String PLACES_TYPE_EXTRA = "com.kvest.odessatoday.EXTRAS.PLACES_TYPE";
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 50;

    public static void putExtras(Intent intent, int placesType) {
        intent.putExtra(PLACES_TYPE_EXTRA, placesType);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        int placesType = intent.getIntExtra(PLACES_TYPE_EXTRA, -1);
        Uri placesUri = Uri.withAppendedPath(PLACES_URI, Integer.toString(placesType));

        boolean hasMorePlaces = true;
        boolean deleteOldPlaces = true;
        int offset = DEFAULT_OFFSET;
        int limit = DEFAULT_LIMIT;

        while (hasMorePlaces) {
            hasMorePlaces = false;

            RequestFuture<GetPlacesResponse> future = RequestFuture.newFuture();
            GetPlacesRequest request = new GetPlacesRequest(placesType, offset, limit, future, future);
            TodayApplication.getApplication().getVolleyHelper().addRequest(request);
            try {
                GetPlacesResponse response = future.get();
                if (response.isSuccessful()) {
                    //save places
                    savePlaces(context, response.data.places, placesUri, deleteOldPlaces);
                    deleteOldPlaces = false;

                    if (response.data.placesRemained > 0) {
                        hasMorePlaces = true;
                        offset += response.data.places.size();
                    } else {
                        BusProvider.getInstance().post(new PlacesLoadedEvent(placesType, true, null));
                    }
                } else {
                    LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                    //notify listeners about unsuccessful loading places
                    BusProvider.getInstance().post(new PlacesLoadedEvent(placesType, false, response.error));
                    break;
                }
            } catch (InterruptedException e) {
                LOGE(Constants.TAG, e.getLocalizedMessage());

                //notify listeners about unsuccessful loading places
                BusProvider.getInstance().post(new PlacesLoadedEvent(placesType, false, e.getLocalizedMessage()));

                break;
            } catch (ExecutionException e) {
                LOGE(Constants.TAG, e.getLocalizedMessage());

                //notify listeners about unsuccessful loading places
                BusProvider.getInstance().post(new PlacesLoadedEvent(placesType, false, e.getLocalizedMessage()));

                break;
            }
        }
    }

    private void savePlaces(Context context, List<Place> places, Uri placesUri, boolean deleteOldPlaces) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(places.size() + (deleteOldPlaces ? 1 : 0));

        if (deleteOldPlaces) {
            //delete old places
            ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(placesUri).build();
            operations.add(deleteOperation);
        }

        //insert places
        for (Place place : places) {
            operations.add(ContentProviderOperation.newInsert(placesUri)
                            .withValues(place.getContentValues()).build());
        }

        //apply
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
            re.printStackTrace();
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
            oae.printStackTrace();
        }
    }
}
