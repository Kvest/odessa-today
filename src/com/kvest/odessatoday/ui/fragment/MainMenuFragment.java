package com.kvest.odessatoday.ui.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.adapter.MainMenuAdapter;

/**
 * Created by Kvest on 02.02.2015.
 */
public class MainMenuFragment extends ListFragment {

    public static MainMenuFragment getInstance() {
        MainMenuFragment result = new MainMenuFragment();
        return result;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setup lit view
        ListView listView = getListView();
        listView.setBackgroundResource(R.color.main_menu_bg);
        listView.setFooterDividersEnabled(false);
        listView.setHeaderDividersEnabled(false);
        listView.setDivider(getResources().getDrawable(R.color.main_menu_divider_color));
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.main_menu_divider_height));

        //set adapter
        setListAdapter(new MainMenuAdapter(getActivity()));
    }
}
