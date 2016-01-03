package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
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
public class FilmDetailsActivity extends BaseActivity implements FilmDetailsFragment.OnShowFilmCommentsListener {
    private static final String EXTRA_FILM_ID = "com.kvest.odessatoday.extra.FILM_ID";
    private static final String EXTRA_TIMETABLE_DATE = "com.kvest.odessatoday.extra.FILM_TIMETABLE_DATE";

    public static void start(Context context, long filmId, long timetableDate) {
        Intent intent = new Intent(context, FilmDetailsActivity.class);
        intent.putExtra(EXTRA_FILM_ID, filmId);
        intent.putExtra(EXTRA_TIMETABLE_DATE, timetableDate);

        context.startActivity(intent);
    }

    public static void startClearTop(Context context, long filmId, long timetableDate) {
        Intent intent = new Intent(context, FilmDetailsActivity.class);
        intent.putExtra(EXTRA_FILM_ID, filmId);
        intent.putExtra(EXTRA_TIMETABLE_DATE, timetableDate);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_with_toolbar_layout);

        //setup action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupToolbar(toolbar);

        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            long filmId = intent.getLongExtra(EXTRA_FILM_ID, -1);
            long timetableDate = intent.getLongExtra(EXTRA_TIMETABLE_DATE, 0);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                FilmDetailsFragment filmDetailsFragment = FilmDetailsFragment.getInstance(filmId, timetableDate);
                transaction.add(R.id.fragment_container, filmDetailsFragment);
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
            CommentsFragment commentsFragment = CommentsFragment.getInstance(filmId, Constants.CommentTargetType.FILM);
            transaction.replace(R.id.fragment_container, commentsFragment);
            transaction.addToBackStack(null);
        } finally {
            transaction.commit();
        }
    }
}
