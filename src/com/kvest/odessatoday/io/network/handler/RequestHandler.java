package com.kvest.odessatoday.io.network.handler;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Kvest on 10.01.2015.
 */
public abstract class RequestHandler {
    public abstract void processIntent(Context context, Intent intent);

    protected void sendLocalBroadcast(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
