package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.kvest.odessatoday.R;

import java.util.List;

/**
 * Created by Kvest on 02.02.2015.
 */
public class MainMenuAdapter extends BaseAdapter {
    private static final MainMenuItem[] items = new MainMenuItem[]{
            new MainMenuItem(1, R.string.menu_cinema, R.drawable.ic_menu_cinema, R.drawable.ic_menu_cinema_selected, true)
    };

    private Context context;
    private ListView.LayoutParams itemLayoutParams;

    public MainMenuAdapter(Context context) {
        super();

        this.context = context;
        itemLayoutParams = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, 300);
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
        }

        MainMenuItem item = getItem(position);

        ((TextView)view).setText(item.textRes);
        ((TextView)view).setCompoundDrawables(context.getResources().getDrawable(item.iconRes), null, null, null);

        return view;
    }

    public static class MainMenuItem {
        public final int id;
        private int textRes;
        private int iconRes;
        private int iconSelectedRes;
        public final boolean enable;

        public MainMenuItem(int id, int textRes, int iconSelectedRes, int iconRes, boolean enable) {
            this.enable = enable;
            this.iconSelectedRes = iconSelectedRes;
            this.iconRes = iconRes;
            this.textRes = textRes;
            this.id = id;
        }
    }
}
