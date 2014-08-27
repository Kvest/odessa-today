package com.kvest.odessatoday.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.kvest.odessatoday.service.NetworkService;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 27.08.14
 * Time: 23:58
 * To change this template use File | Settings | File Templates.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //get network info
        NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

        //start sync if network appears
        if (networkInfo != null && networkInfo.isConnected()) {
            NetworkService.sync(context);
        }
    }
}
