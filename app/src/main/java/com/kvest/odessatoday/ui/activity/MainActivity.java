package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class MainActivity extends BaseActivity implements MainMenuFragment.MainMenuItemSelectedListener,
                                                        CinemasListFragment.CinemaSelectedListener,
                                                        MainMenuController{
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
        setContentView(R.layout.fragment_container_with_toolbar_layout);

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

    private Drawable getMainMenuIcon() {
        //get color
        int[] attrs = {R.attr.ABIconColor};
        TypedArray ta = obtainStyledAttributes(attrs);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_menu);
        drawable.setColorFilter(new PorterDuffColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_IN));

        ta.recycle();

        return drawable;
    }

    @Override
    public void onCinemaSelected(long cinemaId) {
        CinemaDetailsActivity.start(this, cinemaId);
    }

    public interface ToolbarExtendable {
        int getExtensionLayoutId();
        void setExtensionView(View view);
    }
}
