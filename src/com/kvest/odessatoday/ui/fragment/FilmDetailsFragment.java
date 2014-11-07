package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.Film;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.TimetableAdapter;
import com.kvest.odessatoday.ui.widget.ExpandablePanel;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
import static com.kvest.odessatoday.utils.LogUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 19.07.14
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class FilmDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
                                                             YouTubePlayer.OnInitializedListener {
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private static final String VIDEO_ID_PARAM = "v";
    private static final String TIMETABLE_DATE_FORMAT_PATTERN = "dd MMMM yyyy";
    private static final SimpleDateFormat TIMETABLE_DATE_FORMAT = new SimpleDateFormat(TIMETABLE_DATE_FORMAT_PATTERN);

    private static final String ARGUMENT_FILM_ID = "com.kvest.odessatoday.argument.FILM_ID";
    private static final String ARGUMENT_TIMETABLE_DATE = "com.kvest.odessatoday.argument.TIMETABLE_DATE";

    private static final int FILM_LOADER_ID = 1;
    private static final int TIMETABLE_LOADER_ID = 2;

    private NetworkImageView filmPoster;
    private TextView filmName;
    private TextView genre;
    private RatingBar filmRating;
    private TextView filmDuration;
    private ImageView filmDurationIcon;
    private TextView description;
    private TextView director;
    private TextView actors;
    private TextView commentsCount;
    private TextView timetableDate;
    private ListView timetableList;
    private TimetableAdapter timetableAdapter;
    private LinearLayout postersContainer;

    private LinearLayout.LayoutParams postersLayoutParams;

    private OnShowFilmCommentsListener onShowFilmCommentsListener;

    private long shownTimetableDate;

    private YouTubePlayer youTubePlayer;
    private View youTubePlayerFragmentContainer;
    private String trailerVideoId;

    public static FilmDetailsFragment getInstance(long filmId, long timetableDate) {
        Bundle arguments = new Bundle(2);
        arguments.putLong(ARGUMENT_FILM_ID, filmId);
        arguments.putLong(ARGUMENT_TIMETABLE_DATE, timetableDate);

        FilmDetailsFragment result = new FilmDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.film_details_fragment, container, false);
        View headerView = inflater.inflate(R.layout.film_details_header, null);

        shownTimetableDate = getTimetableDate();

        init((ListView)rootView, headerView);

        return rootView;
    }

    @Override
     public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onShowFilmCommentsListener = (OnShowFilmCommentsListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for FilmDetailsFragment should implements FilmDetailsFragment.OnShowFilmCommentsListener");
        }
    }

    private void init(ListView rootView, View headerView) {
        //create layout params for posters
        int postersMargin = (int)getResources().getDimension(R.dimen.film_details_posters_margin);
        postersLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                           (int)getResources().getDimension(R.dimen.film_details_posters_height));
        postersLayoutParams.setMargins(0, postersMargin, postersMargin, postersMargin);

        //store views
        filmPoster = (NetworkImageView) headerView.findViewById(R.id.film_poster);
        filmPoster.setDefaultImageResId(R.drawable.loading_poster);
        filmPoster.setErrorImageResId(R.drawable.no_poster);
        filmName = (TextView) headerView.findViewById(R.id.film_name);
        genre = (TextView) headerView.findViewById(R.id.genre);
        filmRating = (RatingBar) headerView.findViewById(R.id.film_rating);
        filmDuration = (TextView) headerView.findViewById(R.id.film_duration);
        filmDurationIcon = (ImageView) headerView.findViewById(R.id.film_duration_icon);
        description = (TextView)headerView.findViewById(R.id.film_description);
        director = (TextView)headerView.findViewById(R.id.director);
        actors = (TextView)headerView.findViewById(R.id.actors);
        commentsCount = (TextView)headerView.findViewById(R.id.comments_count_value);
        postersContainer = (LinearLayout)headerView.findViewById(R.id.posters_container);
        timetableDate = (TextView)headerView.findViewById(R.id.timetable_date);
        youTubePlayerFragmentContainer = headerView.findViewById(R.id.youtube_fragment_container);

        //init youtube player
        initYoutubePlayer();

        ((ExpandablePanel)headerView.findViewById(R.id.expand_panel)).setOnExpandListener(new ExpandablePanel.OnExpandListener() {
            @Override
            public void onExpand(View handle, View content) {
                ((ImageButton)handle).setImageResource(R.drawable.collapse_arrow);
            }

            @Override
            public void onCollapse(View handle, View content) {
                ((ImageButton)handle).setImageResource(R.drawable.expand_arrow);
            }
        });

        headerView.findViewById(R.id.comments_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComments();
            }
        });

        timetableList = rootView;
        timetableList.addHeaderView(headerView);
        timetableAdapter = new TimetableAdapter(getActivity());
        timetableList.setAdapter(timetableAdapter);

        timetableDate.setText(TIMETABLE_DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(shownTimetableDate)));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request full timetable for the film and comments
        NetworkService.loadTimetable(getActivity(), getFilmId());
        NetworkService.loadFilmComments(getActivity(), getFilmId());

        getLoaderManager().initLoader(FILM_LOADER_ID, null, this);
        getLoaderManager().initLoader(TIMETABLE_LOADER_ID, null, this);
    }

    private void initYoutubePlayer() {
        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment)getFragmentManager().findFragmentById(R.id.youtube_fragment);
        if (youTubePlayerFragment != null) {
            youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST && requestCode == Activity.RESULT_OK) {
            // Retry initialization if user performed a recovery action
            initYoutubePlayer();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        this.youTubePlayer = youTubePlayer;

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

    private long getFilmId() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_FILM_ID, -1);
        } else {
            return -1;
        }
    }

    private long getTimetableDate() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_TIMETABLE_DATE, 0);
        } else {
            return 0;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == FILM_LOADER_ID) {
            return DataProviderHelper.getFilmLoader(getActivity(), getFilmId(), null);
        } else if (id == TIMETABLE_LOADER_ID) {
            long endDate = TimeUtils.getEndOfTheDay(shownTimetableDate);
            return DataProviderHelper.getFilmsFullTimetableLoader(getActivity(), getFilmId(), shownTimetableDate, endDate,
                                                                  TimetableAdapter.PROJECTION, Tables.FilmsFullTimetable.TIMETABLE_ORDER_ASC);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case FILM_LOADER_ID :
                setFilmData(cursor);
                break;
            case TIMETABLE_LOADER_ID :
                timetableAdapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case TIMETABLE_LOADER_ID :
                timetableAdapter.swapCursor(null);
                break;
        }
    }

    private void showComments() {
        if (onShowFilmCommentsListener != null) {
            onShowFilmCommentsListener.onShowFilmComments(getFilmId());
        }
    }

    private void setFilmData(Cursor cursor) {
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            //set data
            filmPoster.setImageUrl(cursor.getString(cursor.getColumnIndex(Tables.Films.Columns.IMAGE)),
                    TodayApplication.getApplication().getVolleyHelper().getImageLoader());

            String filmNameValue = cursor.getString(cursor.getColumnIndex(Tables.Films.Columns.NAME));
            Activity activity = getActivity();
            if (activity != null) {
                activity.setTitle(filmNameValue);
            }
            filmName.setText(filmNameValue);

            genre.setText(cursor.getString(cursor.getColumnIndex(Tables.Films.Columns.GENRE)));
            genre.setVisibility(TextUtils.isEmpty(genre.getText()) ? View.GONE : View.VISIBLE);

            filmRating.setRating(cursor.getFloat(cursor.getColumnIndex(Tables.Films.Columns.RATING)));

            int filmDurationValue = cursor.getInt(cursor.getColumnIndex(Tables.Films.Columns.FILM_DURATION));
            if (filmDurationValue > 0) {
                filmDuration.setVisibility(View.VISIBLE);
                filmDurationIcon.setVisibility(View.VISIBLE);
                filmDuration.setText(getString(R.string.film_duration, filmDurationValue));
            } else {
                filmDuration.setVisibility(View.GONE);
                filmDurationIcon.setVisibility(View.GONE);
            }

            String value = getString(R.string.film_director, cursor.getString(cursor.getColumnIndex(Tables.Films.Columns.DIRECTOR)));
            director.setText(Html.fromHtml(value));

            value = getString(R.string.film_actors, cursor.getString(cursor.getColumnIndex(Tables.Films.Columns.ACTORS)));
            actors.setText(Html.fromHtml(value));

            description.setText(cursor.getString(cursor.getColumnIndex(Tables.Films.Columns.DESCRIPTION)) + "\n");
            commentsCount.setText(Integer.toString(cursor.getInt(cursor.getColumnIndex(Tables.Films.Columns.COMMENTS_COUNT))));

            //setTrailer
            setTrailer(cursor.getString(cursor.getColumnIndex(Tables.Films.Columns.VIDEO)));

            //add new posters
            String[] postersUrls = Film.string2Posters(cursor.getString(cursor.getColumnIndex(Tables.Films.Columns.POSTERS)));
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
            }
        }
    }

    public interface OnShowFilmCommentsListener {
        public void onShowFilmComments(long filmId);
    }
}
