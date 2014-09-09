package com.kvest.odessatoday.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.CommentsFragment;
import com.kvest.odessatoday.ui.fragment.FilmDetailsFragment;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 19.07.14
 * Time: 22:57
 * To change this template use File | Settings | File Templates.
 */
public class FilmDetailsActivity extends TodayBaseActivity implements FilmDetailsFragment.OnShowFilmCommentsListener {
    private static final String EXTRA_FILM_ID = "com.kvest.odessatoday.extra.FILM_ID";
    private static final String EXTRA_TIMETABLE_DATE = "com.kvest.odessatoday.extra.FILM_TIMETABLE_DATE";

    private FilmDetailsFragment filmDetailsFragment;
    private CommentsFragment commentsFragment;
    private boolean commentsShown;

    public static Intent getStartIntent(Context context, long filmId, long timetableDate) {
        Intent intent = new Intent(context, FilmDetailsActivity.class);
        intent.putExtra(EXTRA_FILM_ID, filmId);
        intent.putExtra(EXTRA_TIMETABLE_DATE, timetableDate);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        commentsShown = false;
        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            long filmId = intent.getLongExtra(EXTRA_FILM_ID, -1);
            long timetableDate = intent.getLongExtra(EXTRA_TIMETABLE_DATE, 0);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                filmDetailsFragment = FilmDetailsFragment.getInstance(filmId, timetableDate);

                transaction.add(R.id.fragment_container, filmDetailsFragment);
            } finally {
                transaction.commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShowFilmComments(long filmId) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            if (commentsFragment == null) {
                commentsFragment = CommentsFragment.getInstance(Constants.CommentTargetType.FILM, filmId);
                transaction.add(R.id.fragment_container, commentsFragment);
            }

            transaction.setCustomAnimations(R.anim.slide_left_in,  R.anim.slide_left_out);

            transaction.show(commentsFragment);
            transaction.hide(filmDetailsFragment);
        } finally {
            transaction.commit();
            commentsShown = true;
        }
    }

    public void backToFilmDetails() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);

            transaction.show(filmDetailsFragment);
            transaction.hide(commentsFragment);
        } finally {
            transaction.commit();
            commentsShown = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (commentsShown) {
            backToFilmDetails();
        } else {
            super.onBackPressed();
        }
    }
}
