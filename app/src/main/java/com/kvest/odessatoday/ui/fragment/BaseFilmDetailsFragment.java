package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.FilmWithTimetable;
import com.kvest.odessatoday.io.network.notification.LoadCommentsNotification;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.ui.activity.PhotoGalleryActivity;
import com.kvest.odessatoday.ui.activity.YoutubeFullscreenActivity;
import com.kvest.odessatoday.ui.widget.CommentsCountView;
import com.kvest.odessatoday.ui.widget.RoundNetworkImageView;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;
import com.kvest.odessatoday.utils.YoutubeApiConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
    private ImageView videoPreview;
    private ImageView videoThumbnailLoadProgress;

    protected String trailerVideoId;
    private String shareTitle, shareText;

    private OnShowFilmCommentsListener onShowFilmCommentsListener;
    private LoadCommentsNotificationReceiver commentsErrorReceiver = new LoadCommentsNotificationReceiver();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onShowFilmCommentsListener = (OnShowFilmCommentsListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity should implements BaseFilmDetailsFragment.OnShowFilmCommentsListener");
        }
    }

    protected void initFilmInfoView(View view) {
        //create layout params for posters
        int postersMargin = (int)getResources().getDimension(R.dimen.film_details_posters_margin);
        postersLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                            (int)getResources().getDimension(R.dimen.film_details_posters_height));
        postersLayoutParams.setMargins(postersMargin, 0, 0, 0);

        //store views
        filmPoster = (NetworkImageView) view.findViewById(R.id.film_poster);
        filmPoster.setDefaultImageResId(R.drawable.loading_poster);
        filmPoster.setErrorImageResId(R.drawable.no_poster);
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

        //start load video thumbnail
        ((YouTubeThumbnailView)view.findViewById(R.id.video_thumbnail)).initialize(YoutubeApiConstants.YOUTUBE_API_KEY, this);

        AnimationDrawable frameAnimation = (AnimationDrawable) videoThumbnailLoadProgress.getBackground();
        frameAnimation.start();

        //TODO
        //FilmDetailsActivity has leaked ServiceConnection - если быстро выйти, пока видео не загрузилось
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(commentsErrorReceiver, new IntentFilter(LoadCommentsNotification.ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(commentsErrorReceiver);
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
            String[] postersUrls = FilmWithTimetable.string2Posters(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.POSTERS)));
            mergeImages(postersUrls);

            //TODO
            //set visibility for Youtube player and posters
//            if (TextUtils.isEmpty(trailerVideoId)) {
                if (postersUrls.length == 0) {
                    imagesContainer.setVisibility(View.GONE);
                } else {
                    imagesContainer.setVisibility(View.VISIBLE);
                }
//            } else {
//                imagesContainer.setVisibility(View.VISIBLE);
//                youTubePlayerFragmentContainer.setVisibility(View.VISIBLE);
//            }

            shareTitle = filmNameValue;
            shareText = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.SHARE_TEXT));
        }
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
        this.youTubeThumbnailLoader = youTubeThumbnailLoader;

        youTubeThumbnailLoader.setOnThumbnailLoadedListener(this);

        if (!TextUtils.isEmpty(trailerVideoId)) {
            youTubeThumbnailLoader.setVideo(trailerVideoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
        LOGE(Constants.TAG, "YouTubePlayer onInitializationFailure");

        //TODO
    }

    @Override
    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String videoId) {
        if (youTubeThumbnailLoader != null) {
            youTubeThumbnailLoader.release();
            youTubeThumbnailLoader = null;
        }

        //transform thumbnail
        Drawable thumbnail = youTubeThumbnailView.getDrawable();

        videoPreview.getLayoutParams().height = postersLayoutParams.height;
        videoPreview.getLayoutParams().width = (int)(((float)thumbnail.getIntrinsicWidth() / (float)thumbnail.getIntrinsicHeight()) * videoPreview.getLayoutParams().height);
        videoPreview.setImageDrawable(thumbnail);
    }

    @Override
    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
        if (youTubeThumbnailLoader != null) {
            youTubeThumbnailLoader.release();
            youTubeThumbnailLoader = null;
        }
        //TODO
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
            onShowFilmCommentsListener.onShowFilmComments(getFilmId());
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
        public void onShowFilmComments(long filmId);
    }

    private class CacheImageAsyncTask extends AsyncTask<Drawable, Void, String> {
        //minimum 3 sec of the cachring process to avoid a blinking of the load dialog
        private static final long MIN_PROCESS_DURATION = 2000L;
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

            String result = null;
            Drawable drawable = params[0];

            if (drawable != null) {
                Rect bounds = drawable.getBounds();
                Bitmap bitmap = Bitmap.createBitmap(bounds.width(),bounds.height(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.draw(canvas);
                OutputStream out = null;
                try {
                    File file = new File(getActivity().getExternalCacheDir(), fileName);
                    if (!file.exists()) {
                        out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    }
                    result = file.getAbsolutePath();
                } catch (IOException ioException) {
                } finally {
                    if ( out != null ){
                        try {
                            out.close();
                        } catch (IOException e) {}
                    }
                }
            }

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

    private class LoadCommentsNotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LoadCommentsNotification.getTargetType(intent) == Constants.CommentTargetType.FILM
                && LoadCommentsNotification.getTargetId(intent) == getFilmId()) {
                Activity activity = getActivity();
                if (!LoadCommentsNotification.isSuccessful(intent) && activity != null) {
                    showErrorSnackbar(activity, R.string.error_loading_comments);
                }
            }
        }
    }
}
