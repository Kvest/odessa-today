package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.receiver.NetworkChangeReceiver;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.fragment.*;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity implements MainMenuFragment.MainMenuItemSelectedListener,
                                                        CinemasListFragment.CinemaSelectedListener,
                                                        PlacesListFragment.PlaceSelectedListener,
                                                        ShowCalendarListener,
                                                        FilmsListFragment.FilmSelectedListener,
                                                        AnnouncementFilmsListFragment.AnnouncementFilmSelectedListener,
                                                        MainMenuController, CalendarFragment.CalendarEventsListener {
    private static final String KEY_TITLE = "com.kvest.odessatoday.key.TITLE";
    private static final String CALENDAR_FRAGMENT_TAG = "calendar";

    private Calendar calendar = Calendar.getInstance();
    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    private SlidingMenu slidingMenu;
    private Toolbar toolbar;
    private View toolbarExtention;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        context.startActivity(intent);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //setup action bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getMainMenuIcon());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupToolbar(toolbar);

        init();

        if (savedInstanceState == null) {
            //set menu fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                MainMenuFragment mainMenuFragment = MainMenuFragment.getInstance(MainMenuFragment.MENU_FILMS_ID);
                transaction.add(R.id.menu_container, mainMenuFragment);
            } finally {
                transaction.commit();
            }

            //show films fragment
            transaction = getSupportFragmentManager().beginTransaction();
            try {
                FilmsFragment filmsFragment = FilmsFragment.getInstance();
                transaction.add(R.id.fragment_container, filmsFragment);
                updateToolbarForFragment(filmsFragment);
            } finally {
                transaction.commit();
            }

            //set title
            setTitle(R.string.menu_films);
        } else {
            Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            updateToolbarForFragment(activeFragment);

            if (savedInstanceState.containsKey(KEY_TITLE)) {
                setTitle(savedInstanceState.getString(KEY_TITLE));
            }
        }

        //sync data
        NetworkService.sync(getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            slidingMenu.toggle();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(networkChangeReceiver);
    }

    private void init() {
        //setup sliding menu
        slidingMenu = new SlidingMenu(this, SlidingMenu.SLIDING_WINDOW);
        slidingMenu.setMenu(R.layout.menu_layout);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.main_menu_behind_offset);
        slidingMenu.setFadeEnabled(false);

        //setup theme switcher
        Switch themeSwitch = (Switch) findViewById(R.id.theme_switcher);
        //themeSwitch.setTrackDrawable(getResources().getDrawable(R.drawable.theme_switcher_track));
        themeSwitch.setChecked(TodayApplication.getApplication().getCurrentTheme() == Constants.ThemeType.DAY);
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TodayApplication.getApplication().setCurrentTheme(Constants.ThemeType.DAY);
                } else {
                    TodayApplication.getApplication().setCurrentTheme(Constants.ThemeType.NIGHT);
                }

                recreate();
            }
        });

        findViewById(R.id.close) .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });
    }

    @Override
    public void lockMenuSliding() {
        slidingMenu.setSlidingEnabled(false);
    }

    @Override
    public void unlockMenuSliding() {
        slidingMenu.setSlidingEnabled(true);
    }

    @Override
    public void onShowCalendar(long selectedDate) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            //create calendar fragment
            calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(selectedDate));
            CalendarFragment calendarFragment = CalendarFragment.getInstance(calendar.get(Calendar.DAY_OF_MONTH),
                                                                             calendar.get(Calendar.MONTH),
                                                                             calendar.get(Calendar.YEAR));

            transaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_up, R.anim.slide_down, R.anim.slide_up);
            transaction.replace(R.id.calendar_fragment_container, calendarFragment);
            transaction.addToBackStack(CALENDAR_FRAGMENT_TAG);
        } finally {
            transaction.commit();
        }
    }

    @Override
    public void onCalendarClose() {
        hideCalendar();
    }

    @Override
    public void onDateSelected(int day, int month, int year) {
        hideCalendar();

        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (activeFragment instanceof DateSelectionListener) {
            //show films by selected date
            calendar.clear();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            ((DateSelectionListener)activeFragment).onDateSelected(TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis()));
        }
    }

    @Override
    public void onFilmSelected(long filmId, long date) {
        FilmDetailsActivity.start(this, filmId, date);
    }

    @Override
    public void onAnnouncementFilmSelected(long filmId) {
        AnnouncementFilmDetailsActivity.start(this, filmId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_TITLE, getTitle().toString());
    }

    private void hideCalendar() {
        getSupportFragmentManager().popBackStack(CALENDAR_FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.fade_in, 0);
            transaction.replace(R.id.fragment_container, fragment);
        } finally {
            transaction.commit();
        }

        updateToolbarForFragment(fragment);
    }

    private void updateToolbarForFragment(Fragment fragment) {
        //remove previous extension
        if (toolbarExtention != null) {
            toolbar.removeView(toolbarExtention);
            toolbarExtention = null;
        }

        if (fragment instanceof ToolbarExtendable) {
            ToolbarExtendable toolbarExtendable = (ToolbarExtendable) fragment;
            toolbarExtention = LayoutInflater.from(this).inflate(toolbarExtendable.getExtensionLayoutId(), toolbar, false);
            toolbar.addView(toolbarExtention);
            toolbarExtendable.setExtensionView(toolbarExtention);
        }
    }

    @Override
    public void onMainMenuItemSelected(int menuItemId) {
        //workaround - need to clear options menu or sometimes it will have options from previous fragment
        toolbar.getMenu().clear();

        switch (menuItemId) {
            case MainMenuFragment.MENU_FILMS_ID :
                replaceFragment(FilmsFragment.getInstance());
                setTitle(R.string.menu_films);
                break;
            case MainMenuFragment.MENU_CONCERT_ID :
                replaceFragment(EventsWithPlacesFragment.newInstance(Constants.EventType.CONCERT,
                                                                     Constants.PlaceType.CONCERT_HALL));
                setTitle(R.string.menu_concert);
                break;
            case MainMenuFragment.MENU_PARTY_ID :
                replaceFragment(EventsWithPlacesFragment.newInstance(Constants.EventType.PARTY,
                                                                     Constants.PlaceType.CLUB));
                setTitle(R.string.menu_party);
                break;
            case MainMenuFragment.MENU_SPECTACLE_ID :
                replaceFragment(EventsWithPlacesFragment.newInstance(Constants.EventType.SPECTACLE,
                                                                     Constants.PlaceType.THEATRE));
                setTitle(R.string.menu_spectacle);
                break;
            case MainMenuFragment.MENU_EXHIBITION_ID :
                replaceFragment(EventsWithPlacesFragment.newInstance(Constants.EventType.EXHIBITION,
                                                                     Constants.PlaceType.MUSEUM));
                setTitle(R.string.menu_exhibition);
                break;
            case MainMenuFragment.MENU_SPORT_ID :
                replaceFragment(EventsListFragment.newInstance(Constants.EventType.SPORT,
                                                               TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                setTitle(R.string.menu_sport);
                break;
            case MainMenuFragment.MENU_WORKSHOP_ID :
                replaceFragment(EventsListFragment.newInstance(Constants.EventType.WORKSHOP,
                                                               TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                setTitle(R.string.menu_workshop);
                break;
            case MainMenuFragment.MENU_CINEMA_ID :
                replaceFragment(CinemasListFragment.getInstance());
                setTitle(R.string.menu_cinema);
                break;
            case MainMenuFragment.MENU_THEATRE_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.THEATRE));
                setTitle(R.string.menu_theatre);
                break;
            case MainMenuFragment.MENU_CONCERT_HALL_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.CONCERT_HALL));
                setTitle(R.string.menu_concert_hall);
                break;
            case MainMenuFragment.MENU_CLUB_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.CLUB));
                setTitle(R.string.menu_club);
                break;
            case MainMenuFragment.MENU_MUSEUM_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.MUSEUM));
                setTitle(R.string.menu_museum);
                break;
            case MainMenuFragment.MENU_GALLERY_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.GALLERY));
                setTitle(R.string.menu_gallery);
                break;
            case MainMenuFragment.MENU_ZOO_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.ZOO));
                setTitle(R.string.menu_zoo);
                break;
            case MainMenuFragment.MENU_QUEST_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.QUEST));
                setTitle(R.string.menu_quest);
                break;
            case MainMenuFragment.MENU_RESTAURANT_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.RESTAURANT));
                setTitle(R.string.menu_restaurant);
                break;
            case MainMenuFragment.MENU_CAFE_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.CAFE));
                setTitle(R.string.menu_cafe);
                break;
            case MainMenuFragment.MENU_PIZZA_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.PIZZA));
                setTitle(R.string.menu_pizza);
                break;
            case MainMenuFragment.MENU_SUSHI_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.SUSHI));
                setTitle(R.string.menu_sushi);
                break;
            case MainMenuFragment.MENU_KARAOKE_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.KARAOKE));
                setTitle(R.string.menu_karaoke);
                break;
            case MainMenuFragment.MENU_SKATING_RINK_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.SKATING_RINK));
                setTitle(R.string.menu_skating_rink);
                break;
            case MainMenuFragment.MENU_BOWLING_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.BOWLING));
                setTitle(R.string.menu_bowling);
                break;
            case MainMenuFragment.MENU_BILLIARD_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.BILLIARD));
                setTitle(R.string.menu_billiard);
                break;
            case MainMenuFragment.MENU_SAUNA_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.SAUNA));
                setTitle(R.string.menu_sauna);
                break;
            case MainMenuFragment.MENU_BATH_ID :
                replaceFragment(PlacesListFragment.newInstance(Constants.PlaceType.BATH));
                setTitle(R.string.menu_bath);
                break;
        }

        hideCalendar();

        //close menu
        slidingMenu.showContent();
    }

    private Drawable getMainMenuIcon() {
        //get color
        int[] attrs = {R.attr.ABIconColor};
        TypedArray ta = obtainStyledAttributes(attrs);

        try {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_menu);

            Utils.setDrawablesColor(ta.getColor(0, Color.WHITE), drawable);

            return drawable;
        } finally {
            ta.recycle();
        }
    }

    @Override
    public void onCinemaSelected(long cinemaId) {
        CinemaDetailsActivity.start(this, cinemaId);
    }

    @Override
    public void onPlaceSelected(long placeId) {
        //TODO
    }

    public interface ToolbarExtendable {
        int getExtensionLayoutId();
        void setExtensionView(View view);
    }
}
