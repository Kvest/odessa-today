package com.kvest.odessatoday.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kvest on 16.07.16.
 */
public class DBCleanerService extends IntentService {
    public static void start(Context context) {
        Intent intent = new Intent(context, DBCleanerService.class);
        context.startService(intent);
    }

    public DBCleanerService() {
        super("DBCleanerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Films + FilmsTimetable + AnnouncementsMetadata + comments
        //Events + EventsTimetable + comments
    }
}
