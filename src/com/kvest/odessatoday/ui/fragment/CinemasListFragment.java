package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
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
public class CinemasListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CINEMAS_LOADER_ID = 1;

    private ListView cinemasList;
    private CinemasAdapter adapter;

    private CinemaSelectedListener cinemaSelectedListener;

    private LoadCinemasNotificationReceiver receiver = new LoadCinemasNotificationReceiver();

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //workaround - we need to clear menu manually
        menu.clear();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            cinemaSelectedListener = (CinemaSelectedListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for CinemasListFragment should implements CinemasListFragment.CinemaSelectedListener");
        }
    }

    private void init(View rootView) {
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

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request all cinemas
        NetworkService.loadCinemas(getActivity());

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

    private class LoadCinemasNotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Activity activity = getActivity();
            if (!LoadCinemasNotification.isSuccessful(intent) && activity != null) {
                Toast.makeText(activity, R.string.error_loading_cinemas, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface CinemaSelectedListener {
        public void onCinemaSelected(long cinemaId);
    }
}
