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
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.io.network.notification.LoadPlacesNotification;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.PlacesAdapter;
import com.kvest.odessatoday.utils.Constants;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by kvest on 08.10.15.
 */
public class PlacesListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    private static final String ARGUMENT_PLACE_TYPE = "com.kvest.odessatoday.argument.PLACE_TYPE";
    private static final int PLACES_LOADER_ID = 1;

    private ListView placesList;
    private PlacesAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    private PlaceSelectedListener placeSelectedListener;

    private LoadPlacesNotificationReceiver receiver = new LoadPlacesNotificationReceiver();

    public static PlacesListFragment newInstance(int placeType) {
        Bundle arguments = new Bundle(1);
        arguments.putInt(ARGUMENT_PLACE_TYPE, placeType);

        PlacesListFragment result = new PlacesListFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.places_list_fragment, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.application_green);

        placesList = (ListView) rootView.findViewById(R.id.places_list);
        placesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (placeSelectedListener != null) {
                    placeSelectedListener.onPlaceSelected(getPlaceType(), id);
                }
            }
        });

        //create and set an adapter
        adapter = new PlacesAdapter(getActivity());
        placesList.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(PLACES_LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            placeSelectedListener = (PlaceSelectedListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for PlacesListFragment should implements PlacesListFragment.PlaceSelectedListener");
        }

        //Request places
        NetworkService.loadPlaces(activity, getPlaceType());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        placeSelectedListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(LoadPlacesNotification.ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop progress
        refreshLayout.setRefreshing(false);

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == PLACES_LOADER_ID) {
            return DataProviderHelper.getPlacesLoader(getActivity(), getPlaceType(), PlacesAdapter.PROJECTION);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case PLACES_LOADER_ID :
                adapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case PLACES_LOADER_ID :
                adapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onRefresh() {
        //Request places
        Context context = getActivity();
        if (context != null) {
            NetworkService.loadPlaces(context, getPlaceType());;
        }
    }

    private int getPlaceType() {
        Bundle arguments = getArguments();

        return (arguments != null) ? arguments.getInt(ARGUMENT_PLACE_TYPE, -1) : -1;
    }

    private class LoadPlacesNotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO use type of the place loading and check if the type from event is the same(see EventsListFragment)
            refreshLayout.setRefreshing(false);

            Activity activity = getActivity();
            if (!LoadPlacesNotification.isSuccessful(intent) && activity != null) {
                showErrorSnackbar(activity, R.string.error_loading_places);
            }
        }
    }
}
