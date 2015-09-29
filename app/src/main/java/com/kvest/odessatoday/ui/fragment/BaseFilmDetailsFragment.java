package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.kvest.odessatoday.ui.widget.ExpandablePanel;
import com.kvest.odessatoday.ui.widget.RoundNetworkImageView;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.YoutubeApiConstants;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.12.14
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseFilmDetailsFragment extends BaseFragment {
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
    protected LinearLayout imagesContainer;
    private View.OnClickListener onImageClickListener;

    protected String trailerVideoId;

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

        ((ExpandablePanel)view.findViewById(R.id.expand_panel)).setOnExpandListener(new ExpandablePanel.OnExpandListener() {
            @Override
            public void onExpand(View handle, View content) {
                ((ImageButton)handle).setImageResource(R.drawable.ic_collapse_arrow);
            }

            @Override
            public void onCollapse(View handle, View content) {
                ((ImageButton)handle).setImageResource(R.drawable.ic_expand_arrow);
            }
        });

        onImageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] urls = new String[]{((NetworkImageView)view).getUrl()};
                PhotoGalleryActivity.start(getActivity(), urls);
            }
        };
        filmPoster.setOnClickListener(onImageClickListener);

//        ((YouTubeThumbnailView)view.findViewById(R.id.video_thumbnail)).initialize(YoutubeApiConstants.YOUTUBE_API_KEY, new YouTubeThumbnailView.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
//                //TODO
//                //youTubeThumbnailLoader.release();
//
//                youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
//                    @Override
//                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
//                        Drawable d = youTubeThumbnailView.getDrawable();
//
//
//                        ImageView iv = (ImageView) getView().findViewById(R.id.video_preview);
//                        // iv.getLayoutParams().width = (int)(((float)d.getIntrinsicWidth() / (float)d.getIntrinsicHeight()) * iv.getLayoutParams().height);
//                        iv.setImageDrawable(d);
//                    }
//
//                    @Override
//                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
//                    }
//                });
//
//                youTubeThumbnailLoader.setVideo("o7VVHhK9zf0");
//            }
//
//
//            @Override
//            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });
//
//        view.findViewById(R.id.video_preview).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                YoutubeFullscreenActivity.start(getActivity(), trailerVideoId);
//            }
//        });
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

            String value = getString(R.string.film_director, cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.DIRECTOR)));
            director.setText(Html.fromHtml(value));

            value = getString(R.string.film_actors, cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.ACTORS)));
            actors.setText(Html.fromHtml(value));

            description.setText(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.DESCRIPTION)) + "\n");

            int commentsCountValue = cursor.getInt(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.COMMENTS_COUNT));
            commentsCount.setText(Integer.toString(commentsCountValue));

            //setTrailer
            setTrailer(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.VIDEO)));

            //add new posters
            String[] postersUrls = FilmWithTimetable.string2Posters(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.POSTERS)));
            mergeImages(postersUrls);

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
        }
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
