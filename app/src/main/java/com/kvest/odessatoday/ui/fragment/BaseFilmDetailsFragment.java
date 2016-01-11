package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.ui.activity.PhotoGalleryActivity;
import com.kvest.odessatoday.ui.activity.YoutubeFullscreenActivity;
import com.kvest.odessatoday.ui.widget.CommentsCountView;
import com.kvest.odessatoday.ui.widget.RoundNetworkImageView;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;
import com.kvest.odessatoday.utils.YoutubeApiConstants;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.12.14
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseFilmDetailsFragment extends BaseFragment implements YouTubeThumbnailView.OnInitializedListener,
                                                                              YouTubeThumbnailLoader.OnThumbnailLoadedListener {
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private static final String VIDEO_ID_PARAM = "v";

    protected static final String ARGUMENT_FILM_ID = "com.kvest.odessatoday.argument.FILM_ID";

    protected LinearLayout.LayoutParams postersLayoutParams;
    protected NetworkImageView filmPoster;
    protected TextView filmName;
    protected TextView genre;
    protected RatingBar filmRating;
    protected TextView commentsCount;
    protected TextView filmDuration;
    protected TextView description;
    protected TextView director;
    protected TextView actors;
    protected CommentsCountView actionCommentsCount;
    protected LinearLayout imagesContainer;
    private View.OnClickListener onImageClickListener;

    private YouTubeThumbnailLoader youTubeThumbnailLoader;
    private boolean isYoutubeInitInProgress = false;
    private ImageView videoPreview;
    private ImageView videoThumbnailLoadProgress;
    private View videoThumbnailContainer;
    private View videoThumbnailPlay;

    protected String trailerVideoId;
    private String shareTitle, shareText;

    private int noImageResId, loadingImageResId;

    private OnShowFilmCommentsListener onShowFilmCommentsListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onShowFilmCommentsListener = (OnShowFilmCommentsListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity should implements BaseFilmDetailsFragment.OnShowFilmCommentsListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initResources(getActivity());
    }

    protected void initFilmInfoView(View view) {
        //create layout params for posters
        int postersMargin = (int)getResources().getDimension(R.dimen.film_details_posters_margin);
        postersLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                            (int)getResources().getDimension(R.dimen.film_details_posters_height));
        postersLayoutParams.setMargins(postersMargin, 0, 0, 0);

        //store views
        filmPoster = (NetworkImageView) view.findViewById(R.id.film_poster);
        filmPoster.setDefaultImageResId(loadingImageResId);
        filmPoster.setErrorImageResId(noImageResId);
        filmName = (TextView) view.findViewById(R.id.film_name);
        genre = (TextView) view.findViewById(R.id.genre);
        filmRating = (RatingBar) view.findViewById(R.id.film_rating);
        commentsCount = (TextView) view.findViewById(R.id.comments_count);
        filmDuration = (TextView) view.findViewById(R.id.film_duration);
        description = (TextView)view.findViewById(R.id.film_description);
        director = (TextView)view.findViewById(R.id.director);
        actors = (TextView)view.findViewById(R.id.actors);
        imagesContainer = (LinearLayout)view.findViewById(R.id.images_container);
        actionCommentsCount = (CommentsCountView) view.findViewById(R.id.action_comments_count);
        videoPreview = (ImageView) view.findViewById(R.id.video_preview);
        videoThumbnailLoadProgress = (ImageView) view.findViewById(R.id.video_thumbnail_load_progress);
        videoThumbnailContainer = view.findViewById(R.id.video_thumbnail_container);
        videoThumbnailPlay = view.findViewById(R.id.video_thumbnail_play);

        onImageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] urls = new String[]{((NetworkImageView)view).getUrl()};
                PhotoGalleryActivity.start(getActivity(), urls);
            }
        };
        filmPoster.setOnClickListener(onImageClickListener);

        actionCommentsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments();
            }
        });

        view.findViewById(R.id.action_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CacheImageAsyncTask(Long.toString(getFilmId())).execute(filmPoster.getDrawable());
            }
        });

        videoPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoutubeFullscreenActivity.start(getActivity(), trailerVideoId);
            }
        });

        AnimationDrawable frameAnimation = (AnimationDrawable) videoThumbnailLoadProgress.getBackground();
        frameAnimation.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        Activity activity = getActivity();
        if (activity != null && activity.isFinishing()) {
            if (youTubeThumbnailLoader != null) {
                youTubeThumbnailLoader.release();
                youTubeThumbnailLoader = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //one more check. In some situations happens "android.app.ServiceConnectionLeaked: Activity com.kvest.odessatoday.ui.activity.FilmDetailsActivity has leaked ServiceConnection.."
        //try to avoid it
        Activity activity = getActivity();
        if (activity != null && activity.isFinishing()) {
            if (youTubeThumbnailLoader != null) {
                youTubeThumbnailLoader.release();
                youTubeThumbnailLoader = null;
            }
        }

        //stop animation
        AnimationDrawable frameAnimation = (AnimationDrawable) videoThumbnailLoadProgress.getBackground();
        frameAnimation.stop();
    }

    protected void setFilmData(Cursor cursor) {
        if (cursor.moveToFirst()) {
            //set data
            String imageUrl = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.IMAGE));
            filmPoster.setImageUrl(imageUrl, TodayApplication.getApplication().getVolleyHelper().getImageLoader());

            String filmNameValue = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.NAME));
            Activity activity = getActivity();
            if (activity != null) {
                activity.setTitle(filmNameValue);
            }
            filmName.setText(filmNameValue);

            genre.setText(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.GENRE)));
            genre.setVisibility(TextUtils.isEmpty(genre.getText()) ? View.GONE : View.VISIBLE);

            filmRating.setRating(cursor.getFloat(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.RATING)));

            int filmDurationValue = cursor.getInt(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.FILM_DURATION));
            if (filmDurationValue > 0) {
                filmDuration.setText(getString(R.string.film_duration, filmDurationValue));
            } else {
                filmDuration.setText(R.string.film_duration_unknown);
            }

            director.setText(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.DIRECTOR)));
            actors.setText(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.ACTORS)));
            description.setText(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.DESCRIPTION)));

            int commentsCountValue = cursor.getInt(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.COMMENTS_COUNT));
            commentsCount.setText(Integer.toString(commentsCountValue));
            actionCommentsCount.setCommentsCount(commentsCountValue);

            //setTrailer
            setTrailer(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.VIDEO)));

            //add new posters
            String[] postersUrls = Utils.string2Images(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.POSTERS)));
            mergeImages(postersUrls);

            //set visibility for Youtube player and images
            if (TextUtils.isEmpty(trailerVideoId)) {
                if (postersUrls.length == 0) {
                    imagesContainer.setVisibility(View.GONE);
                } else {
                    imagesContainer.setVisibility(View.VISIBLE);
                }
            } else {
                imagesContainer.setVisibility(View.VISIBLE);

                if (youTubeThumbnailLoader == null) {
                    initVideoThumbnailLoading();
                } else {
                    youTubeThumbnailLoader.setVideo(trailerVideoId);
                }
            }

            shareTitle = filmNameValue;
            shareText = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.SHARE_TEXT));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            if (requestCode == Activity.RESULT_OK) {
                // Retry initialization if user performed a recovery action
                initVideoThumbnailLoading();
            } else {
                onVideoThumbnailLoadFailed();
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
        isYoutubeInitInProgress = false;

        Activity activity = getActivity();
        if (activity != null && activity.isFinishing()) {
            youTubeThumbnailLoader.release();
            return;
        }

        this.youTubeThumbnailLoader = youTubeThumbnailLoader;

        youTubeThumbnailLoader.setOnThumbnailLoadedListener(this);

        if (!TextUtils.isEmpty(trailerVideoId)) {
            youTubeThumbnailLoader.setVideo(trailerVideoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
        isYoutubeInitInProgress = false;

        LOGE(Constants.TAG, "YouTubePlayer onInitializationFailure");

        Activity activity = getActivity();
        if (youTubeInitializationResult.isUserRecoverableError() && activity != null) {
            youTubeInitializationResult.getErrorDialog(activity, RECOVERY_DIALOG_REQUEST).show();
        } else {
            onVideoThumbnailLoadFailed();
        }
    }

    @Override
    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String videoId) {
        //transform thumbnail
        Drawable thumbnail = youTubeThumbnailView.getDrawable();

        videoPreview.getLayoutParams().height = postersLayoutParams.height;
        videoPreview.getLayoutParams().width = (int)(((float)thumbnail.getIntrinsicWidth() / (float)thumbnail.getIntrinsicHeight()) * videoPreview.getLayoutParams().height);
        videoPreview.setImageDrawable(thumbnail);

        //hide loading views
        videoThumbnailLoadProgress.setVisibility(View.GONE);
        videoPreview.setVisibility(View.VISIBLE);
        videoThumbnailPlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
        onVideoThumbnailLoadFailed();
    }

    private void onVideoThumbnailLoadFailed() {
        //if only 1 child - it is only video thumbnail and we need to hide the container
        if (imagesContainer.getChildCount() <= 1) {
            imagesContainer.setVisibility(View.GONE);
        } else {
            //hide video thumbnail views
            videoThumbnailContainer.setVisibility(View.GONE);
        }
    }

    private void initVideoThumbnailLoading() {
        if (isYoutubeInitInProgress) {
            return;
        }
        isYoutubeInitInProgress = true;

        videoThumbnailContainer.setVisibility(View.VISIBLE);
        videoThumbnailLoadProgress.setVisibility(View.VISIBLE);
        videoPreview.setVisibility(View.GONE);
        videoThumbnailPlay.setVisibility(View.GONE);

        YouTubeThumbnailView youTubeThumbnailView = ((YouTubeThumbnailView) getView().findViewById(R.id.video_thumbnail));
        youTubeThumbnailView.initialize(YoutubeApiConstants.YOUTUBE_API_KEY, this);
    }

    private void setTrailer(String trailerLink) {
        //parse video id
        String newTrailerVideoId = null;
        if (!TextUtils.isEmpty(trailerLink)) {
            newTrailerVideoId = Uri.parse(trailerLink).getQueryParameter(VIDEO_ID_PARAM);
        }
        if (newTrailerVideoId == null) {
            newTrailerVideoId = "";
        }

        //store video id
        trailerVideoId = newTrailerVideoId;
    }

    private void mergeImages(String[] imageUrls) {
        boolean found;
        for (String imageUrl : imageUrls) {
            found = false;
            for (int i = 1; i < imagesContainer.getChildCount(); ++i) {
                if (imageUrl.equals(((RoundNetworkImageView) imagesContainer.getChildAt(i)).getUrl())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                //add image view
                RoundNetworkImageView imageView = new RoundNetworkImageView(imagesContainer.getContext());
                imageView.setCornersRadiusResource(R.dimen.image_corner_radius);
                imagesContainer.addView(imageView, postersLayoutParams);

                //start loading
                imageView.setImageUrl(imageUrl, TodayApplication.getApplication().getVolleyHelper().getImageLoader());

                //set click listener
                imageView.setOnClickListener(onImageClickListener);
            }
        }
    }

    private void showComments() {
        if (onShowFilmCommentsListener != null) {
            onShowFilmCommentsListener.onShowFilmComments(getFilmId(), filmName.getText().toString(),
                                                          genre.getText().toString(), actionCommentsCount.getCommentsCount(),
                                                          filmRating.getRating());
        }
    }

    private void share(String imageFilePath) {
        Context context = getActivity();
        if (context != null) {
            Intent sharingIntent = new Intent();
            sharingIntent.setAction(Intent.ACTION_SEND);
            sharingIntent.setType(imageFilePath != null ? "image/*" : "text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            if (imageFilePath != null) {
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Utils.getImageContentUri(context, imageFilePath));
            }
            startActivity(Intent.createChooser(sharingIntent, getResources().getText(R.string.share)));
        }
    }

    protected long getFilmId() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_FILM_ID, -1);
        } else {
            return -1;
        }
    }

    public interface OnShowFilmCommentsListener {
        void onShowFilmComments(long filmId, String filmName, String genre, int commentsCount, float rating);
    }

    private void initResources(Context context) {
        // The attributes you want retrieved
        int[] attrs = {R.attr.NoImage, R.attr.LoadingImage};

        // Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            // Fetching the resources defined in the style
            noImageResId = ta.getResourceId(0, -1);
            loadingImageResId = ta.getResourceId(1, -1);
        } finally {
            ta.recycle();
        }
    }

    private class CacheImageAsyncTask extends AsyncTask<Drawable, Void, String> {
        //minimum 3 sec of the caching process to avoid a blinking of the load dialog
        private static final long MIN_PROCESS_DURATION = 3000L;
        private static final String CACHE_IMAGE_FORMAT = ".png";

        private String fileName;
        private ProgressDialog progressDialog;

        public CacheImageAsyncTask(String fileName) {
            super();

            this.fileName = fileName + CACHE_IMAGE_FORMAT;
        }

        @Override
        protected void onPreExecute() {
            //show progress dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.image_caching_progress));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String filePath) {
            //hide progress dialog
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            progressDialog = null;

            share(filePath);
        }

        @Override
        protected String doInBackground(Drawable... params) {
            long startTime = System.currentTimeMillis();

            String result = Utils.saveDrawable(getActivity(), params[0], fileName);

            //artificial delay to avoid a blinking of the load dialog
            long delayDuration = MIN_PROCESS_DURATION - (System.currentTimeMillis() - startTime);
            if (delayDuration > 0) {
                try {
                    Thread.sleep(delayDuration);
                } catch (InterruptedException e) {}
            }

            return result;
        }
    }
}
