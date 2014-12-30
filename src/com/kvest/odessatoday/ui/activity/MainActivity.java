package com.kvest.odessatoday.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.receiver.NetworkChangeReceiver;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.fragment.AnnouncementFilmsListFragment;
import com.kvest.odessatoday.ui.fragment.CalendarFragment;
import com.kvest.odessatoday.ui.fragment.CinemasListFragment;
import com.kvest.odessatoday.ui.fragment.FilmsListFragment;
import com.kvest.odessatoday.utils.TimeUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements FilmsListFragment.ShowCalendarListener,
                                                               CalendarFragment.OnDateSelectedListener,
                                                               FilmsListFragment.FilmSelectedListener,
                                                               CinemasListFragment.CinemaSelectedListener,
                                                               AnnouncementFilmsListFragment.AnnouncementFilmSelectedListener {
    private long shownFilmsDate;
    private final Calendar calendar = Calendar.getInstance();
    private FrameLayout calendarContainer;

    private Animation showCalendarAnimation;
    private Animation hideCalendarAnimation;

    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        return intent;
    }

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

        //sync data
        NetworkService.sync(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isFinishing()) {
            unregisterReceiver(networkChangeReceiver);
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

        //category selector
        RadioGroup categorySelector = (RadioGroup)findViewById(R.id.category_selector);
        categorySelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.timetable :
                        switchToTimetable();
                        break;
                    case R.id.cinemas :
                        switchToCinemas();
                        break;
                    case R.id.announcements :
                        switchToAnnouncements();
                        break;
                }
            }
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
        shownFilmsDate = Math.max(date, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        Fragment filmsListFragment = FilmsListFragment.getInstance(shownFilmsDate, TimeUtils.isCurrentDay(shownFilmsDate));
        replaceFragment(filmsListFragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.slide_left_in,  R.anim.slide_left_out);
            transaction.replace(R.id.fragment_container, fragment);
        } finally {
            transaction.commit();
        }
    }

    @Override
    public void onShowCalendar() {
        if (isCalendarShown()) {
            hideCalendar();
        } else {
            showCalendar(shownFilmsDate);
        }
    }

    private void showCalendar(long selectedDate) {
        //set fragment
        if (calendarContainer != null && !isCalendarShown()) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(selectedDate));
                CalendarFragment calendarFragment = CalendarFragment.getInstance(calendar.get(Calendar.DAY_OF_MONTH),
                                                                                 calendar.get(Calendar.MONTH),
                                                                                 calendar.get(Calendar.YEAR));
                transaction.replace(R.id.calendar_container, calendarFragment);
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

    private void switchToTimetable() {
        showFilmsByDate(shownFilmsDate);
    }

    private void switchToCinemas() {
        CinemasListFragment cinemasListFragment = CinemasListFragment.getInstance();
        replaceFragment(cinemasListFragment);
    }

    private void switchToAnnouncements() {
        AnnouncementFilmsListFragment announcementFilmsListFragment = AnnouncementFilmsListFragment.getInstance();
        replaceFragment(announcementFilmsListFragment);
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
    public void onDateSelected(int day, int month, int year) {
        //hide calendar
        hideCalendar();

        //show films by selected date
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        showFilmsByDate(TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis()));
    }

    @Override
    public void onFilmSelected(long filmId) {
        startActivity(FilmDetailsActivity.getStartIntent(this, filmId, shownFilmsDate));
    }

    @Override
    public void onAnnouncementFilmSelected(long filmId) {
        startActivity(AnnouncementFilmDetailsActivity.getStartIntent(this, filmId));
    }

    @Override
    public void onCinemaSelected(long cinemaId) {
        startActivity(CinemaDetailsActivity.getStartIntent(this, cinemaId));
    }
}
