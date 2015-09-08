package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.activity.AnnouncementFilmDetailsActivity;
import com.kvest.odessatoday.ui.activity.FilmDetailsActivity;
import com.kvest.odessatoday.ui.activity.MainActivity;
import com.kvest.odessatoday.ui.activity.MainMenuController;
import com.kvest.odessatoday.ui.activity.OnBackPressedListener;
import com.kvest.odessatoday.utils.FontUtils;
import com.kvest.odessatoday.utils.TimeUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kvest on 08.02.2015.
 */
public class FilmsFragment extends BaseFragment implements CalendarFragment.OnDateSelectedListener,
                                                        FilmsListFragment.FilmSelectedListener,
                                                        FilmsListFragment.ShowCalendarListener,
                                                        AnnouncementFilmsListFragment.AnnouncementFilmSelectedListener,
                                                        MainActivity.ToolbarExtendable,
                                                        OnBackPressedListener {
    private static final int FILMS_LIST_FRAGMENT_POSITION = 0;
    private static final int CINEMAS_LIST_FRAGMENT_POSITION = 1;
    private static final int ANNOUNCEMENTS_LIST_FRAGMENT_POSITION = 2;

    private long shownFilmsDate;
    private final Calendar calendar = Calendar.getInstance();
    private FrameLayout calendarContainer;

    private Animation showCalendarAnimation;
    private Animation hideCalendarAnimation;

    private MainMenuController mainMenuController;

    private ViewPager fragmentsPager;
    private RadioGroup categorySelector;
    private FilmsFragmentPagerAdapter pagerAdapter;

    private View toolbarExtension;

    public static FilmsFragment getInstance() {
        FilmsFragment result = new FilmsFragment();
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.films_fragment, container, false);

        init(root);

        return root;
    }

    private void init(View view) {
        Context context = getActivity();
        fragmentsPager = (ViewPager) view.findViewById(R.id.view_pager);
        fragmentsPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pageNumber) {
                if (mainMenuController != null) {
                    if (pageNumber == 0) {
                        //unlock menu
                        mainMenuController.unlockMenuSliding();
                    } else {
                        //lock menu
                        mainMenuController.lockMenuSliding();
                    }
                }

                //set selected tab
                switch (pageNumber) {
                    case FILMS_LIST_FRAGMENT_POSITION:
                        categorySelector.check(R.id.selector_timetable);
                        break;
                    case CINEMAS_LIST_FRAGMENT_POSITION:
                        categorySelector.check(R.id.selector_cinemas);
                        break;
                    case ANNOUNCEMENTS_LIST_FRAGMENT_POSITION:
                        categorySelector.check(R.id.selector_announcements);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        pagerAdapter = new FilmsFragmentPagerAdapter(getChildFragmentManager());
        fragmentsPager.setAdapter(pagerAdapter);

        calendarContainer = (FrameLayout)view.findViewById(R.id.calendar_container);
        calendarContainer.setVisibility(View.INVISIBLE);
        calendarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCalendar();
            }
        });

        showCalendarAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        hideCalendarAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
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
        categorySelector = (RadioGroup)view.findViewById(R.id.category_selector);
        categorySelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.selector_timetable:
                        fragmentsPager.setCurrentItem(FILMS_LIST_FRAGMENT_POSITION, true);
                        setToolbarExtensionVisibility(View.VISIBLE);
                        break;
                    case R.id.selector_cinemas:
                        fragmentsPager.setCurrentItem(CINEMAS_LIST_FRAGMENT_POSITION, true);
                        setToolbarExtensionVisibility(View.GONE);
                        break;
                    case R.id.selector_announcements:
                        fragmentsPager.setCurrentItem(ANNOUNCEMENTS_LIST_FRAGMENT_POSITION, true);
                        setToolbarExtensionVisibility(View.GONE);
                        break;
                }
            }
        });

        //set typeface for the selector's RadioButtons
        Typeface helveticaneuecyrBold = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_BOLD_FONT);
        ((RadioButton)view.findViewById(R.id.selector_timetable)).setTypeface(helveticaneuecyrBold);
        ((RadioButton)view.findViewById(R.id.selector_cinemas)).setTypeface(helveticaneuecyrBold);
        ((RadioButton)view.findViewById(R.id.selector_announcements)).setTypeface(helveticaneuecyrBold);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mainMenuController = (MainMenuController) activity;
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mainMenuController != null) {
            mainMenuController.unlockMenuSliding();
        }
        mainMenuController = null;
    }

    @Override
    public boolean onBackPressed() {
        if (isCalendarShown()) {
            hideCalendar();
            return true;
        } else {
            return false;
        }
    }

    private void showFilmsByDate(long date) {

        shownFilmsDate = Math.max(date, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        FilmsListFragment filmsListFragment = pagerAdapter.getFilmsListFragmentCache();
        if (filmsListFragment != null) {
            filmsListFragment.changeDate(shownFilmsDate, TimeUtils.isCurrentDay(shownFilmsDate));
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

    private void setToolbarExtensionVisibility(int visibility) {
        if (toolbarExtension != null) {
            toolbarExtension.setVisibility(visibility);
        }
    }

    private void showCalendar(long selectedDate) {
        //set fragment
        if (calendarContainer != null && !isCalendarShown()) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
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

    public class FilmsFragmentPagerAdapter extends FragmentPagerAdapter {
        private static final int FRAGMENTS_COUNT = 3;

        private FilmsListFragment filmsListFragmentCache;

        public FilmsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case FILMS_LIST_FRAGMENT_POSITION:
                    shownFilmsDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                    filmsListFragmentCache = FilmsListFragment.getInstance(shownFilmsDate, true);
                    filmsListFragmentCache.setFilmSelectedListener(FilmsFragment.this);
                    filmsListFragmentCache.setShowCalendarListener(FilmsFragment.this);
                    return filmsListFragmentCache;
                case CINEMAS_LIST_FRAGMENT_POSITION:
                    return CinemasListFragment.getInstance();
                case ANNOUNCEMENTS_LIST_FRAGMENT_POSITION:
                    AnnouncementFilmsListFragment announcementFilmsListFragment = AnnouncementFilmsListFragment.getInstance();
                    announcementFilmsListFragment.setAnnouncementFilmSelectedListener(FilmsFragment.this);
                    return announcementFilmsListFragment;
                default :
                    return null;
            }
        }

        @Override
        public int getCount() {
            return FRAGMENTS_COUNT;
        }

        public FilmsListFragment getFilmsListFragmentCache() {
            return filmsListFragmentCache;
        }
    }

    @Override
    public int getExtensionLayoutId() {
        return R.layout.films_fragment_toolbar_extension;
    }

    @Override
    public void setExtensionView(View extension ) {
        toolbarExtension = extension;
    }
}
