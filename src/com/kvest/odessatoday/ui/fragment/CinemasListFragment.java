package com.kvest.odessatoday.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.CinemasAdapter;

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

    private void init(View rootView) {
        cinemasList = (ListView) rootView.findViewById(R.id.cinemas_list);

        //create and set an adapter
        adapter = new CinemasAdapter(getActivity());
        cinemasList.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request all cinemas
        NetworkService.loadCinemas(getActivity());
        //TODO
        //обработать ошибки загрузки

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
}
