package com.kvest.odessatoday;

import android.app.Application;
import com.bugsense.trace.BugSenseHandler;
import com.kvest.odessatoday.io.network.VolleyHelper;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 01.06.14
 * Time: 19:28
 * To change this template use File | Settings | File Templates.
 */
public class TodayApplication extends Application {
    private static TodayApplication applicaion;

    private String todayApiKey;
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

        //get api key
        todayApiKey = Utils.getCertificateSignature(this);

        //init volley
        volleyHelper.init(getApplicationContext());
    }

    public String getTodayApiKey() {
        return todayApiKey;
    }

    public VolleyHelper getVolleyHelper() {
        return volleyHelper;
    }
}
