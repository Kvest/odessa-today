package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.io.network.notification.LoadTimetableNotification;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.activity.CinemaDetailsActivity;
import com.kvest.odessatoday.ui.adapter.TimetableAdapter;
import com.kvest.odessatoday.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 19.07.14
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class FilmDetailsFragment extends BaseFilmDetailsFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARGUMENT_TIMETABLE_DATE = "com.kvest.odessatoday.argument.TIMETABLE_DATE";

    private static final String MIN_MAX_PRICES_SEPARATOR = " / ";
    private static final Pattern PRICES_PATTERN = Pattern.compile("(\\d+)");
    private static final int PRICES_GROUP = 1;

    private static final String DATE_FORMAT_PATTERN = " dd MMMM yyyy, cccc";
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private static final int FILM_LOADER_ID = 1;
    private static final int TIMETABLE_LOADER_ID = 2;

    private TextView minMaxPricesView;
    private TimetableAdapter timetableAdapter;
    private String currencyStr;

    private TextView dateTextView;
    private long shownTimetableDate;

    private View actionTickets;

    private LoadTimetableNotificationReceiver timetableErrorReceiver = new LoadTimetableNotificationReceiver();

    public static FilmDetailsFragment getInstance(long filmId, long timetableDate) {
        Bundle arguments = new Bundle(2);
        arguments.putLong(ARGUMENT_FILM_ID, filmId);
        arguments.putLong(ARGUMENT_TIMETABLE_DATE, timetableDate);

        FilmDetailsFragment result = new FilmDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currencyStr = getString(R.string.currency);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.film_details_fragment, container, false);
        View headerView = inflater.inflate(R.layout.film_details_header, null);

        shownTimetableDate = getTimetableDate();

        initFilmInfoView(headerView);
        initTimetableList((ListView) rootView, headerView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(timetableErrorReceiver, new IntentFilter(LoadTimetableNotification.ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(timetableErrorReceiver);
    }

    @Override
    protected void initFilmInfoView(View view) {
        super.initFilmInfoView(view);

        minMaxPricesView = (TextView)view.findViewById(R.id.min_max_prices);
        dateTextView = (TextView)view.findViewById(R.id.date);

        actionTickets = view.findViewById(R.id.action_tickets);
        actionTickets.setEnabled(false);
    }

    private void initTimetableList(ListView rootView, View headerView) {
        rootView.addHeaderView(headerView);
        timetableAdapter = new TimetableAdapter(getActivity());
        rootView.setAdapter(timetableAdapter);

        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showCinema(id);
            }
        });

        //set date
        String dateValue = (TimeUtils.isCurrentDay(shownTimetableDate) ? getString(R.string.today_marker) : "") +
                           DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(shownTimetableDate));
        dateTextView.setText(dateValue);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request full timetable for the film and comments
        NetworkService.loadTimetable(getActivity(), getFilmId());

        getLoaderManager().initLoader(FILM_LOADER_ID, null, this);
        getLoaderManager().initLoader(TIMETABLE_LOADER_ID, null, this);
    }

    private long getTimetableDate() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_TIMETABLE_DATE, 0);
        } else {
            return 0;
        }
    }

    private void showCinema(long cinemaId) {
        CinemaDetailsActivity.startClearTop(getActivity(), cinemaId);
    }

    private void setMinMaxPrices(Cursor cursor) {
        String minMaxPrices = calculateMinMaxPrices(cursor);
        if (!TextUtils.isEmpty(minMaxPrices)) {
            minMaxPricesView.setText(minMaxPrices);
            minMaxPricesView.setVisibility(View.VISIBLE);
        } else {
            minMaxPricesView.setVisibility(View.GONE);
        }
    }

    private String calculateMinMaxPrices(Cursor cursor) {
        //get column index
        int pricesColumnIndex = cursor.getColumnIndex(Tables.FilmsFullTimetableView.Columns.PRICES);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FILM_LOADER_ID :
                return DataProviderHelper.getFilmLoader(getActivity(), getFilmId(), null);
            case TIMETABLE_LOADER_ID :
                long endDate = TimeUtils.getEndOfTheDay(shownTimetableDate);
                return DataProviderHelper.getFilmsFullTimetableLoader(getActivity(), getFilmId(),
                                                                      shownTimetableDate, endDate,
                                                                      TimetableAdapter.PROJECTION,
                                                                      Tables.FilmsFullTimetableView.TIMETABLE_ORDER_CINEMA_ASC_DATE_ASC);
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
                setMinMaxPrices(cursor);
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

    private class LoadTimetableNotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Activity activity = getActivity();
            if (!LoadTimetableNotification.isSuccessful(intent) && activity != null) {
                showErrorSnackbar(activity, R.string.error_loading_films_timetable);
            }
        }
    }
}
