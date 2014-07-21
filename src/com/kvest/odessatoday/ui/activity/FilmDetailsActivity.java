package com.kvest.odessatoday.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.FilmDetailsFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 19.07.14
 * Time: 22:57
 * To change this template use File | Settings | File Templates.
 */
public class FilmDetailsActivity extends TodayBaseActivity {
    private static final String EXTRA_FILM_ID = "com.kvest.odessatoday.extra.FILM_ID";

    public static Intent getStartIntent(Context context, long filmId) {
        Intent intent = new Intent(context, FilmDetailsActivity.class);
        intent.putExtra(EXTRA_FILM_ID, filmId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        getActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            long filmId = intent.getLongExtra(EXTRA_FILM_ID, -1);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                transaction.add(R.id.fragment_container, FilmDetailsFragment.getInstance(filmId));
            }  finally {
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
}
