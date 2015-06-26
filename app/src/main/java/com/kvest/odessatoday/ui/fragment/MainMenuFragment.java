package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.adapter.MainMenuAdapter;
import com.kvest.odessatoday.utils.Constants;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 02.02.2015.
 */
public class MainMenuFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private static final String ARGUMENT_DEFAULT_SELECTED_ITEM_ID = "com.kvest.odessatoday.argument.DEFAULT_SELECTED_ITEM_ID";

    public static final int MENU_FILMS_ID = 0;
    public static final int MENU_CONCERT_ID = 1;
    public static final int MENU_PARTY_ID = 2;
    public static final int MENU_SPECTACLE_ID = 3;
    public static final int MENU_EXHIBITION_ID = 4;
    public static final int MENU_SPORT_ID = 5;
    public static final int MENU_WORKSHOP_ID = 6;
    public static final int MENU_CINEMA_ID = 7;
    public static final int MENU_THEATRE_ID = 8;
    public static final int MENU_CONCERT_HALL_ID = 9;
    public static final int MENU_CLUB_ID = 10;
    public static final int MENU_MUSEUM_ID = 11;
    public static final int MENU_GALLERY_ID = 12;
    public static final int MENU_ZOO_ID = 13;
    public static final int MENU_QUEST_ID = 14;
    public static final int MENU_RESTAURANT_ID = 15;
    public static final int MENU_CAFE_ID = 16;
    public static final int MENU_PIZZA_ID = 17;
    public static final int MENU_SUSHI_ID = 18;
    public static final int MENU_KARAOKE_ID = 19;
    public static final int MENU_SKATING_RINK_ID = 20;
    public static final int MENU_BOWLING_ID = 21;
    public static final int MENU_BILLIARD_ID = 22;
    public static final int MENU_SAUNA_ID = 23;
    public static final int MENU_BATH_ID = 24;

    private MainMenuAdapter adapter;
    private MainMenuItemSelectedListener mainMenuItemSelectedListener;

    public static MainMenuFragment getInstance(int defaultSelectedItemId) {
        Bundle arguments = new Bundle(1);
        arguments.putInt(ARGUMENT_DEFAULT_SELECTED_ITEM_ID, defaultSelectedItemId);

        MainMenuFragment result = new MainMenuFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set adapter
        adapter = new MainMenuAdapter(getActivity());
        adapter.setSelectedItemById(getDefaultSelectedItemId());
        setListAdapter(adapter);

        //setup lit view
        ListView listView = getListView();
        listView.setBackgroundResource(R.color.main_menu_bg);
        listView.setFooterDividersEnabled(false);
        listView.setHeaderDividersEnabled(false);
        listView.setDivider(getResources().getDrawable(R.color.main_menu_divider_color));
        listView.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.main_menu_divider_height));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mainMenuItemSelectedListener = (MainMenuItemSelectedListener) activity;
        } catch (ClassCastException cce) {
            LOGE(Constants.TAG, "Host activity for MainMenuFragment should implements MainMenuFragment.MainMenuItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mainMenuItemSelectedListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainMenuAdapter.MainMenuItem item = adapter.getItem(position);
        if (item.enable) {
            int oldSelectedPosition = adapter.getSelectedItemPosition();

            if (position != oldSelectedPosition) {
                //set selected item in menu
                adapter.setSelectedItemPosition(position);

                //notify selected menu item was changed
                if (mainMenuItemSelectedListener != null) {
                    mainMenuItemSelectedListener.onMainMenuItemSelected((int)id);
                }
            }
        }
    }

    private int getDefaultSelectedItemId() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getInt(ARGUMENT_DEFAULT_SELECTED_ITEM_ID, MainMenuAdapter.NO_SELECTION);
        } else {
            return MainMenuAdapter.NO_SELECTION;
        }
    }

    public interface MainMenuItemSelectedListener {
        public void onMainMenuItemSelected(int menuItemId);
    }
}
