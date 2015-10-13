package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.TodayProviderContract.*;
import com.kvest.odessatoday.utils.FontUtils;
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
    private int evenItemBgColor, oddItemBgColor, drawablesColor;

    private Typeface helveticaneuecyrRoman, helveticaneuecyrBold;

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
        holder.name.setTypeface(helveticaneuecyrRoman);
        holder.rating = (RatingBar)view.findViewById(R.id.rating);
        holder.commentsCount = (TextView)view.findViewById(R.id.comments_count);
        holder.commentsCount.setTypeface(helveticaneuecyrBold);
        holder.address = (TextView)view.findViewById(R.id.address);
        holder.address.setTypeface(helveticaneuecyrRoman);
        setDrawablesColor(holder.address.getCompoundDrawables());
        holder.phones = (TextView)view.findViewById(R.id.phones);
        holder.phones.setTypeface(helveticaneuecyrRoman);
        setDrawablesColor(holder.phones.getCompoundDrawables());

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        //set view background
        if (cursor.getPosition() % 2 == 0) {
            view.setBackgroundColor(evenItemBgColor);
        } else {
            view.setBackgroundColor(oddItemBgColor);
        }

        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }

        holder.name.setText(cursor.getString(nameColumnIndex));
        holder.rating.setRating(cursor.getFloat(ratingColumnIndex));
        holder.commentsCount.setText(Utils.createCommentsString(context, cursor.getInt(commentsCountColumnIndex)));

        String value = cursor.getString(addressColumnIndex);
        holder.address.setVisibility(TextUtils.isEmpty(value) ? View.GONE : View.VISIBLE);
        holder.address.setText(value);

        value = cursor.getString(phonesColumnIndex);
        holder.phones.setText(value);
        holder.phones.setVisibility(TextUtils.isEmpty(value) ? View.GONE : View.VISIBLE);
    }

    private void setDrawablesColor(Drawable[] drawables) {
        for (int i = 0; i < drawables.length; i++) {
            if (drawables[i] != null) {
                drawables[i].setColorFilter(drawablesColor, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private void initResources(Context context) {
        // The attributes you want retrieved
        int[] attrs = {R.attr.ListEvenItemBg, R.attr.ListOddItemBg, R.attr.PlacesListDrawablesColor};

        // Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            // Fetching the resources defined in the style
            evenItemBgColor = ta.getColor(0, Color.BLACK);
            oddItemBgColor = ta.getColor(1, Color.BLACK);
            drawablesColor = ta.getColor(2, Color.BLACK);
        } finally {
            ta.recycle();
        }

        //retrieve fonts
        helveticaneuecyrRoman = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_ROMAN_FONT);
        helveticaneuecyrBold = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_BOLD_FONT);
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
