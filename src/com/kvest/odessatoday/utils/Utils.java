package com.kvest.odessatoday.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 18.08.14
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    public static String getDeviceId(Context context)
    {
        //Android id
        String deviceId = Settings.System.getString(context.getContentResolver(),Settings.System.ANDROID_ID);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }

        //IMEI
        TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tManager.getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }

        //Device serial id
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            deviceId = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) { }
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }

        return "unknown";
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null && ni.isConnected());
    }
}
