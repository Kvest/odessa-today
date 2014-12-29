package com.kvest.odessatoday.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
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

    public static AnnouncementFilmsFragment getInstance() {
        AnnouncementFilmsFragment result = new AnnouncementFilmsFragment();
        return result;
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
        LogUtils.LOGD("KVEST_TAG", "data.getCount()=" + data.getCount());
        //TODO
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //TODO
    }
}
