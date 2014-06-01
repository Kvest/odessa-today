package com.kvest.odessatoday.ui.activity;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.ui.fragment.FilmsFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyActivity extends TodayBaseActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        test();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            try {
                FilmsFragment filmsFragment = FilmsFragment.getInstance(true);
                transaction.add(R.id.fragment_container, filmsFragment);
            } finally {
                transaction.commit();
            }
        }
    }

    private void test() {
        Uri filmsUri = Uri.withAppendedPath(TodayProviderContract.BASE_CONTENT_URI, TodayProviderContract.FILMS_PATH);
        getContentResolver().delete(filmsUri, null, null);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss");
//        Date d = new Date(1401310800000L);
//        Log.d("KVEST_TAG", "d=" + sdf.format(d));
    }
}
