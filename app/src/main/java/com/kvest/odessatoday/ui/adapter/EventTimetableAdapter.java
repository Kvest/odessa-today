package com.kvest.odessatoday.ui.adapter;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created by kvest on 19.12.15.
 */
public class EventTimetableAdapter extends BaseAdapter {
    public static final String[] PROJECTION = new String[]{ EventsTimetable.Columns._ID, EventsTimetable.Columns.PLACE_NAME,
                                                            EventsTimetable.Columns.DATE, EventsTimetable.Columns.PRICES,
                                                            EventsTimetable.Columns.HAS_TICKETS };

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void setCursor(Cursor cursor) {
        
    }
}
