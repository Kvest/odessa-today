package com.kvest.odessatoday.ui.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.CalendarFragment;
import com.kvest.odessatoday.ui.fragment.FilmsListFragment;

import java.util.concurrent.TimeUnit;

public class MainActivity extends TodayBaseActivity implements FilmsListFragment.ShowCalendarListener,
                                                               CalendarFragment.OnDateSelectedListener,
                                                               FilmsListFragment.FilmSelectedListener{
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
                FilmsListFragment filmsListFragment = FilmsListFragment.getInstance(shownFilmsDate, true);
                transaction.add(R.id.fragment_container, filmsListFragment);
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
            FilmsListFragment filmsListFragment = FilmsListFragment.getInstance(shownFilmsDate, isCurrentDay(shownFilmsDate));
            transaction.setCustomAnimations(R.anim.slide_left_in,  R.anim.slide_left_out);
            transaction.replace(R.id.fragment_container, filmsListFragment);
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

    private boolean isCurrentDay(long date) {
        //set start of the current date in seconds
        long currentDate = System.currentTimeMillis();
        currentDate -= (currentDate % TimeUnit.DAYS.toMillis(1));
        currentDate = TimeUnit.MILLISECONDS.toSeconds(currentDate);

        return (date >= currentDate && date < (currentDate + TimeUnit.DAYS.toSeconds(1)));
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

    @Override
    public void onFilmSelected(long filmId) {
        startActivity(FilmDetailsActivity.getStartIntent(this, filmId));
    }
}
