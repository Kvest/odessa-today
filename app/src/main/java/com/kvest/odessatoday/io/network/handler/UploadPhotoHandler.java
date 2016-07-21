package com.kvest.odessatoday.io.network.handler;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.network.event.UploadPhotoEvent;
import com.kvest.odessatoday.io.network.request.UploadPhotoRequest;
import com.kvest.odessatoday.io.network.response.UploadPhotoResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by roman on 6/24/16.
 */
public class UploadPhotoHandler extends RequestHandler {
    private static final String EXTRA_PHOTO_PATH = "com.kvest.odessatoday.EXTRAS.PHOTO_PATH";
    private static final String EXTRA_TARGET_ID = "com.kvest.odessatoday.EXTRAS.TARGET_ID";
    private static final String EXTRA_TARGET_TYPE = "com.kvest.odessatoday.EXTRAS.TARGET_TYPE";

    public static void putExtras(Intent intent, long targetId, int targetType, String photoPath) {
        intent.putExtra(EXTRA_TARGET_ID, targetId);
        intent.putExtra(EXTRA_TARGET_TYPE, targetType);
        intent.putExtra(EXTRA_PHOTO_PATH, photoPath);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        String photoPath = intent.getStringExtra(EXTRA_PHOTO_PATH);
        long targetId = intent.getLongExtra(EXTRA_TARGET_ID, -1);
        int targetType = intent.getIntExtra(EXTRA_TARGET_TYPE, -1);

        try {
            //send request
            RequestFuture<UploadPhotoResponse> future = RequestFuture.newFuture();
            UploadPhotoRequest request = new UploadPhotoRequest(targetId, targetType, photoPath, future, future);
            TodayApplication.getApplication().getVolleyHelper().addRequest(request);

            UploadPhotoResponse response = future.get();
            if (response.isSuccessful()) {
                //save new gallery photos
                savePhotos(context, targetId, targetType, response.data);

                //ugly hack : we have to clear cache in order to avoid loading old list of the images from cache.
                //But we can't clear cache for the particular instance - so we need to delete all cache
                TodayApplication.getApplication().getVolleyHelper().clearCache();

                //notify listeners about successful uploading photo
                BusProvider.getInstance().post(new UploadPhotoEvent(response.data));
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful uploading photo
                BusProvider.getInstance().post(new UploadPhotoEvent(false, response.error));
            }
        } catch (InterruptedException e) {
            onException(e);
        } catch (ExecutionException e) {
            onException(e);
        } catch (IOException e) {
            onException(e);
        }
    }

    private void onException(Exception e) {
        LOGE(Constants.TAG, e.getLocalizedMessage());

        //notify listeners about unsuccessful uploading photo
        BusProvider.getInstance().post(new UploadPhotoEvent(false, e.getLocalizedMessage()));
    }

    private void savePhotos(Context context, long targetId, int targetType, String[] photos) {
        ContentValues cv = new ContentValues(1);

        //get URI by target type + fill cv and "where" statement
        Uri uri;
        String where;
        switch (Utils.targetType2Group(targetType)) {
            case Constants.TargetTypeGroup.CINEMA :
                uri = TodayProviderContract.CINEMAS_URI;
                cv.put(TodayProviderContract.Tables.Cinemas.Columns.IMAGE,
                       photos != null ? Utils.images2String(photos) : null);
                where = TodayProviderContract.Tables.Cinemas.Columns.CINEMA_ID + "=?";
                break;
            case Constants.TargetTypeGroup.PLACE :
                uri = TodayProviderContract.PLACES_URI;
                cv.put(TodayProviderContract.Tables.Places.Columns.IMAGE,
                        photos != null ? Utils.images2String(photos) : null);
                where = TodayProviderContract.Tables.Places.Columns.PLACE_ID + "=?";
                break;
            default:
                throw new IllegalArgumentException("targetType " + targetType + " not supported");
        }

        context.getContentResolver().update(uri, cv, where, new String[] {Long.toString(targetId)});
    }
}
