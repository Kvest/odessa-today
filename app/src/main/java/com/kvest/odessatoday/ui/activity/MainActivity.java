package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.receiver.NetworkChangeReceiver;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.fragment.*;
import com.kvest.odessatoday.utils.Constants;

public class MainActivity extends AppCompatActivity implements MainMenuFragment.MainMenuItemSelectedListener,
                                                        CinemasListFragment.CinemaSelectedListener,
                                                        MainMenuController{
    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    private SlidingMenu slidingMenu;

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
        setContentView(R.layout.fragment_container);

        //setup action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            } finally {
                transaction.commit();
            }

            //set title
            setTitle(R.string.menu_films);
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

        if (isFinishing()) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

    private void init() {
        //setup sliding menu
        slidingMenu = new SlidingMenu(this, SlidingMenu.SLIDING_WINDOW);
        slidingMenu.setMenu(R.layout.menu_layout);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindOffsetRes(R.dimen.main_menu_behind_offset);
        slidingMenu.setFadeEnabled(false);

        //setup theme switcher
        SwitchCompat themeSwitch = (SwitchCompat) findViewById(R.id.theme_switcher);
        themeSwitch.setTrackDrawable(getResources().getDrawable(R.drawable.theme_switcher_track));
        themeSwitch.setChecked(TodayApplication.getApplication().getCurrentTheme() == Constants.ThemeType.DAY);
        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TodayApplication.getApplication().setCurrentTheme(Constants.ThemeType.DAY);
                } else {
                    TodayApplication.getApplication().setCurrentTheme(Constants.ThemeType.NIGHT);
                }
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
    public void onBackPressed() {
        boolean processed = false;
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof OnBackPressedListener) {
            processed = ((OnBackPressedListener)fragment).onBackPressed();
        }

        if (!processed) {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.fade_in, 0);
            transaction.replace(R.id.fragment_container, fragment);
        } finally {
            transaction.commit();
        }
    }

    @Override
    public void onMainMenuItemSelected(int menuItemId) {
        switch (menuItemId) {
            case MainMenuFragment.MENU_FILMS_ID :
                replaceFragment(FilmsFragment.getInstance());
                setTitle(R.string.menu_films);
                break;
            case MainMenuFragment.MENU_CINEMA_ID :
                replaceFragment(CinemasListFragment.getInstance());
                setTitle(R.string.menu_cinema);
                break;
        }

        //close menu
        slidingMenu.showContent();
    }

    @Override
    public void onCinemaSelected(long cinemaId) {
        startActivity(CinemaDetailsActivity.getStartIntent(this, cinemaId));
    }
}
