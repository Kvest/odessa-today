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
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.ui.activity.PhotoGalleryActivity;
import com.kvest.odessatoday.ui.activity.YoutubeFullscreenActivity;
import com.kvest.odessatoday.ui.adapter.EventTimetableAdapter;
import com.kvest.odessatoday.ui.widget.CommentsCountView;
import com.kvest.odessatoday.ui.widget.RoundNetworkImageView;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;
import com.kvest.odessatoday.utils.YoutubeApiConstants;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by kvest on 18.12.15.
 */
public class EventDetailsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
                                                                  YouTubeThumbnailView.OnInitializedListener,
                                                                  YouTubeThumbnailLoader.OnThumbnailLoadedListener {
    private static final String ARGUMENT_EVENT_ID = "com.kvest.odessatoday.argument.EVENT_ID";
    private static final int EVENT_LOADER_ID = 0;
    private static final int TIMETABLE_LOADER_ID = 1;

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private static final String VIDEO_ID_PARAM = "v";

    private static final String MIN_MAX_PRICES_SEPARATOR = " / ";
    private static final Pattern PRICES_PATTERN = Pattern.compile("(\\d+)");
    private static final int PRICES_GROUP = 1;

    private NetworkImageView eventPoster;
    private TextView eventName;
    private RatingBar eventRating;
    private TextView commentsCount;
    private TextView minMaxPrices;
    private TextView director;
    private View directorContainer;
    private TextView actors;
    private View actorsContainer;
    private TextView description;
    private LinearLayout imagesContainer;
    protected CommentsCountView actionCommentsCount;
    protected LinearLayout.LayoutParams postersLayoutParams;
    private View.OnClickListener onImageClickListener;

    private ListView timetableList;
    private EventTimetableAdapter timetableAdapter;
    private String currencyStr;

    private YouTubeThumbnailLoader youTubeThumbnailLoader;
    private boolean isYoutubeInitInProgress = false;
    private ImageView videoPreview;
    private ImageView videoThumbnailLoadProgress;
    private View videoThumbnailContainer;
    private View videoThumbnailPlay;
    private String videoId;
    private String shareTitle, shareText;

    private int noImageResId, loadingImageResId;

    private OnShowEventCommentsListener onShowEventCommentsListener;
    private int eventType = -1;

    public static EventDetailsFragment newInstance(long eventId) {
        Bundle arguments = new Bundle(1);
        arguments.putLong(ARGUMENT_EVENT_ID, eventId);

        EventDetailsFragment result = new EventDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initResources(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.event_details_fragment, container, false);
        View headerView = inflater.inflate(R.layout.event_details_header, null);

        init(rootView, headerView);

        return rootView;
    }

    private void init(View rootView, View headerView) {
        //create layout params for posters
        int postersMargin = (int)getResources().getDimension(R.dimen.event_details_posters_margin);
        postersLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                            (int)getResources().getDimension(R.dimen.event_details_posters_height));
        postersLayoutParams.setMargins(postersMargin, 0, 0, 0);

        //store views
        eventPoster = (NetworkImageView) headerView.findViewById(R.id.event_poster);
        eventPoster.setDefaultImageResId(loadingImageResId);
        eventPoster.setErrorImageResId(noImageResId);
        eventName = (TextView) headerView.findViewById(R.id.event_name);
        eventRating = (RatingBar) headerView.findViewById(R.id.event_rating);
        commentsCount = (TextView) headerView.findViewById(R.id.comments_count);
        director = (TextView) headerView.findViewById(R.id.director);
        directorContainer = headerView.findViewById(R.id.director_container);
        actors = (TextView) headerView.findViewById(R.id.actors);
        actorsContainer = headerView.findViewById(R.id.actors_container);
        description = (TextView) headerView.findViewById(R.id.description);
        minMaxPrices = (TextView) headerView.findViewById(R.id.min_max_prices);
        imagesContainer = (LinearLayout)headerView.findViewById(R.id.images_container);
        actionCommentsCount = (CommentsCountView) headerView.findViewById(R.id.action_comments_count);
        ((TextView) headerView.findViewById(R.id.action_tickets_title)).setText(R.string.action_tickets_soon);
        videoPreview = (ImageView) headerView.findViewById(R.id.video_preview);
        videoThumbnailLoadProgress = (ImageView) headerView.findViewById(R.id.video_thumbnail_load_progress);
        videoThumbnailContainer = headerView.findViewById(R.id.video_thumbnail_container);
        videoThumbnailPlay = headerView.findViewById(R.id.video_thumbnail_play);

        //setup timetable list
        timetableList = (ListView)rootView.findViewById(R.id.event_details_list);
        timetableList.addHeaderView(headerView);
        timetableAdapter = new EventTimetableAdapter(getActivity());
        timetableList.setAdapter(timetableAdapter);

        actionCommentsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments();
            }
        });

        headerView.findViewById(R.id.action_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CacheImageAsyncTask(Long.toString(getEventId())).execute(eventPoster.getDrawable());
            }
        });

        onImageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] urls = new String[]{((NetworkImageView)view).getUrl()};
                PhotoGalleryActivity.start(getActivity(), urls);
            }
        };
        eventPoster.setOnClickListener(onImageClickListener);

        videoPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoutubeFullscreenActivity.start(getActivity(), videoId);
            }
        });

        AnimationDrawable frameAnimation = (AnimationDrawable) videoThumbnailLoadProgress.getBackground();
        frameAnimation.start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onShowEventCommentsListener = (OnShowEventCommentsListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity should implements EventDetailsFragment.OnShowEventCommentsListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(EVENT_LOADER_ID, null, this);
        getLoaderManager().initLoader(TIMETABLE_LOADER_ID, null, this);
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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case EVENT_LOADER_ID :
                return DataProviderHelper.getEventLoader(getActivity(), getEventId(), null);
            case TIMETABLE_LOADER_ID :
                long startDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                return DataProviderHelper.getEventTimetableLoader(getActivity(), getEventId(), startDate,
                                            EventTimetableAdapter.PROJECTION,
                                            TodayProviderContract.Tables.EventsTimetable.ORDER_DATE_ASC);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case EVENT_LOADER_ID :
                setEventData(cursor);
                break;
            case TIMETABLE_LOADER_ID :
                setMinMaxPrices(cursor);
                timetableAdapter.setCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case TIMETABLE_LOADER_ID :
                timetableAdapter.setCursor(null);
                break;
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

    private void setEventData(Cursor cursor) {
        if (cursor.moveToFirst()) {
            //remember event type
            eventType = cursor.getInt(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.EVENT_TYPE));

            //set data
            String imageUrl = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.IMAGE));
            eventPoster.setImageUrl(imageUrl, TodayApplication.getApplication().getVolleyHelper().getImageLoader());

            String eventNameValue = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.NAME));
            Activity activity = getActivity();
            if (activity != null) {
                activity.setTitle(eventNameValue);
            }
            eventName.setText(eventNameValue);

            eventRating.setRating(cursor.getFloat(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.RATING)));

            int commentsCountValue = cursor.getInt(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.COMMENTS_COUNT));
            commentsCount.setText(Integer.toString(commentsCountValue));
            actionCommentsCount.setCommentsCount(commentsCountValue);

            //setTrailer
            setVideo(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.VIDEO)));

            //add new posters
            String[] postersUrls = Utils.string2Images(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.POSTERS)));
            mergeImages(postersUrls);

            //set visibility for Youtube player and images
            if (TextUtils.isEmpty(videoId)) {
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
                    youTubeThumbnailLoader.setVideo(videoId);
                }
            }

            shareTitle = eventNameValue;
            shareText = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.SHARE_TEXT));

            String value = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.DIRECTOR));
            if (TextUtils.isEmpty(value)) {
                directorContainer.setVisibility(View.GONE);
            } else {
                director.setText(value);
                directorContainer.setVisibility(View.VISIBLE);
            }
            value = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.ACTORS));
            if (TextUtils.isEmpty(value)) {
                actorsContainer.setVisibility(View.GONE);
            } else {
                actors.setText(value);
                actorsContainer.setVisibility(View.VISIBLE);
            }
            value = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Events.Columns.DESCRIPTION));
            if (TextUtils.isEmpty(value)) {
                description.setVisibility(View.GONE);
            } else {
                description.setText(value);
                description.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showComments() {
        if (onShowEventCommentsListener != null && eventType != -1) {
            onShowEventCommentsListener.onShowEventComments(getEventId(), eventType, eventName.getText().toString(),
                                                            actionCommentsCount.getCommentsCount(), eventRating.getRating());
        }
    }

    private void initResources(Context context) {
        currencyStr = getString(R.string.currency);

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

    private void setMinMaxPrices(Cursor cursor) {
        String minMaxPricesValue = calculateMinMaxPrices(cursor);
        if (!TextUtils.isEmpty(minMaxPricesValue)) {
            minMaxPrices.setText(minMaxPricesValue);
            minMaxPrices.setVisibility(View.VISIBLE);
        } else {
            minMaxPrices.setVisibility(View.GONE);
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

        if (!TextUtils.isEmpty(videoId)) {
            youTubeThumbnailLoader.setVideo(videoId);
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

    private void setVideo(String videoLink) {
        //parse video id
        String newVideoId = null;
        if (!TextUtils.isEmpty(videoLink)) {
            newVideoId = Uri.parse(videoLink).getQueryParameter(VIDEO_ID_PARAM);
        }
        if (newVideoId == null) {
            newVideoId = "";
        }

        //store video id
        videoId = newVideoId;
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

    private String calculateMinMaxPrices(Cursor cursor) {
        //get column index
        int pricesColumnIndex = cursor.getColumnIndex(TodayProviderContract.Tables.EventsTimetable.Columns.PRICES);
        if (pricesColumnIndex == -1) {
            return "";
        }

        //calculate min-max values
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Matcher matcher = PRICES_PATTERN.matcher(cursor.getString(pricesColumnIndex));
            while (matcher.find()) {
                int price = Integer.parseInt(matcher.group(PRICES_GROUP));
                min = Math.min(price, min);
                max = Math.max(price, max);
            }

            cursor.moveToNext();
        }

        if (min == Integer.MAX_VALUE && max == Integer.MIN_VALUE) {
            return "";
        }

        if (min == Integer.MAX_VALUE) {
            return Integer.toString(max) + currencyStr;
        }

        if (max == Integer.MIN_VALUE) {
            return Integer.toString(min) + currencyStr;
        }

        if (max == min) {
            return Integer.toString(max) + currencyStr;
        }

        return Integer.toString(min) + currencyStr + MIN_MAX_PRICES_SEPARATOR + Integer.toString(max) + currencyStr;
    }

    protected long getEventId() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_EVENT_ID, -1);
        } else {
            return -1;
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

    public interface OnShowEventCommentsListener {
        void onShowEventComments(long eventId, int eventType, String eventName, int commentsCount, float rating);
    }
}
