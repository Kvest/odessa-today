package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.AnnouncementFilmDetailsFragment;
import com.kvest.odessatoday.ui.fragment.CommentsFragment;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.12.14
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementFilmDetailsActivity extends AppCompatActivity implements AnnouncementFilmDetailsFragment.OnShowFilmCommentsListener {
    private static final String EXTRA_FILM_ID = "com.kvest.odessatoday.extra.FILM_ID";

    public static Intent getStartIntent(Context context, long filmId) {
        Intent intent = new Intent(context, AnnouncementFilmDetailsActivity.class);
        intent.putExtra(EXTRA_FILM_ID, filmId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            long filmId = intent.getLongExtra(EXTRA_FILM_ID, -1);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                AnnouncementFilmDetailsFragment fragment = AnnouncementFilmDetailsFragment.getInstance(filmId);
                transaction.add(R.id.fragment_container, fragment);
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
    public void onShowFilmComments(long filmId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.slide_left_in,  R.anim.slide_left_out, R.anim.slide_right_in, R.anim.slide_right_out);
            CommentsFragment commentsFragment = CommentsFragment.getInstance(Constants.CommentTargetType.FILM, filmId);
            transaction.replace(R.id.fragment_container, commentsFragment);
            transaction.addToBackStack(null);
        } finally {
            transaction.commit();
        }
    }
}
