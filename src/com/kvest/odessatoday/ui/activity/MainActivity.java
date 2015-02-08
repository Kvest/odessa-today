package com.kvest.odessatoday.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.receiver.NetworkChangeReceiver;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.fragment.*;

public class MainActivity extends Activity implements MainMenuFragment.MainMenuItemSelectedListener,
                                                        CinemasListFragment.CinemaSelectedListener{
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

        init();

        if (savedInstanceState == null) {
            //set menu fragment
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                MainMenuFragment mainMenuFragment = MainMenuFragment.getInstance(MainMenuFragment.MENU_FILMS_ID);
                transaction.add(R.id.menu_container, mainMenuFragment);
            } finally {
                transaction.commit();
            }

            //show films fragment
            transaction = getFragmentManager().beginTransaction();
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
    }

    //TODO
//    @Override
//    public void onBackPressed() {
//        if (isCalendarShown()) {
//            hideCalendar();
//        } else {
//            super.onBackPressed();
//        }
//    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            transaction.replace(R.id.fragment_container, fragment);
        } finally {
            transaction.commit();
        }

        //workaround - we need to update options menu
        invalidateOptionsMenu();
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
