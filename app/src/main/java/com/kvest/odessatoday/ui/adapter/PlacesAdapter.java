package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.TodayProviderContract;

/**
 * Created by kvest on 08.10.15.
 */
public class PlacesAdapter extends CursorAdapter {
    public static final String[] PROJECTION = new String[]{TodayProviderContract.Tables.Places.Columns.PLACE_ID + " as " + TodayProviderContract.Tables.Places.Columns._ID,
                                                           TodayProviderContract.Tables.Places.Columns.NAME };

    private int nameColumnIndex = -1;

    public PlacesAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.places_list_item, parent, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView)view.findViewById(R.id.place_name);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        //set view background
//        if (cursor.getPosition() % 2 == 0) {
//            view.setBackgroundColor(evenItemBgColor);
//        } else {
//            view.setBackgroundColor(oddItemBgColor);
//        }
        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }

        holder.name.setText(cursor.getString(nameColumnIndex));
    }

    private boolean isColumnIndexesCalculated() {
        return (nameColumnIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        nameColumnIndex = cursor.getColumnIndex(TodayProviderContract.Tables.Places.Columns.NAME);
    }

    private static class ViewHolder {
        private TextView name;
        private TextView address;
        private RatingBar raiting;
        private TextView commentsCount;
        private TextView phones;
    }
}
