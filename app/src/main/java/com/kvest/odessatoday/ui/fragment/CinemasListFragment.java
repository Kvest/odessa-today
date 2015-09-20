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
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.io.network.notification.LoadCinemasNotification;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.CinemasAdapter;
import com.kvest.odessatoday.utils.Constants;

import static com.kvest.odessatoday.utils.LogUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 20.08.14
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
public class CinemasListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {
    private static final int CINEMAS_LOADER_ID = 1;

    private ListView cinemasList;
    private CinemasAdapter adapter;

    private CinemaSelectedListener cinemaSelectedListener;

    private LoadCinemasNotificationReceiver receiver = new LoadCinemasNotificationReceiver();

    private SwipeRefreshLayout refreshLayout;

    public static CinemasListFragment getInstance() {
        CinemasListFragment result = new CinemasListFragment();
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cinemas_list_fragment, container, false);

        init(rootView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            cinemaSelectedListener = (CinemaSelectedListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for CinemasListFragment should implements CinemasListFragment.CinemaSelectedListener");
        }

        //Request all cinemas
        NetworkService.loadCinemas(getActivity());

//        //workaround - start showing progress
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                refreshLayout.setRefreshing(true);
//            }
//        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        cinemaSelectedListener = null;
    }

    private void init(View rootView) {
        setHasOptionsMenu(true);

        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.application_green);

        cinemasList = (ListView) rootView.findViewById(R.id.cinemas_list);
        cinemasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (cinemaSelectedListener != null) {
                    cinemaSelectedListener.onCinemaSelected(id);
                }
            }
        });

        //create and set an adapter
        adapter = new CinemasAdapter(getActivity());
        cinemasList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(LoadCinemasNotification.ACTION));
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

        getLoaderManager().initLoader(CINEMAS_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == CINEMAS_LOADER_ID) {
            return DataProviderHelper.getCinemasLoader(getActivity(), CinemasAdapter.PROJECTION, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case CINEMAS_LOADER_ID :
                adapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case CINEMAS_LOADER_ID :
                adapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onRefresh() {
        //Request all cinemas
        NetworkService.loadCinemas(getActivity());
    }

    private class LoadCinemasNotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshLayout.setRefreshing(false);

            Activity activity = getActivity();
            if (!LoadCinemasNotification.isSuccessful(intent) && activity != null) {
                showErrorSnackbar(activity, R.string.error_loading_cinemas);
            }
        }
    }

    public interface CinemaSelectedListener {
        public void onCinemaSelected(long cinemaId);
    }
}
