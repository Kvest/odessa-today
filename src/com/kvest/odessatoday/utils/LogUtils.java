package com.kvest.odessatoday.utils;

import android.util.Log;
import com.kvest.odessatoday.BuildConfig;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 23.09.14
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
public class LogUtils {
    private LogUtils() {
    }

    public static void LOGD(final String tag, String message) {
        if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    public static void LOGV(final String tag, String message) {
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message);
        }
    }

    public static void LOGT(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void LOGI(final String tag, String message) {
        Log.i(tag, message);
    }

    public static void LOGW(final String tag, String message) {
        Log.w(tag, message);
    }

    public static void LOGE(final String tag, String message) {
        Log.e(tag, message);
    }
}
