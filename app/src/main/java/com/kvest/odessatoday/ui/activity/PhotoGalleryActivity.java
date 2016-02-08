package com.kvest.odessatoday.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.PhotoGalleryFragment;

/**
 * Created by Kvest on 07.02.2015.
 */
public class PhotoGalleryActivity extends BaseActivity {
    private static final String EXTRA_URLS = "com.kvest.odessatoday.extra.URLS";
    private static final String EXTRA_SELECTED_URL = "com.kvest.odessatoday.extra.SELECTED_URL";

    public static void start(Context context, String[] urls) {
        Intent intent = new Intent(context, PhotoGalleryActivity.class);
        intent.putExtra(EXTRA_URLS, urls);

        context.startActivity(intent);
    }

    public static void start(Context context, String[] urls, int selectedUrl) {
        Intent intent = new Intent(context, PhotoGalleryActivity.class);
        intent.putExtra(EXTRA_URLS, urls);
        intent.putExtra(EXTRA_SELECTED_URL, selectedUrl);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);

        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            String[] urls = intent.getStringArrayExtra(EXTRA_URLS);
            int selectedUrl = intent.getIntExtra(EXTRA_SELECTED_URL, 0);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                PhotoGalleryFragment fragment = PhotoGalleryFragment.newInstance(urls, selectedUrl);
                transaction.add(R.id.fragment_container, fragment);
            } finally {
                transaction.commit();
            }
        }
    }
}
