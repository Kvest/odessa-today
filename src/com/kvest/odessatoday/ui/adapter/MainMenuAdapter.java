package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.kvest.odessatoday.R;

/**
 * Created by Kvest on 02.02.2015.
 */
public class MainMenuAdapter extends BaseAdapter {
    private static final MainMenuItem[] items = new MainMenuItem[]{
            new MainMenuItem(1, R.string.menu_films, R.drawable.ic_menu_films, R.drawable.ic_menu_films_selected, true),
            new MainMenuItem(1, R.string.menu_concert, R.drawable.ic_menu_concert, R.drawable.ic_menu_concert_selected, false),
            new MainMenuItem(1, R.string.menu_party, R.drawable.ic_menu_party, R.drawable.ic_menu_party_selected, false),
            new MainMenuItem(1, R.string.menu_spectacle, R.drawable.ic_menu_spectacle, R.drawable.ic_menu_spectacle_selected, false),
            new MainMenuItem(1, R.string.menu_exhibition, R.drawable.ic_menu_exhibition, R.drawable.ic_menu_exhibition_selected, false),
            new MainMenuItem(1, R.string.menu_sport, R.drawable.ic_menu_sport, R.drawable.ic_menu_sport_selected, false),
            new MainMenuItem(1, R.string.menu_workshop, R.drawable.ic_menu_workshop, R.drawable.ic_menu_workshop_selected, false),
            new MainMenuItem(1, R.string.menu_cinema, R.drawable.ic_menu_cinema, R.drawable.ic_menu_cinema_selected, true),
            new MainMenuItem(1, R.string.menu_theatre, R.drawable.ic_menu_theatre, R.drawable.ic_menu_theatre_selected, false),
            new MainMenuItem(1, R.string.menu_concert_hall, R.drawable.ic_menu_concert_hall, R.drawable.ic_menu_concert_hall_selected, false),
            new MainMenuItem(1, R.string.menu_club, R.drawable.ic_menu_club, R.drawable.ic_menu_club_selected, false),
            new MainMenuItem(1, R.string.menu_museum, R.drawable.ic_menu_museum, R.drawable.ic_menu_museum_selected, false),
            new MainMenuItem(1, R.string.menu_gallery, R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery_selected, false),
            new MainMenuItem(1, R.string.menu_zoo, R.drawable.ic_menu_zoo, R.drawable.ic_menu_zoo_selected, false),
            new MainMenuItem(1, R.string.menu_quest, R.drawable.ic_menu_quest, R.drawable.ic_menu_quest_selected, false),
            new MainMenuItem(1, R.string.menu_restaurant, R.drawable.ic_menu_restaurant, R.drawable.ic_menu_restaurant_selected, false),
            new MainMenuItem(1, R.string.menu_cafe, R.drawable.ic_menu_cafe, R.drawable.ic_menu_cafe_selected, false),
            new MainMenuItem(1, R.string.menu_pizza, R.drawable.ic_menu_pizza, R.drawable.ic_menu_pizza_selected, false),
            new MainMenuItem(1, R.string.menu_sushi, R.drawable.ic_menu_sushi, R.drawable.ic_menu_sushi_selected, false),
            new MainMenuItem(1, R.string.menu_karaoke, R.drawable.ic_menu_karaoke, R.drawable.ic_menu_karaoke_selected, false),
            new MainMenuItem(1, R.string.menu_skating_rink, R.drawable.ic_menu_skating_rink, R.drawable.ic_menu_skating_rink_selected, false),
            new MainMenuItem(1, R.string.menu_bowling, R.drawable.ic_menu_bowling, R.drawable.ic_menu_bowling_selected, false),
            new MainMenuItem(1, R.string.menu_billiard, R.drawable.ic_menu_billiard, R.drawable.ic_menu_billiard_selected, false),
            new MainMenuItem(1, R.string.menu_sauna, R.drawable.ic_menu_sauna, R.drawable.ic_menu_sauna_selected, false),
            new MainMenuItem(1, R.string.menu_bath, R.drawable.ic_menu_bath, R.drawable.ic_menu_bath_selected, false)
    };

    private Context context;
    private ListView.LayoutParams itemLayoutParams;

    public MainMenuAdapter(Context context) {
        super();

        this.context = context;
        itemLayoutParams = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public MainMenuItem getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return items[position].id;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //create item view if needed
        if (view == null) {
            view = new TextView(context);
            view.setLayoutParams(itemLayoutParams);

            view.setPadding(30, 30, 30, 30);
        }

        MainMenuItem item = getItem(position);

        ((TextView)view).setText(item.textRes);
        ((TextView)view).setGravity(Gravity.TOP);
        ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(item.iconSelectedRes, 0, 0, 0);

        return view;
    }

    public static class MainMenuItem {
        public final int id;
        private int textRes;
        private int iconRes;
        private int iconSelectedRes;
        public final boolean enable;

        public MainMenuItem(int id, int textRes, int iconRes, int iconSelectedRes, boolean enable) {
            this.id = id;
            this.textRes = textRes;
            this.iconRes = iconRes;
            this.iconSelectedRes = iconSelectedRes;
            this.enable = enable;
        }
    }
}
