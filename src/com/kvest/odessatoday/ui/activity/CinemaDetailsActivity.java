package com.kvest.odessatoday.ui.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.CinemaDetailsFragment;
import com.kvest.odessatoday.ui.fragment.CommentsFragment;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.11.14
 * Time: 22:44
 * To change this template use File | Settings | File Templates.
 */
public class CinemaDetailsActivity extends Activity implements CinemaDetailsFragment.CinemaDetailsActionsListener {
    private static final String EXTRA_CINEMA_ID = "com.kvest.odessatoday.extra.CINEMA_ID";
    private static final int SHOWN_FRAGMENT_DETAILS = 0;
    private static final int SHOWN_FRAGMENT_COMMENTS = 1;
    private static final int SHOWN_FRAGMENT_PHOTOS = 2;

    private CinemaDetailsFragment cinemaDetailsFragment;
    private CommentsFragment commentsFragment;
    private int showFragmentType;

    public static Intent getStartIntent(Context context, long cinemaId) {
        Intent intent = new Intent(context, CinemaDetailsActivity.class);
        intent.putExtra(EXTRA_CINEMA_ID, cinemaId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        showFragmentType = SHOWN_FRAGMENT_DETAILS;
        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            long cinemaId = intent.getLongExtra(EXTRA_CINEMA_ID, -1);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                cinemaDetailsFragment = CinemaDetailsFragment.getInstance(cinemaId);

                transaction.add(R.id.fragment_container, cinemaDetailsFragment);
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
    public void onShowCinemaComments(long cinemaId) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            if (commentsFragment == null) {
                commentsFragment = CommentsFragment.getInstance(Constants.CommentTargetType.CINEMA, cinemaId);
                transaction.add(R.id.fragment_container, commentsFragment);
            }

            transaction.setCustomAnimations(R.anim.slide_left_in,  R.anim.slide_left_out);

            transaction.show(commentsFragment);
            transaction.hide(cinemaDetailsFragment);
        } finally {
            transaction.commit();
            showFragmentType = SHOWN_FRAGMENT_COMMENTS;
        }
    }

    @Override
    public void onShowCinemaPhotos(long cinemaId) {
        //TODO
    }

    public void backToCinemaDetails() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);

            transaction.show(cinemaDetailsFragment);
            transaction.hide(commentsFragment);
        } finally {
            transaction.commit();
            showFragmentType = SHOWN_FRAGMENT_DETAILS;
        }
    }

    @Override
    public void onBackPressed() {
        if (showFragmentType != SHOWN_FRAGMENT_DETAILS) {
            backToCinemaDetails();
        } else {
            if (!cinemaDetailsFragment.onBackPressed()) {
                super.onBackPressed();
            }
        }
    }
}
