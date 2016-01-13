package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.io.network.event.CinemasLoadedEvent;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.CinemasAdapter;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;
import com.squareup.otto.Subscribe;

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

    private SwipeRefreshLayout refreshLayout;

    public static CinemasListFragment newInstance() {
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
        NetworkService.loadCinemas(activity);

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

    @Subscribe
    public void onCinemasLoaded(CinemasLoadedEvent event) {
        //event dispatched not in the UI thread
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });

        Activity activity = getActivity();
        if (!event.isSuccessful() && activity != null) {
            showErrorSnackbar(activity, R.string.error_loading_cinemas);
        }
    }
}
