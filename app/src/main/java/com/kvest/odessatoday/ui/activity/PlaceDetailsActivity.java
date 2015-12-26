package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.PhotoGalleryFragment;
import com.kvest.odessatoday.ui.fragment.PlaceDetailsFragment;

/**
 * Created by kvest on 20.12.15.
 */
public class PlaceDetailsActivity extends BaseActivity implements PlaceDetailsFragment.PlaceDetailsActionsListener {
    private static final String EXTRA_PLACE_ID = "com.kvest.odessatoday.extra.PLACE_ID";
    private static final String EXTRA_PLACE_TYPE = "com.kvest.odessatoday.extra.PLACE_TYPE";

    public static void start(Context context, int placeType, long placeId) {
        Intent intent = new Intent(context, PlaceDetailsActivity.class);
        intent.putExtra(EXTRA_PLACE_ID, placeId);
        intent.putExtra(EXTRA_PLACE_TYPE, placeType);

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
            long placeId = intent.getLongExtra(EXTRA_PLACE_ID, -1);
            int placeType = intent.getIntExtra(EXTRA_PLACE_TYPE, -1);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                PlaceDetailsFragment fragment = PlaceDetailsFragment.getInstance(placeId, placeType);
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
    public void onShowPlaceComments(long cinemaId) {
        //TODO
    }

    @Override
    public void onShowPlacePhotos(String[] photoURLs) {
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
}
