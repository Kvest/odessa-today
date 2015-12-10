package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.Utils;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;
/**
 * Created by roman on 12/8/15.
 */
public class EventsAdapter extends CursorAdapter {
    public static final String[] PROJECTION = {EventsTimetableView.Columns.EVENT_ID + " as " + EventsTimetableView.Columns._ID,
                                               EventsTimetableView.Columns.IMAGE, EventsTimetableView.Columns.NAME,
                                               EventsTimetableView.Columns.RATING, EventsTimetableView.Columns.COMMENTS_COUNT,
                                               EventsTimetableView.Columns.PLACE_NAME, EventsTimetableView.Columns.DATE,
                                               EventsTimetableView.Columns.PRICES, EventsTimetableView.Columns.HAS_TICKETS};

    private int imageColumnIndex = -1;
    private int nameColumnIndex = -1;
    private int ratingColumnIndex = -1;
    private int commentsCountColumnIndex = -1;
    private int placeNameColumnIndex = -1;
    private int dateColumnIndex = -1;
    private int pricesColumnIndex = -1;
    private int hasTicketsColumnIndex = -1;
    private int evenItemBgColor, oddItemBgColor;

    public EventsAdapter(Context context) {
        super(context, null, 0);

        initResources(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.events_list_item, parent, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.image = (NetworkImageView)view.findViewById(R.id.image);
        holder.image.setDefaultImageResId(R.drawable.loading_poster);
        holder.image.setErrorImageResId(R.drawable.no_poster);
        holder.name = (TextView)view.findViewById(R.id.event_name);
        holder.rating = (RatingBar)view.findViewById(R.id.event_rating);
        holder.commentsCount = (TextView) view.findViewById(R.id.comments_count);
        holder.placeName = (TextView) view.findViewById(R.id.place_name);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        //set view background
        view.setBackgroundColor(cursor.getPosition() % 2 == 0 ? evenItemBgColor : oddItemBgColor);

        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }

        holder.image.setImageUrl(cursor.getString(imageColumnIndex), TodayApplication.getApplication().getVolleyHelper().getImageLoader());
        holder.name.setText(cursor.getString(nameColumnIndex));
        holder.rating.setRating(cursor.getFloat(ratingColumnIndex));
        holder.commentsCount.setText(Utils.createCommentsString(context, cursor.getInt(commentsCountColumnIndex)));
        holder.placeName.setText(cursor.getString(placeNameColumnIndex));
        //TODO
    }

    private boolean isColumnIndexesCalculated() {
        return (imageColumnIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        imageColumnIndex = cursor.getColumnIndex(EventsTimetableView.Columns.IMAGE);
        nameColumnIndex = cursor.getColumnIndex(EventsTimetableView.Columns.NAME);
        ratingColumnIndex = cursor.getColumnIndex(EventsTimetableView.Columns.RATING);
        commentsCountColumnIndex = cursor.getColumnIndex(EventsTimetableView.Columns.COMMENTS_COUNT);
        placeNameColumnIndex = cursor.getColumnIndex(EventsTimetableView.Columns.PLACE_NAME);
        dateColumnIndex = cursor.getColumnIndex(EventsTimetableView.Columns.DATE);
        pricesColumnIndex = cursor.getColumnIndex(EventsTimetableView.Columns.PRICES);
        hasTicketsColumnIndex = cursor.getColumnIndex(EventsTimetableView.Columns.HAS_TICKETS);
    }

    private void initResources(Context context) {
        // The attributes you want retrieved
        int[] attrs = {R.attr.ListEvenItemBg, R.attr.ListOddItemBg};

        // Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            // Fetching the resources defined in the style
            evenItemBgColor = ta.getColor(0, Color.BLACK);
            oddItemBgColor = ta.getColor(1, Color.BLACK);
        } finally {
            ta.recycle();
        }
    }

    private static class ViewHolder {
        public NetworkImageView image;
        private TextView name;
        private RatingBar rating;
        private TextView commentsCount;
        private TextView placeName;
    }
}