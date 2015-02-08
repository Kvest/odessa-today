package com.kvest.odessatoday.ui.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.activity.AnnouncementFilmDetailsActivity;
import com.kvest.odessatoday.ui.activity.CinemaDetailsActivity;
import com.kvest.odessatoday.ui.activity.FilmDetailsActivity;
import com.kvest.odessatoday.utils.TimeUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kvest on 08.02.2015.
 */
public class FilmsFragment extends Fragment implements CalendarFragment.OnDateSelectedListener,
                                                        FilmsListFragment.FilmSelectedListener,
                                                        FilmsListFragment.ShowCalendarListener,
                                                        CinemasListFragment.CinemaSelectedListener,
                                                        AnnouncementFilmsListFragment.AnnouncementFilmSelectedListener {
    private long shownFilmsDate;
    private final Calendar calendar = Calendar.getInstance();
    private FrameLayout calendarContainer;

    private Animation showCalendarAnimation;
    private Animation hideCalendarAnimation;

    public static FilmsFragment getInstance() {
        FilmsFragment result = new FilmsFragment();
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.films_fragment, container, false);

        init(root);

        if (savedInstanceState == null) {
            //set content fragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                shownFilmsDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                FilmsListFragment filmsListFragment = FilmsListFragment.getInstance(shownFilmsDate, true);
                filmsListFragment.setFilmSelectedListener(this);
                filmsListFragment.setShowCalendarListener(this);
                transaction.add(R.id.subfragment_container, filmsListFragment);
            } finally {
                transaction.commit();
            }
        }

        return root;
    }

    private void init(View view) {
        setHasOptionsMenu(true);

        calendarContainer = (FrameLayout)view.findViewById(R.id.calendar_container);
        calendarContainer.setVisibility(View.INVISIBLE);
        calendarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCalendar();
            }
        });

        showCalendarAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        hideCalendarAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
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
        RadioGroup categorySelector = (RadioGroup)view.findViewById(R.id.category_selector);
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

//    @Override
//    public void onBackPressed() {
//        if (isCalendarShown()) {
//            hideCalendar();
//        } else {
//            super.onBackPressed();
//        }
//    }

    private void showFilmsByDate(long date) {
        shownFilmsDate = Math.max(date, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        FilmsListFragment filmsListFragment = FilmsListFragment.getInstance(shownFilmsDate, TimeUtils.isCurrentDay(shownFilmsDate));
        filmsListFragment.setFilmSelectedListener(this);
        filmsListFragment.setShowCalendarListener(this);
        replaceFragment(filmsListFragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.slide_left_in,  R.anim.slide_left_out);
            transaction.replace(R.id.subfragment_container, fragment);
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
                //create calendar fragment
                calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(selectedDate));
                CalendarFragment calendarFragment = CalendarFragment.getInstance(calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.YEAR));
                calendarFragment.setOnDateSelectedListener(this);

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
        cinemasListFragment.setCinemaSelectedListener(this);
        replaceFragment(cinemasListFragment);
    }

    private void switchToAnnouncements() {
        AnnouncementFilmsListFragment announcementFilmsListFragment = AnnouncementFilmsListFragment.getInstance();
        announcementFilmsListFragment.setAnnouncementFilmSelectedListener(this);
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
        startActivity(FilmDetailsActivity.getStartIntent(getActivity(), filmId, shownFilmsDate));
    }

    @Override
    public void onAnnouncementFilmSelected(long filmId) {
        startActivity(AnnouncementFilmDetailsActivity.getStartIntent(getActivity(), filmId));
    }

    @Override
    public void onCinemaSelected(long cinemaId) {
        startActivity(CinemaDetailsActivity.getStartIntent(getActivity(), cinemaId));
    }
}
