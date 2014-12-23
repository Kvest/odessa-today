package com.kvest.odessatoday.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import com.kvest.odessatoday.service.NetworkService;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.14
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementsFragment extends Fragment {

    public static AnnouncementsFragment getInstance() {
        AnnouncementsFragment result = new AnnouncementsFragment();
        return result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Request all announcements
        NetworkService.loadAnnouncements(getActivity());

        //TODO
        //getLoaderManager().initLoader(CINEMAS_LOADER_ID, null, this);
    }
}
