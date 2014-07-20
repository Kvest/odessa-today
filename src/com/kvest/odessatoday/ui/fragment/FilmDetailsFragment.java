package com.kvest.odessatoday.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kvest.odessatoday.R;
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case FILM_LOADER_ID :
                //TODO
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //TODO
    }
}
