package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.CalendarFragment;
import com.kvest.odessatoday.ui.fragment.CinemaDetailsFragment;
import com.kvest.odessatoday.ui.fragment.CommentsFragment;
import com.kvest.odessatoday.ui.fragment.PhotoSlideFragment;
import com.kvest.odessatoday.utils.Constants;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.11.14
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class CinemaDetailsActivity extends BaseActivity implements CinemaDetailsFragment.CinemaDetailsActionsListener,
                                                                   CalendarFragment.CalendarEventsListener {
    private static final String EXTRA_CINEMA_ID = "com.kvest.odessatoday.extra.CINEMA_ID";

    private final Calendar calendar = Calendar.getInstance();
    private FrameLayout calendarContainer;

    private Animation showCalendarAnimation;
    private Animation hideCalendarAnimation;

    public static void start(Context context, long cinemaId) {
        Intent intent = new Intent(context, CinemaDetailsActivity.class);
        intent.putExtra(EXTRA_CINEMA_ID, cinemaId);

        context.startActivity(intent);
    }

    public static void startClearTop(Context context, long cinemaId) {
        Intent intent = new Intent(context, CinemaDetailsActivity.class);
        intent.putExtra(EXTRA_CINEMA_ID, cinemaId);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cinema_details_activity);

        //setup action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupToolbar(toolbar);

        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            long cinemaId = intent.getLongExtra(EXTRA_CINEMA_ID, -1);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                CinemaDetailsFragment cinemaDetailsFragment = CinemaDetailsFragment.newInstance(cinemaId);

                transaction.add(R.id.fragment_container, cinemaDetailsFragment);
            } finally {
                transaction.commit();
            }
        }

        setupCalendar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                finish();
            } else {
                getFragmentManager().popBackStack();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShowCinemaComments(long cinemaId, String cinemaName, int commentsCount, float rating) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.slide_left_in,  R.anim.slide_left_out, R.anim.slide_right_in, R.anim.slide_right_out);
            CommentsFragment commentsFragment = CommentsFragment.newInstance(cinemaId, Constants.CommentTargetType.CINEMA,
                                                                             cinemaName, getString(R.string.menu_cinema).toLowerCase(),
                                                                             commentsCount, rating, false);
            transaction.replace(R.id.fragment_container, commentsFragment);
            transaction.addToBackStack(null);
        } finally {
            transaction.commit();
        }
    }

    @Override
    public void onShowCinemaPhotos(String[] photoURLs) {
        PhotoGalleryActivity.start(this, photoURLs, getTitle().toString());
    }

    @Override
    public void onShowCalendar(long withDate) {
        if (isCalendarShown()) {
            hideCalendar();
        } else {
            showCalendar(withDate);
        }
    }

    @Override
    public void onBackPressed() {
        if (isCalendarShown()) {
            hideCalendar();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onCalendarClose() {
        hideCalendar();
    }

    @Override
    public void onDateSelected(int day, int month, int year) {
        //hide calendar
        hideCalendar();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null && currentFragment instanceof CinemaDetailsFragment) {
            //show films by selected date
            calendar.clear();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            ((CinemaDetailsFragment)currentFragment).setShownDate(TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis()));
        }
    }

    private void setupCalendar() {
        calendarContainer = (FrameLayout)findViewById(R.id.calendar_fragment_container);
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
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                calendarContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void showCalendar(long withDate) {
        //set fragment
        if (calendarContainer != null && !isCalendarShown()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(withDate));
                CalendarFragment calendarFragment = CalendarFragment.newInstance(calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.YEAR));
                transaction.replace(R.id.calendar_fragment_container, calendarFragment);
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
}
