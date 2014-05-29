package com.kvest.odessatoday.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import com.bugsense.trace.BugSenseHandler;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 29.05.14
 * Time: 21:48
 * To change this template use File | Settings | File Templates.
 */
public class TodayBaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BugSenseHandler.initAndStartSession(this, Constants.BUGSENS_API_KEY);
    }
}
