package com.kvest.odessatoday.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.utils.YoutubeApiConstants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 15.11.14
 * Time: 20:23
 * To change this template use File | Settings | File Templates.
 */
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 2000;

    private boolean isCanceled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slash_activity);
Log.d("KVEST_TAG", "key=" + YoutubeApiConstants.YOUTUBE_API_KEY);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isCanceled) {
                    startActivity(MainActivity.getStartIntent(SplashActivity.this));

                    finish();
                }
            }
        }, SPLASH_DELAY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        isCanceled = true;
    }
}
