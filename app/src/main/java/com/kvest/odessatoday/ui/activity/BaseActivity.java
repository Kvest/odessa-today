package com.kvest.odessatoday.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created by kvest on 26.08.15.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //set theme
        if (TodayApplication.getApplication().getCurrentTheme() == Constants.ThemeType.NIGHT) {
            setTheme(R.style.NightOdessaTodayTheme);
        } else {
            setTheme(R.style.DayOdessaTodayTheme);
        }

        super.onCreate(savedInstanceState);
    }
}
