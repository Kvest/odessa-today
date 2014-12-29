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
import com.kvest.odessatoday.ui.adapter.AnnouncementFilmsAdapter;
import com.kvest.odessatoday.utils.LogUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.14
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementFilmsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ANNOUNCEMENTS_LOADER_ID = 1;

    private AnnouncementFilmsAdapter adapter;

    public static AnnouncementFilmsFragment getInstance() {
        AnnouncementFilmsFragment result = new AnnouncementFilmsFragment();
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.announcement_films_fragment, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        //get list view for announcement films
        ListView filmsList = (ListView) rootView.findViewById(R.id.announcement_films_list);

        //create and set an adapter
        adapter = new AnnouncementFilmsAdapter(getActivity());
        filmsList.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request all announcements
        NetworkService.loadAnnouncements(getActivity());

        getLoaderManager().initLoader(ANNOUNCEMENTS_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ANNOUNCEMENTS_LOADER_ID :
                return DataProviderHelper.getAnnouncementFilmsLoader(getActivity(), AnnouncementFilmsAdapter.PROJECTION);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ANNOUNCEMENTS_LOADER_ID :
                adapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ANNOUNCEMENTS_LOADER_ID :
                adapter.swapCursor(null);
                break;
        }
    }
}
