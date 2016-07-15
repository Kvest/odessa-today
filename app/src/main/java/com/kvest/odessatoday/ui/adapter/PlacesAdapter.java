package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.TodayProviderContract.*;
import com.kvest.odessatoday.utils.Utils;

/**
 * Created by kvest on 08.10.15.
 */
public class PlacesAdapter extends CursorAdapter {
    public static final String[] PROJECTION = new String[]{Tables.Places.Columns.PLACE_ID + " as " + Tables.Places.Columns._ID,
                                                           Tables.Places.Columns.NAME, Tables.Places.Columns.RATING,
                                                           Tables.Places.Columns.COMMENTS_COUNT, Tables.Places.Columns.ADDRESS,
                                                           Tables.Places.Columns.PHONES};

    private int nameColumnIndex = -1;
    private int ratingColumnIndex = -1;
    private int commentsCountColumnIndex = -1;
    private int addressColumnIndex = -1;
    private int phonesColumnIndex = -1;
    private int evenItemBgResId, oddItemBgResId, drawablesColor;

    public PlacesAdapter(Context context) {
        super(context, null, 0);

        initResources(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.places_list_item, parent, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView)view.findViewById(R.id.name);
        holder.rating = (RatingBar)view.findViewById(R.id.rating);
        holder.commentsCount = (TextView)view.findViewById(R.id.comments_count);
        holder.address = (TextView)view.findViewById(R.id.address);
        Utils.setDrawablesColor(drawablesColor, holder.address.getCompoundDrawables());
        holder.phones = (TextView)view.findViewById(R.id.phones);
        Utils.setDrawablesColor(drawablesColor, holder.phones.getCompoundDrawables());

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        //set view background
        if (cursor.getPosition() % 2 == 0) {
            view.setBackgroundResource(evenItemBgResId);
        } else {
            view.setBackgroundResource(oddItemBgResId);
        }

        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }

        holder.name.setText(cursor.getString(nameColumnIndex));
        holder.rating.setRating(cursor.getFloat(ratingColumnIndex));
        holder.commentsCount.setText(Utils.createCountString(context, cursor.getInt(commentsCountColumnIndex), Utils.COMMENTS_COUNT_PATTERNS));

        String value = cursor.getString(addressColumnIndex);
        holder.address.setVisibility(TextUtils.isEmpty(value) ? View.GONE : View.VISIBLE);
        holder.address.setText(value);

        value = cursor.getString(phonesColumnIndex);
        holder.phones.setText(value);
        holder.phones.setVisibility(TextUtils.isEmpty(value) ? View.GONE : View.VISIBLE);
    }

    private void initResources(Context context) {
        // The attributes you want retrieved
        int[] attrs = {R.attr.ListEvenItemBgRes, R.attr.ListOddItemBgRes, R.attr.PlacesListDrawablesColor};

        // Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            // Fetching the resources defined in the style
            evenItemBgResId = ta.getResourceId(0, 0);
            oddItemBgResId = ta.getResourceId(1, 0);
            drawablesColor = ta.getColor(2, Color.BLACK);
        } finally {
            ta.recycle();
        }
    }

    private boolean isColumnIndexesCalculated() {
        return (nameColumnIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        nameColumnIndex = cursor.getColumnIndex(Tables.Places.Columns.NAME);
        ratingColumnIndex = cursor.getColumnIndex(Tables.Places.Columns.RATING);
        commentsCountColumnIndex = cursor.getColumnIndex(Tables.Places.Columns.COMMENTS_COUNT);
        addressColumnIndex = cursor.getColumnIndex(Tables.Places.Columns.ADDRESS);
        phonesColumnIndex = cursor.getColumnIndex(Tables.Places.Columns.PHONES);
    }

    private static class ViewHolder {
        private TextView name;
        private TextView address;
        private RatingBar rating;
        private TextView commentsCount;
        private TextView phones;
    }
}
