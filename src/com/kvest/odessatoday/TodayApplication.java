package com.kvest.odessatoday;

import android.app.Application;
import com.kvest.odessatoday.io.VolleyHelper;
import com.kvest.odessatoday.service.NetworkService;

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

        //save application instance
        applicaion = this;

        //init volley
        volleyHelper.init(getApplicationContext());

        //sync data
        NetworkService.sync(getApplicationContext());
    }

    public VolleyHelper getVolleyHelper() {
        return volleyHelper;
    }
}
