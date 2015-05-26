package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.AnnouncementFilmsAdapter;
import com.kvest.odessatoday.utils.Constants;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;
import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.14
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementFilmsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ANNOUNCEMENTS_LOADER_ID = 1;

    private AnnouncementFilmsAdapter adapter;

    private AnnouncementFilmSelectedListener announcementFilmSelectedListener;

    public static AnnouncementFilmsListFragment getInstance() {
        AnnouncementFilmsListFragment result = new AnnouncementFilmsListFragment();
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
        filmsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (announcementFilmSelectedListener != null) {
                    announcementFilmSelectedListener.onAnnouncementFilmSelected(id);
                }
            }
        });

        //create and set an adapter
        adapter = new AnnouncementFilmsAdapter(getActivity());
        filmsList.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(ANNOUNCEMENTS_LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            announcementFilmSelectedListener = (AnnouncementFilmSelectedListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for AnnouncementFilmsListFragment should implements AnnouncementFilmsListFragment.AnnouncementFilmSelectedListener");
        }

        //Request all announcements
        NetworkService.loadAnnouncements(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        announcementFilmSelectedListener = null;
    }

    public void setAnnouncementFilmSelectedListener(AnnouncementFilmSelectedListener announcementFilmSelectedListener) {
        this.announcementFilmSelectedListener = announcementFilmSelectedListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ANNOUNCEMENTS_LOADER_ID :
                return DataProviderHelper.getAnnouncementFilmsLoader(getActivity(), AnnouncementFilmsAdapter.PROJECTION,
                                                                    Tables.AnnouncementFilmsView.PREMIERE_DATE_ORDER_ASC);
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

    public interface AnnouncementFilmSelectedListener {
        public void onAnnouncementFilmSelected(long filmId);
    }
}
