package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.FilmsAdapter;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;
import static com.kvest.odessatoday.provider.TodayProviderContract.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 01.06.14
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class FilmsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARGUMENT_FOR_TODAY = "com.kvest.odessatoday.argiment.FOR_TODAY";
    private static final String ARGUMENT_DATE = "com.kvest.odessatoday.argiment.DATE";
    private static final int FILMS_LOADER_ID = 1;
    private static final String DATE_FORMAT_PATTERN = " dd MMMM yyyy, ";
    private static final String WEEK_DAY_FORMAT_PATTERN = "cccc";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    private static final SimpleDateFormat WEEK_DAY_FORMAT = new SimpleDateFormat(WEEK_DAY_FORMAT_PATTERN);

    private ListView filmsList;
    private FilmsAdapter adapter;

    private ShowCalendarListener showCalendarListener;

    public static FilmsFragment getInstance(long date, boolean isForToday) {
        Bundle arguments = new Bundle(2);
        arguments.putLong(ARGUMENT_DATE, date);
        arguments.putBoolean(ARGUMENT_FOR_TODAY, isForToday);

        FilmsFragment result = new FilmsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.films_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_calendar:
                onShowCalendar();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View root = inflater.inflate(R.layout.films_fragment, container, false);

        initComponents(root);

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            showCalendarListener = (ShowCalendarListener) activity;
        } catch (ClassCastException cce) {
            Log.e(Constants.TAG, "Host activity for FilmsFragment should implements FilmsFragment.ShowCalendarListener");
        }

        //reload films data
        long startDate = getDate();
        long endDate = Utils.getEndOfTheDay(startDate);
        NetworkService.loadFilms(activity, startDate, endDate);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(FILMS_LOADER_ID, null, this);
    }

    private void onShowCalendar() {
        if (showCalendarListener != null) {
            showCalendarListener.onShowCalendar();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FILMS_LOADER_ID :
                long startDate = getDate();
                long endDate = Utils.getEndOfTheDay(startDate);
                String selection = Tables.Films.Columns.FILM_ID + " in (" + Tables.FilmsTimetable.GET_FILMS_ID_BY_PERIOD_SQL + ")";
                return new CursorLoader(getActivity(), TodayProviderContract.FILMS_URI, FilmsAdapter.PROJECTION, selection,
                                        new String[]{Long.toString(startDate), Long.toString(endDate)}, null);
            default :
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case FILMS_LOADER_ID :
                adapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case FILMS_LOADER_ID :
                adapter.swapCursor(null);
                break;
        }
    }


    private void initComponents(View root) {
        //get list view for films
        filmsList = (ListView) root.findViewById(R.id.films_list);

        //set date
        root.findViewById(R.id.is_today).setVisibility(isForToday() ? View.VISIBLE : View.GONE);
        Date date = new Date(TimeUnit.SECONDS.toMillis(getDate()));
        TextView dateTextView = (TextView)root.findViewById(R.id.date);
        dateTextView.setText(DATE_FORMAT.format(date));
        TextView weekTextView = (TextView)root.findViewById(R.id.week_day);
        weekTextView.setText(WEEK_DAY_FORMAT.format(date).toLowerCase());

        //create and set an adapter
        adapter = new FilmsAdapter(getActivity());
        filmsList.setAdapter(adapter);
    }

    private boolean isForToday() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getBoolean(ARGUMENT_FOR_TODAY, false);
        } else {
            return false;
        }
    }

    private long getDate() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_DATE, 0);
        } else {
            return 0;
        }
    }

    public interface ShowCalendarListener {
        public void onShowCalendar();
    }
}
