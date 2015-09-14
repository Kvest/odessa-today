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
    private static final String EXTRA_PHOTO_URLS = "com.kvest.odessatoday.extra.PHOTO_URLS";

    public static void start(Context context, String[] urls) {
        Intent intent = new Intent(context, PhotoGalleryActivity.class);
        intent.putExtra(EXTRA_PHOTO_URLS, urls);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);

        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            String[] urls = intent.getStringArrayExtra(EXTRA_PHOTO_URLS);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                PhotoGalleryFragment fragment = PhotoGalleryFragment.getInstance(urls);
                transaction.add(R.id.fragment_container, fragment);
            } finally {
                transaction.commit();
            }
        }
    }
}
