package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.io.network.event.EventsLoaded;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.EventsAdapter;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.TimeUtils;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by roman on 12/8/15.
 */
public class EventsListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    private static final String ARGUMENT_EVENT_TYPE = "com.kvest.odessatoday.argument.EVENT_TYPE";
    private static final int EVENTS_LOADER_ID = 1;

    //date of the shown events in seconds
    private long date = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

    private ListView eventsList;
    private EventsAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    private EventSelectedListener eventSelectedListener;

    public static EventsListFragment newInstance(int eventType) {
        Bundle arguments = new Bundle(1);
        arguments.putInt(ARGUMENT_EVENT_TYPE, eventType);

        EventsListFragment result = new EventsListFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        //TODO
        //don't forget cto call getLoaderManager().restartLoader(EVENTS_LOADER_ID, null, this); when the date was changed
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(EVENTS_LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

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

    @Override
    public void onRefresh() {
        Context context = getActivity();
        if (context != null) {
            loadEvents(context);
        }
    }

    private void loadEvents(Context context) {
        long startDate = date;
        long endDate = TimeUtils.getEndOfTheDay(startDate);

        //show progress
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(true);
        }

        NetworkService.loadEvents(context, startDate, endDate, getEventType());
    }

    private int getEventType() {
        Bundle arguments = getArguments();

        return (arguments != null) ? arguments.getInt(ARGUMENT_EVENT_TYPE, -1) : -1;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        eventSelectedListener = null;
    }

    public interface EventSelectedListener {
        void onEventSelected(long eventId);
    }
}
