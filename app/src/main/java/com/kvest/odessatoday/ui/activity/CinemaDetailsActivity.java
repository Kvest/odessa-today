package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.CinemaDetailsFragment;
import com.kvest.odessatoday.ui.fragment.CommentsFragment;
import com.kvest.odessatoday.ui.fragment.PhotoGalleryFragment;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.11.14
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class CinemaDetailsActivity extends AppCompatActivity implements CinemaDetailsFragment.CinemaDetailsActionsListener {
    private static final String EXTRA_CINEMA_ID = "com.kvest.odessatoday.extra.CINEMA_ID";

    public static Intent getStartIntent(Context context, long cinemaId) {
        Intent intent = new Intent(context, CinemaDetailsActivity.class);
        intent.putExtra(EXTRA_CINEMA_ID, cinemaId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            long cinemaId = intent.getLongExtra(EXTRA_CINEMA_ID, -1);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                CinemaDetailsFragment cinemaDetailsFragment = CinemaDetailsFragment.getInstance(cinemaId);

                transaction.add(R.id.fragment_container, cinemaDetailsFragment);
            } finally {
                transaction.commit();
            }
        }
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
    public void onShowCinemaComments(long cinemaId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.slide_left_in,  R.anim.slide_left_out, R.anim.slide_right_in, R.anim.slide_right_out);
            CommentsFragment commentsFragment = CommentsFragment.getInstance(Constants.CommentTargetType.CINEMA, cinemaId);
            transaction.replace(R.id.fragment_container, commentsFragment);
            transaction.addToBackStack(null);
        } finally {
            transaction.commit();
        }
    }

    @Override
    public void onShowCinemaPhotos(String[] photoURLs) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.slide_left_in,  R.anim.slide_left_out, R.anim.slide_right_in, R.anim.slide_right_out);
            PhotoGalleryFragment photoGalleryFragment = PhotoGalleryFragment.getInstance(photoURLs);
            transaction.replace(R.id.fragment_container, photoGalleryFragment);
            transaction.addToBackStack(null);
        } finally {
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null && currentFragment instanceof CinemaDetailsFragment) {
            if (((CinemaDetailsFragment)currentFragment).onBackPressed()) {
                return;
            }
        }

        super.onBackPressed();
    }
}
