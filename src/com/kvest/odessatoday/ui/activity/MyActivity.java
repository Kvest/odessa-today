package com.kvest.odessatoday.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import com.kvest.odessatoday.R;

public class MyActivity extends TodayBaseActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
