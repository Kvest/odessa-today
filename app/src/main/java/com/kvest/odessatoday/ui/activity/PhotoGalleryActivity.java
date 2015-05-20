package com.kvest.odessatoday.ui.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.fragment.PhotoGalleryFragment;

/**
 * Created by Kvest on 07.02.2015.
 */
public class PhotoGalleryActivity extends Activity {
    private static final String EXTRA_PHOTO_URLS = "com.kvest.odessatoday.extra.PHOTO_URLS";

    public static Intent getStartIntent(Context context, String[] urls) {
        Intent intent = new Intent(context, PhotoGalleryActivity.class);
        intent.putExtra(EXTRA_PHOTO_URLS, urls);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_container);

        Intent intent = getIntent();
        if (savedInstanceState == null && intent != null) {
            String[] urls = intent.getStringArrayExtra(EXTRA_PHOTO_URLS);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                PhotoGalleryFragment fragment = PhotoGalleryFragment.getInstance(urls);
                transaction.add(R.id.fragment_container, fragment);
            } finally {
                transaction.commit();
            }
        }
    }
}
