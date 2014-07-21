package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 19.07.14
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class FilmDetailsFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARGUMENT_FILM_ID = "com.kvest.odessatoday.argument.FILM_ID";
    private static final int FILM_LOADER_ID = 1;

    private NetworkImageView filmPoster;
    private TextView filmName;
    private TextView genre;
    private RatingBar filmRating;
    private TextView filmDuration;

    public static FilmDetailsFragment getInstance(long filmId) {
        Bundle arguments = new Bundle(1);
        arguments.putLong(ARGUMENT_FILM_ID, filmId);

        FilmDetailsFragment result = new FilmDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.film_details_fragment, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        //store views
        filmPoster = (NetworkImageView) rootView.findViewById(R.id.film_poster);
        filmPoster.setDefaultImageResId(R.drawable.loading_poster);
        filmPoster.setErrorImageResId(R.drawable.no_poster);
        filmName = (TextView) rootView.findViewById(R.id.film_name);
        genre = (TextView) rootView.findViewById(R.id.genre);
        filmRating = (RatingBar) rootView.findViewById(R.id.film_rating);
        filmDuration = (TextView) rootView.findViewById(R.id.film_duration);
        //TODO
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request full timetable for film
        //TODO

        getLoaderManager().initLoader(FILM_LOADER_ID, null, this);
    }

    private long getFilmId() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_FILM_ID, -1);
        } else {
            return -1;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == FILM_LOADER_ID) {
            String selection = Tables.Films.Columns.FILM_ID + "=?";
            return new CursorLoader(getActivity(), FILMS_URI, null,
                                    selection, new String[]{Long.toString(getFilmId())}, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case FILM_LOADER_ID :
                setFilmData(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //TODO
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
                filmDuration.setText(getString(R.string.film_duration, filmDurationValue));
            } else {
                filmDuration.setVisibility(View.GONE);
            }
        }
    }
}
