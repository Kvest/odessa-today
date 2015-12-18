package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.io.network.event.EventsLoaded;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.activity.DateSelectionListener;
import com.kvest.odessatoday.ui.activity.MainActivity;
import com.kvest.odessatoday.ui.adapter.EventsAdapter;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.TimeUtils;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.utils.LogUtils.LOGD;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by roman on 12/8/15.
 */
public class EventsListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
                                                                SwipeRefreshLayout.OnRefreshListener,
                                                                MainActivity.ToolbarExtendable,
                                                                DateSelectionListener {
    private final SimpleDateFormat EVENTS_LIST_DATE_FORMAT = new SimpleDateFormat("dd MMMM, cc.");


    private static final String KEY_DATE = "com.kvest.odessatoday.key.DATE";
    private static final String ARGUMENT_EVENT_TYPE = "com.kvest.odessatoday.argument.EVENT_TYPE";
    private static final String ARGUMENT_DATE = "com.kvest.odessatoday.argiment.DATE";
    private static final int EVENTS_LOADER_ID = 1;

    //date of the shown events in seconds
    private long date = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    private DateChangedListener dateChangedListener;

    private TextView extensionTitle;
    private View previousDay;
    private View nextDay;

    private ListView eventsList;
    private EventsAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    private EventSelectedListener eventSelectedListener;
    private ShowCalendarListener showCalendarListener;

    public static EventsListFragment newInstance(int eventType, long date) {
        Bundle arguments = new Bundle(2);
        arguments.putInt(ARGUMENT_EVENT_TYPE, eventType);
        arguments.putLong(ARGUMENT_DATE, date);

        EventsListFragment result = new EventsListFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.events_list_fragment, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.application_green);

        eventsList = (ListView) rootView.findViewById(R.id.events_list);
        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (eventSelectedListener != null) {
                    eventSelectedListener.onEventSelected(id);
                }
            }
        });

        //create and set an adapter
        adapter = new EventsAdapter(getActivity());
        eventsList.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(EVENTS_LOADER_ID, null, this);

        //send data once again after recreate to the date listener
        if (savedInstanceState != null) {
            onDateChanged(this.date);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_DATE)) {
            setDate(savedInstanceState.getLong(KEY_DATE));
        } else {
            //get initial data
            Bundle arguments = getArguments();
            if (arguments != null) {
                setDate(arguments.getLong(ARGUMENT_DATE, date));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.calendar_menu, menu);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            showCalendarListener = (ShowCalendarListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for EventsListFragment should implements ShowCalendarListener");
        }
        try {
            eventSelectedListener = (EventSelectedListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for EventsListFragment should implements EventsListFragment.EventSelectedListener");
        }

        //Request events
        loadEvents(activity);
    }

    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop progress
        refreshLayout.setRefreshing(false);

        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        dateChangedListener = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        eventSelectedListener = null;
        showCalendarListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(KEY_DATE, date);
    }

    @Subscribe
    public void onEventsLoaded(EventsLoaded event) {
        if (event.getType() == getEventType()) {
            //event dispatched not in the UI thread
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            });


            Activity activity = getActivity();
            if (!event.isSuccessful() && activity != null) {
                showErrorSnackbar(activity, R.string.error_loading_events);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == EVENTS_LOADER_ID) {
            long startDate = date;
            long endDate = TimeUtils.getEndOfTheDay(startDate);

            return DataProviderHelper.getFullEventsForPeriodLoader(getActivity(), getEventType(), startDate, endDate, EventsAdapter.PROJECTION, TodayProviderContract.Tables.EventsTimetableView.ORDER_ASC);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case EVENTS_LOADER_ID :
                adapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case EVENTS_LOADER_ID :
                adapter.swapCursor(null);
                break;
        }
    }

    private void onShowCalendar() {
        if (showCalendarListener != null) {
            showCalendarListener.onShowCalendar(date);
        }
    }

    @Override
    public void onDateSelected(long date) {
        setDate(Math.max(date, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));

        //reload content
        getLoaderManager().restartLoader(EVENTS_LOADER_ID, null, this);
        getLoaderManager().restartLoader(EVENTS_LOADER_ID, null, this);
    }

    public void showNextDay() {
        long nextDay = TimeUtils.getBeginningOfTheDay(date) + TimeUnit.DAYS.toSeconds(1);
        onDateSelected(nextDay);
    }

    public void showPreviousDay() {
        long previousDay = TimeUtils.getBeginningOfTheDay(date) - TimeUnit.DAYS.toSeconds(1);
        onDateSelected(previousDay);
    }

    private void setDate(long date) {
        this.date = date;

        onDateChanged(date);
    }

    public void onDateChanged(long date) {
        if (extensionTitle != null && previousDay != null) {
            int previousDayVisibility = View.VISIBLE;
            if (TimeUtils.isCurrentDay(date)) {
                extensionTitle.setText(R.string.odessa_today);
                previousDayVisibility = View.INVISIBLE;
            } else if (TimeUtils.isTomorrow(date)) {
                extensionTitle.setText(R.string.odessa_tomorrow);
            } else {
                extensionTitle.setText(EVENTS_LIST_DATE_FORMAT.format(TimeUnit.SECONDS.toMillis(date)));
            }

            previousDay.setVisibility(previousDayVisibility);
        }

        //notify listener
        if (dateChangedListener != null) {
            dateChangedListener.onDateChanged(this.date);
        }
    }

    @Override
    public void onRefresh() {
        Context context = getActivity();
        if (context != null) {
            loadEvents(context);
        }
    }

    @Override
    public int getExtensionLayoutId() {
        return R.layout.toolbar_extension_with_calendar;
    }

    @Override
    public void setExtensionView(View extension) {
        extensionTitle = (TextView) extension.findViewById(R.id.title);
        previousDay = extension.findViewById(R.id.previous_day);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousDay();
            }
        });
        nextDay = extension.findViewById(R.id.next_day);
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextDay();
            }
        });
    }

    public DateChangedListener getDateChangedListener() {
        return dateChangedListener;
    }

    public void setDateChangedListener(DateChangedListener dateChangedListener) {
        this.dateChangedListener = dateChangedListener;
    }

    private void loadEvents(Context context) {
        long startDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        //show progress
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(true);
        }

        NetworkService.loadEvents(context, startDate, -1, getEventType());
    }

    private int getEventType() {
        Bundle arguments = getArguments();

        return (arguments != null) ? arguments.getInt(ARGUMENT_EVENT_TYPE, -1) : -1;
    }

    public interface EventSelectedListener {
        void onEventSelected(long eventId);
    }
}
