package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.activity.MainActivity;
import com.kvest.odessatoday.ui.activity.MainMenuController;
import com.kvest.odessatoday.utils.FontUtils;
import com.kvest.odessatoday.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kvest on 08.02.2015.
 */
public class FilmsFragment extends BaseFragment implements MainActivity.ToolbarExtendable, FilmsListFragment.DateChangedListener {
    private static final String FILMS_LIST_DATE_FORMAT_PATTERN = "dd MMMM, cc.";
    private final SimpleDateFormat FILMS_LIST_DATE_FORMAT = new SimpleDateFormat(FILMS_LIST_DATE_FORMAT_PATTERN);

    private static final int FILMS_LIST_FRAGMENT_POSITION = 0;
    private static final int CINEMAS_LIST_FRAGMENT_POSITION = 1;
    private static final int ANNOUNCEMENTS_LIST_FRAGMENT_POSITION = 2;

    private MainMenuController mainMenuController;

    private ViewPager fragmentsPager;
    private RadioGroup categorySelector;
    private FilmsFragmentPagerAdapter pagerAdapter;

    private View toolbarExtension;
    private TextView extensionTitle;
    private View previousDay;
    private View nextDay;

    public static FilmsFragment getInstance() {
        FilmsFragment result = new FilmsFragment();
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.films_fragment, container, false);

        init(root);

        //reset date change listener
        if (savedInstanceState != null) {
            FilmsListFragment filmsListFragment = getFilmsListFragment();
            if (filmsListFragment != null) {
                filmsListFragment.setDateChangedListener(this);
            }
        }

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

    public void showFilmsByDate(long date) {
        FilmsListFragment filmsListFragment = getFilmsListFragment();
        if (filmsListFragment != null) {
            filmsListFragment.changeDate(date);
        }
    }

    @Override
    public void onDateChanged(long date) {
        int previousDayVisibility = View.VISIBLE;
        if (TimeUtils.isCurrentDay(date)) {
            extensionTitle.setText(R.string.odessa_today);
            previousDayVisibility = View.INVISIBLE;
        } else if (TimeUtils.isTomorrow(date)) {
            extensionTitle.setText(R.string.odessa_tomorrow);
        } else {
            extensionTitle.setText(FILMS_LIST_DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(date)));
        }

        previousDay.setVisibility(previousDayVisibility);
    }

    private FilmsListFragment getFilmsListFragment() {
        //workaround
        return (FilmsListFragment)getChildFragmentManager().findFragmentByTag("android:switcher:" + fragmentsPager.getId() + ":" + FILMS_LIST_FRAGMENT_POSITION);
    }

    private void setToolbarExtensionVisibility(int visibility) {
        if (toolbarExtension != null) {
            toolbarExtension.setVisibility(visibility);
        }
    }

    public class FilmsFragmentPagerAdapter extends FragmentPagerAdapter {
        private static final int FRAGMENTS_COUNT = 3;

        public FilmsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case FILMS_LIST_FRAGMENT_POSITION:
                    FilmsListFragment filmsListFragment = FilmsListFragment.getInstance(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    filmsListFragment.setDateChangedListener(FilmsFragment.this);
                    return filmsListFragment;
                case CINEMAS_LIST_FRAGMENT_POSITION:
                    return CinemasListFragment.getInstance();
                case ANNOUNCEMENTS_LIST_FRAGMENT_POSITION:
                    AnnouncementFilmsListFragment announcementFilmsListFragment = AnnouncementFilmsListFragment.getInstance();
                    return announcementFilmsListFragment;
                default :
                    return null;
            }
        }

        @Override
        public int getCount() {
            return FRAGMENTS_COUNT;
        }
    }

    @Override
    public int getExtensionLayoutId() {
        return R.layout.films_fragment_toolbar_extension;
    }

    @Override
    public void setExtensionView(View extension ) {
        toolbarExtension = extension;
        extensionTitle = (TextView) toolbarExtension.findViewById(R.id.title);
        previousDay = toolbarExtension.findViewById(R.id.previous_day);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilmsListFragment filmsListFragment = getFilmsListFragment();
                if (filmsListFragment != null) {
                    filmsListFragment.showPreviousDay();
                }
            }
        });
        nextDay = toolbarExtension.findViewById(R.id.next_day);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilmsListFragment filmsListFragment = getFilmsListFragment();
                if (filmsListFragment != null) {
                    filmsListFragment.showNextDay();
                }
            }
        });

        //set title font
        extensionTitle.setTypeface(FontUtils.getFont(extensionTitle.getContext().getAssets(),
                                                     FontUtils.HELVETICANEUECYR_ROMAN_FONT));
    }
}
