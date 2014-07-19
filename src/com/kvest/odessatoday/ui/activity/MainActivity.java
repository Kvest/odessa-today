package com.kvest.odessatoday.ui.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.ui.fragment.CalendarFragment;
import com.kvest.odessatoday.ui.fragment.FilmsFragment;

import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MainActivity extends TodayBaseActivity implements FilmsFragment.ShowCalendarListener,
                                                               CalendarFragment.OnDateSelectedListener {
    private long shownFilmsDate;
    private FrameLayout calendarContainer;

    private Animation showCalendarAnimation;
    private Animation hideCalendarAnimation;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        init();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                shownFilmsDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                FilmsFragment filmsFragment = FilmsFragment.getInstance(shownFilmsDate, true);
                transaction.add(R.id.fragment_container, filmsFragment);
            } finally {
                transaction.commit();
            }
        }
    }

    private void init() {
        calendarContainer = (FrameLayout)findViewById(R.id.calendar_container);
        calendarContainer.setVisibility(View.INVISIBLE);
        calendarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCalendar();
            }
        });

        showCalendarAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        hideCalendarAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        hideCalendarAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                calendarContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    @Override
    public void onBackPressed() {
        if (isCalendarShown()) {
            hideCalendar();
        } else {
            super.onBackPressed();
        }
    }

    private void showFilmsByDate(long date) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            shownFilmsDate = TimeUnit.MILLISECONDS.toSeconds(date);
            //TODO  calculate today flag
            FilmsFragment filmsFragment = FilmsFragment.getInstance(shownFilmsDate, false);
            transaction.replace(R.id.fragment_container, filmsFragment);
        } finally {
            transaction.commit();
        }
    }

    @Override
    public void onShowCalendar() {
        if (isCalendarShown()) {
            hideCalendar();
        } else {
            showCalendar(TimeUnit.SECONDS.toMillis(shownFilmsDate));
        }
    }

    private void showCalendar(long selectedDate) {
        //set fragment
        if (calendarContainer != null && !isCalendarShown()) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                CalendarFragment calendarFragment = CalendarFragment.getInstance(selectedDate);
                transaction.add(R.id.calendar_container, calendarFragment);
            } finally {
                transaction.commit();
            }

            //set calendar visible
            calendarContainer.setVisibility(View.VISIBLE);

            //animate
            calendarContainer.clearAnimation();
            calendarContainer.startAnimation(showCalendarAnimation);
        }
    }

    private void hideCalendar() {
        //animate
        calendarContainer.clearAnimation();
        calendarContainer.startAnimation(hideCalendarAnimation);
    }

    private boolean isCalendarShown() {
        return calendarContainer.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDateSelected(long date) {
        //hide calendar
        hideCalendar();

        //show films by selected date
        showFilmsByDate(date);
    }
}
