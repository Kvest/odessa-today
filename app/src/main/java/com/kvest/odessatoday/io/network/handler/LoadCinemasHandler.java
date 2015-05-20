package com.kvest.odessatoday.io.network.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.Cinema;
import com.kvest.odessatoday.io.network.notification.LoadCinemasNotification;
import com.kvest.odessatoday.io.network.request.GetCinemasRequest;
import com.kvest.odessatoday.io.network.response.GetCinemasResponse;
import com.kvest.odessatoday.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.provider.TodayProviderContract.CINEMAS_URI;
import static com.kvest.odessatoday.provider.TodayProviderContract.CONTENT_AUTHORITY;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 10.01.2015.
 */
public class LoadCinemasHandler extends RequestHandler {
    @Override
    public void processIntent(Context context, Intent intent) {
        RequestFuture<GetCinemasResponse> future = RequestFuture.newFuture();
        GetCinemasRequest request = new GetCinemasRequest(future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetCinemasResponse response = future.get();
            if (response.isSuccessful()) {
                //save cinemas
                saveCinemas(context, response.data.cinemas);

                //notify listeners about successful loading cinemas
                sendLocalBroadcast(context, LoadCinemasNotification.createSuccessResult());
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading cinemas
                sendLocalBroadcast(context, LoadCinemasNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading cinemas
            sendLocalBroadcast(context, LoadCinemasNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading cinemas
            sendLocalBroadcast(context, LoadCinemasNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }

    private void saveCinemas(Context context, List<Cinema> cinemas) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(cinemas.size() + 1);

        //delete cinemas
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(CINEMAS_URI).build();
        operations.add(deleteOperation);

        for (Cinema cinema : cinemas) {
            //insert film
            operations.add(ContentProviderOperation.newInsert(CINEMAS_URI).withValues(cinema.getContentValues()).build());
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
