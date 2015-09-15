package com.kvest.odessatoday.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.FontUtils;

import java.lang.reflect.Field;

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

    protected void setupToolbar(Toolbar toolbar){
        //It's an ugly hack
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            TextView titleTextView = (TextView) f.get(toolbar);
            if (titleTextView != null) {
                titleTextView.setTypeface(FontUtils.getFont(getAssets(), FontUtils.HELVETICANEUECYR_ROMAN_FONT));
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {}
    }
}
