package com.kvest.odessatoday;

import android.app.Application;
import com.bugsense.trace.BugSenseHandler;
import com.kvest.odessatoday.io.network.VolleyHelper;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 01.06.14
 * Time: 19:28
 * To change this template use File | Settings | File Templates.
 */
public class TodayApplication extends Application {
    private static TodayApplication applicaion;

    private VolleyHelper volleyHelper = new VolleyHelper();

    public static TodayApplication getApplication() {
        return applicaion;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            BugSenseHandler.initAndStartSession(this, Constants.BUGSENS_API_KEY);
        }

        //save application instance
        applicaion = this;

        //init volley
        volleyHelper.init(getApplicationContext());
    }

    public VolleyHelper getVolleyHelper() {
        return volleyHelper;
    }
}
