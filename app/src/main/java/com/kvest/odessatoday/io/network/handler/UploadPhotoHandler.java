package com.kvest.odessatoday.io.network.handler;

import android.content.Context;
import android.content.Intent;

import com.kvest.odessatoday.io.network.event.UploadPhotoEvent;
import com.kvest.odessatoday.utils.BusProvider;

/**
 * Created by roman on 6/24/16.
 */
public class UploadPhotoHandler extends RequestHandler {
    private static final String EXTRA_PHOTO_PATH = "com.kvest.odessatoday.EXTRAS.PHOTO_PATH";

    public static void putExtras(Intent intent, String photoPath) {
        intent.putExtra(EXTRA_PHOTO_PATH, photoPath);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        String photoPath = intent.getStringExtra(EXTRA_PHOTO_PATH);

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
        }
        BusProvider.getInstance().post(new UploadPhotoEvent(true, null));
    }
}
