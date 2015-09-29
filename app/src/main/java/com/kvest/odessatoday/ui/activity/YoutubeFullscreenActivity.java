package com.kvest.odessatoday.ui.activity;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.utils.YoutubeApiConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;


/**
 * Created by kvest on 28.09.15.
 */
public class YoutubeFullscreenActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private static final String EXTRA_VIDEO_ID = "com.kvest.odessatoday.extra.VIDEO_ID";
    public static void start(Context context, String videoId) {
        Intent intent = new Intent(context, YoutubeFullscreenActivity.class);
        intent.putExtra(EXTRA_VIDEO_ID, videoId);
        context.startActivity(intent);
    }

    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_fullscreen_activity);

        videoId = getIntent().getStringExtra(EXTRA_VIDEO_ID);

        initYoutubePlayer();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setFullscreen(true);
        youTubePlayer.loadVideo(videoId);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST && requestCode == Activity.RESULT_OK) {
            // Retry initialization if user performed a recovery action
            initYoutubePlayer();
        }
    }

    private void initYoutubePlayer() {
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView)findViewById(R.id.player);
        youTubePlayerView.initialize(YoutubeApiConstants.YOUTUBE_API_KEY, this);
    }
}
