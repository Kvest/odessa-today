package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.PhotoGalleryFragment;
import com.kvest.odessatoday.ui.fragment.PhotoSlideFragment;

/**
 * Created by roman on 3/18/16.
 */
public class PhotoGalleryActivity extends BaseActivity implements PhotoGalleryFragment.OnPhotoSelectedListener {
    private static final String EXTRA_URLS = "com.kvest.odessatoday.extra.URLS";
    private static final String EXTRA_TITLE = "com.kvest.odessatoday.extra.TITLE";

    public static void start(Context context, String[] urls, String title) {
        Intent intent = new Intent(context, PhotoGalleryActivity.class);
        intent.putExtra(EXTRA_URLS, urls);
        intent.putExtra(EXTRA_TITLE, title);

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
            String[] urls = intent.getStringArrayExtra(EXTRA_URLS);
            String title = intent.getStringExtra(EXTRA_TITLE);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                PhotoGalleryFragment fragment = PhotoGalleryFragment.newInstance(urls, title);
                transaction.add(R.id.fragment_container, fragment);
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
    public void onPhotoSelected(String[] photoURLs, int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            PhotoSlideFragment fragment = PhotoSlideFragment.newInstance(photoURLs, position);
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
        } finally {
            transaction.commit();
        }
    }
}
