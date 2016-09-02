package com.kvest.odessatoday;

import android.app.Application;
import com.bugsense.trace.BugSenseHandler;
import com.kvest.odessatoday.io.network.VolleyHelper;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.SettingsSPStorage;
import com.kvest.odessatoday.utils.Utils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 01.06.14
 * Time: 19:28
 * To change this template use File | Settings | File Templates.
 */
public class TodayApplication extends Application {
    private static TodayApplication application;

    private String todayApiKey;
    private String clientId;
    private VolleyHelper volleyHelper = new VolleyHelper();
    private int currentTheme;
    private RefWatcher refWatcher;

    public static TodayApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        refWatcher = LeakCanary.install(this);

        //load and apply theme
        currentTheme = SettingsSPStorage.getCurrentTheme(this);

        if (!BuildConfig.DEBUG) {
            BugSenseHandler.initAndStartSession(this, Constants.BUGSENS_API_KEY);
        }

        //save application instance
        application = this;

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

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }
}
