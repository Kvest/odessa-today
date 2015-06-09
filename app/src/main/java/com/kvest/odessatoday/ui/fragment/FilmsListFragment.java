package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.io.network.notification.LoadFilmsNotification;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.FilmsAdapter;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.utils.LogUtils.*;
/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 01.06.14
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class FilmsListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    private static final String ARGUMENT_FOR_TODAY = "com.kvest.odessatoday.argiment.FOR_TODAY";
    private static final String ARGUMENT_DATE = "com.kvest.odessatoday.argiment.DATE";
    private static final int FILMS_LOADER_ID = 1;
    private static final String DATE_FORMAT_PATTERN = "cccc, dd MMMM";
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private long date = 0;
    private boolean isForToday = false;

    private FilmsAdapter adapter;

    private ShowCalendarListener showCalendarListener;
    private FilmSelectedListener filmSelectedListener;

    private LoadFilmsNotificationReceiver receiver = new LoadFilmsNotificationReceiver();
    private TextView dateTextView;

    private SwipeRefreshLayout refreshLayout;

    public static FilmsListFragment getInstance(long date, boolean isForToday) {
        Bundle arguments = new Bundle(2);
        arguments.putLong(ARGUMENT_DATE, date);
        arguments.putBoolean(ARGUMENT_FOR_TODAY, isForToday);

        FilmsListFragment result = new FilmsListFragment();
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

        View root = inflater.inflate(R.layout.films_list_fragment, container, false);

        init(root);

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //get initial data
        Bundle arguments = getArguments();
        if (arguments != null) {
            date = arguments.getLong(ARGUMENT_DATE, 0);
            isForToday = arguments.getBoolean(ARGUMENT_FOR_TODAY, false);
        }

        try {
            showCalendarListener = (ShowCalendarListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for FilmsListFragment should implements FilmsListFragment.ShowCalendarListener");
        }

        try {
            filmSelectedListener = (FilmSelectedListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for FilmsListFragment should implements FilmsListFragment.FilmSelectedListener");
        }

        //load films data
        loadFilmsData(activity);

        //workaround - start showing progress
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                refreshLayout.setRefreshing(true);
//            }
//        });
    }

    private void loadFilmsData(Context context) {
        long startDate = date;
        long endDate = TimeUtils.getEndOfTheDay(startDate);

        //show progress
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(true);
        }

        NetworkService.loadFilms(context, startDate, endDate);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        showCalendarListener = null;
        filmSelectedListener = null;
    }

    public void setShowCalendarListener(ShowCalendarListener showCalendarListener) {
        this.showCalendarListener = showCalendarListener;
    }

    public void setFilmSelectedListener(FilmSelectedListener filmSelectedListener) {
        this.filmSelectedListener = filmSelectedListener;
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(LoadFilmsNotification.ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop progress
        refreshLayout.setRefreshing(false);

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
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
                long startDate = date;
                long endDate = TimeUtils.getEndOfTheDay(startDate);

                return DataProviderHelper.getFilmsForPeriodLoader(getActivity(), startDate, endDate, FilmsAdapter.PROJECTION, null);
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

    private void init(View root) {
        refreshLayout = (SwipeRefreshLayout)root.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.application_green);

        //get list view for films
        ListView filmsList = (ListView) root.findViewById(R.id.films_list);
        filmsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (filmSelectedListener != null) {
                    filmSelectedListener.onFilmSelected(id);
                }
            }
        });

        //set date
        dateTextView = (TextView)root.findViewById(R.id.date);
        dateTextView.setText(createDateString());

        //create and set an adapter
        adapter = new FilmsAdapter(getActivity());
        filmsList.setAdapter(adapter);
    }

    public void changeDate(long date, boolean isForToday) {
        this.date = date;
        this.isForToday = isForToday;

        //set date
        dateTextView.setText(createDateString());

        //reload content
        getLoaderManager().restartLoader(FILMS_LOADER_ID, null, this);

        //load films data
        Activity activity = getActivity();
        if (activity != null) {
            loadFilmsData(activity);
        }
    }

    private String createDateString() {
        StringBuilder sb = new StringBuilder();

        if (isForToday) {
            sb.append(getActivity().getString(R.string.today_marker)).append(" ");
        }
        sb.append(DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(date)).toLowerCase());

        return sb.toString();
    }

    @Override
    public void onRefresh() {
        //reload films data
        Activity activity = getActivity();
        if (activity != null) {
            loadFilmsData(activity);
        }
    }

    private class LoadFilmsNotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshLayout.setRefreshing(false);

            Activity activity = getActivity();
            if (!LoadFilmsNotification.isSuccessful(intent) && activity != null) {
                showErrorSnackbar(activity, R.string.error_loading_films);
            }
        }
    }

    public interface ShowCalendarListener {
        public void onShowCalendar();
    }

    public interface FilmSelectedListener {
        public void onFilmSelected(long filmId);
    }
}
