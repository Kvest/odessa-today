package com.kvest.odessatoday;

import android.app.Application;
import com.bugsense.trace.BugSenseHandler;
import com.kvest.odessatoday.io.network.VolleyHelper;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.SettingsSPStorage;
import com.kvest.odessatoday.utils.Utils;
import com.squareup.leakcanary.LeakCanary;

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
    private String clientId;
    private VolleyHelper volleyHelper = new VolleyHelper();
    private int currentTheme;

    public static TodayApplication getApplication() {
        return applicaion;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        //load and apply theme
        currentTheme = SettingsSPStorage.getCurrentTheme(this);

        if (!BuildConfig.DEBUG) {
            BugSenseHandler.initAndStartSession(this, Constants.BUGSENS_API_KEY);
        }

        //save application instance
        applicaion = this;

        //get api key and client id
        todayApiKey = Utils.getCertificateSignature(this);
        clientId = Utils.getDeviceId(this);

        //init volley
        volleyHelper.init(getApplicationContext());
    }

    public String getTodayApiKey() {
        return todayApiKey;
    }

    public String getClientId() {
        return clientId;
    }

    public VolleyHelper getVolleyHelper() {
        return volleyHelper;
    }

    public int getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(int currentTheme) {
        if (this.currentTheme != currentTheme) {
            this.currentTheme = currentTheme;

            SettingsSPStorage.setCurrentTheme(this, this.currentTheme);
        }
    }
}
