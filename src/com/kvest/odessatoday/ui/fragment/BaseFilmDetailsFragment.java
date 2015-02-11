package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.FilmWithTimetable;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.ui.activity.PhotoGalleryActivity;
import com.kvest.odessatoday.ui.widget.ExpandablePanel;
import com.kvest.odessatoday.utils.Constants;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.12.14
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseFilmDetailsFragment extends Fragment implements YouTubePlayer.OnInitializedListener {
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
    protected ImageView filmDurationIcon;
    protected TextView description;
    protected TextView director;
    protected TextView actors;
    protected Button showComments;
    protected LinearLayout postersContainer;
    private View.OnClickListener onPosterClickListener;

    protected YouTubePlayer youTubePlayer;
    protected View youTubePlayerFragmentContainer;
    protected String trailerVideoId;

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

    protected void initFilmInfoView(View view) {
        //create layout params for posters
        int postersMargin = (int)getResources().getDimension(R.dimen.film_details_posters_margin);
        postersLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                            (int)getResources().getDimension(R.dimen.film_details_posters_height));
        postersLayoutParams.setMargins(postersMargin, postersMargin, 0, postersMargin);

        //store views
        filmPoster = (NetworkImageView) view.findViewById(R.id.film_poster);
        filmPoster.setDefaultImageResId(R.drawable.loading_poster);
        filmPoster.setErrorImageResId(R.drawable.no_poster);
        filmName = (TextView) view.findViewById(R.id.film_name);
        genre = (TextView) view.findViewById(R.id.genre);
        filmRating = (RatingBar) view.findViewById(R.id.film_rating);
        commentsCount = (TextView) view.findViewById(R.id.comments_count);
        filmDuration = (TextView) view.findViewById(R.id.film_duration);
        filmDurationIcon = (ImageView) view.findViewById(R.id.film_duration_icon);
        description = (TextView)view.findViewById(R.id.film_description);
        director = (TextView)view.findViewById(R.id.director);
        actors = (TextView)view.findViewById(R.id.actors);
        showComments = (Button)view.findViewById(R.id.show_comments);
        postersContainer = (LinearLayout)view.findViewById(R.id.posters_container);

        youTubePlayerFragmentContainer = view.findViewById(R.id.youtube_fragment_container);

        //init youtube player
        initYoutubePlayer();

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

        showComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments();
            }
        });
        onPosterClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] urls = new String[]{((NetworkImageView)view).getUrl()};
                Intent startIntent = PhotoGalleryActivity.getStartIntent(getActivity(), urls);
                startActivity(startIntent);
            }
        };
    }

    private void initYoutubePlayer() {
        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment)getNestedFragmentManager().findFragmentById(R.id.youtube_fragment_container);

        //add new fragment if it is not exists
        if (youTubePlayerFragment == null) {
            youTubePlayerFragment = YouTubePlayerFragment.newInstance();

            FragmentTransaction transaction = getNestedFragmentManager().beginTransaction();
            try {
                transaction.add(R.id.youtube_fragment_container, youTubePlayerFragment);
            } finally {
                transaction.commit();
            }
        }

        //initialize player
        youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, this);
    }

    @Override
    public void onDestroyView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //workaround - we need to delete youtube fragment manually
            if (!getActivity().isFinishing()) {
                //delete subfragment
                Fragment subfragment = getFragmentManager().findFragmentById(R.id.youtube_fragment_container);
                if (subfragment != null) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    try {
                        transaction.remove(subfragment);
                    } finally {
                        transaction.commitAllowingStateLoss();
                    }
                }
            }
        }

        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST && requestCode == Activity.RESULT_OK) {
            // Retry initialization if user performed a recovery action
            initYoutubePlayer();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        youTubePlayer = player;

        if (!wasRestored && !TextUtils.isEmpty(trailerVideoId)) {
            youTubePlayer.cueVideo(trailerVideoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        LOGE(Constants.TAG, "YouTubePlayer onInitializationFailure");

        Activity activity = getActivity();
        if (youTubeInitializationResult.isUserRecoverableError() && activity != null) {
            youTubeInitializationResult.getErrorDialog(activity, RECOVERY_DIALOG_REQUEST).show();
        } else {
            //if only 1 child - it is youtube fragment
            if (postersContainer.getChildCount() <= 1) {
                postersContainer.setVisibility(View.GONE);
            }
        }
    }

    private FragmentManager getNestedFragmentManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return getChildFragmentManager();
        } else {
            return getFragmentManager();
        }
    }

    protected void setFilmData(Cursor cursor) {
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
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
                filmDuration.setVisibility(View.VISIBLE);
                filmDurationIcon.setVisibility(View.VISIBLE);
                filmDuration.setText(getString(R.string.film_duration, filmDurationValue));
            } else {
                filmDuration.setVisibility(View.GONE);
                filmDurationIcon.setVisibility(View.GONE);
            }

            String value = getString(R.string.film_director, cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.DIRECTOR)));
            director.setText(Html.fromHtml(value));

            value = getString(R.string.film_actors, cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.ACTORS)));
            actors.setText(Html.fromHtml(value));

            description.setText(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.DESCRIPTION)) + "\n");

            int commentsCountValue = cursor.getInt(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.COMMENTS_COUNT));
            commentsCount.setText(Integer.toString(commentsCountValue));
            showComments.setText(Html.fromHtml(getString(R.string.comments_with_count, commentsCountValue)));

            //setTrailer
            setTrailer(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.VIDEO)));

            //add new posters
            String[] postersUrls = FilmWithTimetable.string2Posters(cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Films.Columns.POSTERS)));
            mergePosters(postersUrls);

            //set visibility for Youtube player and posters
            if (TextUtils.isEmpty(trailerVideoId)) {
                if (postersUrls.length == 0) {
                    postersContainer.setVisibility(View.GONE);
                } else {
                    postersContainer.setVisibility(View.VISIBLE);
                    youTubePlayerFragmentContainer.setVisibility(View.GONE);
                }
            } else {
                postersContainer.setVisibility(View.VISIBLE);
                youTubePlayerFragmentContainer.setVisibility(View.VISIBLE);
            }
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

        //start playing video
        if (youTubePlayer != null && !newTrailerVideoId.equals(trailerVideoId)) {
            youTubePlayer.cueVideo(newTrailerVideoId);
        }

        //store video id
        trailerVideoId = newTrailerVideoId;
    }

    private void mergePosters(String[] posterUrls) {
        boolean found;
        for (String posterUrl : posterUrls) {
            found = false;
            for (int i = 1; i < postersContainer.getChildCount(); ++i) {
                if (posterUrl.equals(((NetworkImageView)postersContainer.getChildAt(i)).getUrl())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                //add image view
                NetworkImageView imageView = new NetworkImageView(postersContainer.getContext());
                postersContainer.addView(imageView, postersLayoutParams);

                //start loading
                imageView.setImageUrl(posterUrl, TodayApplication.getApplication().getVolleyHelper().getImageLoader());

                //set click listener
                imageView.setOnClickListener(onPosterClickListener);
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
}
