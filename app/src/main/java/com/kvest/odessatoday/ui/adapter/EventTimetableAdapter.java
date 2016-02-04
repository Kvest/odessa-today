package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.widget.FormatTextView;
import com.kvest.odessatoday.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created by kvest on 19.12.15.
 */
public class EventTimetableAdapter extends BaseAdapter {
    public static final String[] PROJECTION = new String[]{ EventsTimetable.Columns._ID, EventsTimetable.Columns.PLACE_NAME,
                                                            EventsTimetable.Columns.DATE, EventsTimetable.Columns.PRICES };
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_TIMETABLE_ROW = 1;

    private static final String MIN_MAX_PRICES_SEPARATOR = " / ";
    private static final Pattern PRICES_PATTERN = Pattern.compile("(\\d+)");
    private static final int PRICES_GROUP = 1;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM, EEEE");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private LayoutInflater inflater;
    private List<BaseTimetableItem> dataset;
    private int evenItemBgColor, oddItemBgColor;
    private String currencyStr;

    public EventTimetableAdapter(Context context) {
        super();

        inflater = LayoutInflater.from(context);
        dataset = new ArrayList<>();

        initResources(context);
    }

    @Override
    public int getItemViewType(int position) {
        return dataset.get(position).getViewType();
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return dataset.size();
    }

    @Override
    public BaseTimetableItem getItem(int position) {
        return dataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataset.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (convertView == null) {
            switch (viewType) {
                case VIEW_TYPE_DATE:
                    convertView = createDateItemView(parent);
                    break;
                case VIEW_TYPE_TIMETABLE_ROW :
                    convertView = createTimetableItemView(parent);
                    break;
            }
        }

        //bind data to view
        switch (viewType) {
            case VIEW_TYPE_DATE :
                ((DateItemViewHolder)convertView.getTag()).bind(((DateItem) dataset.get(position)));
                break;
            case VIEW_TYPE_TIMETABLE_ROW :
                ((TimetableItemViewHolder)convertView.getTag()).bind(((TimetableItem) dataset.get(position)));
                break;
        }

        return convertView;
    }

    private View createDateItemView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.event_timetable_list_date_item, parent, false);

        //create holder
        DateItemViewHolder holder = new DateItemViewHolder(view);
        view.setTag(holder);

        return view;
    }

    private View createTimetableItemView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.event_timetable_list_seance_item, parent, false);

        //create holder
        TimetableItemViewHolder holder = new TimetableItemViewHolder(view);
        view.setTag(holder);

        return view;
    }

    public void setCursor(Cursor cursor) {
        dataset.clear();

        if (cursor != null && cursor.moveToFirst()) {
            long prevDay = -1;
            long date;
            int idIndex = cursor.getColumnIndex(EventsTimetable.Columns._ID);
            int placeNameIndex = cursor.getColumnIndex(EventsTimetable.Columns.PLACE_NAME);
            int dateIndex = cursor.getColumnIndex(EventsTimetable.Columns.DATE);
            int pricesIndex = cursor.getColumnIndex(EventsTimetable.Columns.PRICES);
            int rowNumber = 0;
            do {
                date = cursor.getLong(dateIndex);
                if (prevDay != TimeUtils.getBeginningOfTheDay(date)) {
                    DateItem dateItem = new DateItem(date);
                    dateItem.date = date;
                    dataset.add(dateItem);

                    prevDay = TimeUtils.getBeginningOfTheDay(date);
                    rowNumber = 0;
                }

                TimetableItem timetableItem = new TimetableItem(cursor.getLong(idIndex));
                timetableItem.date = date;
                timetableItem.placeName = cursor.getString(placeNameIndex);
                timetableItem.prices = convertPrices(cursor.getString(pricesIndex));
                timetableItem.bgColor = ((rowNumber % 2) == 0 ? evenItemBgColor : oddItemBgColor);
                ++rowNumber;

                dataset.add(timetableItem);
            } while (cursor.moveToNext());
        }

        notifyDataSetChanged();
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

        currencyStr = context.getString(R.string.currency);
    }

    private String convertPrices(String prices) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        Matcher matcher = PRICES_PATTERN.matcher(prices);
        while (matcher.find()) {
            int price = Integer.parseInt(matcher.group(PRICES_GROUP));
            min = Math.min(price, min);
            max = Math.max(price, max);
        }
        if (min == Integer.MAX_VALUE && max == Integer.MIN_VALUE) {
            return "";
        }

        if (min == Integer.MAX_VALUE) {
            return Integer.toString(max) + currencyStr;
        }

        if (max == Integer.MIN_VALUE) {
            return Integer.toString(min) + currencyStr;
        }

        if (max == min) {
            return Integer.toString(max) + currencyStr;
        }

        return Integer.toString(min) + currencyStr + MIN_MAX_PRICES_SEPARATOR + Integer.toString(max) + currencyStr;
    }

    private static abstract class BaseTimetableItem {
        public final long id;

        public BaseTimetableItem(long id) {
            this.id = id;
        }

        public abstract int getViewType();
    }

    private static class DateItem extends BaseTimetableItem {
        public long date;

        public DateItem(long id) {
            super(id);
        }

        public int getViewType() {
            return VIEW_TYPE_DATE;
        }
    }

    private static class TimetableItem extends BaseTimetableItem {
        public long date;
        public String placeName;
        public String prices;
        public int bgColor;

        public TimetableItem(long id) {
            super(id);
        }

        public int getViewType() {
            return VIEW_TYPE_TIMETABLE_ROW;
        }
    }

    private static class DateItemViewHolder {
        private TextView date;

        public DateItemViewHolder(View view) {
            date = (TextView) view;
        }

        public void bind(DateItem dateItem) {
            long dateValue = TimeUnit.SECONDS.toMillis(dateItem.date);
            date.setText(DATE_FORMAT.format(dateValue));
        }
    }

    private static class TimetableItemViewHolder {
        private View parent;
        private TextView seanceTime;
        private TextView placeName;
        private TextView prices;

        public TimetableItemViewHolder(View view) {
            parent = view;
            seanceTime = (TextView)parent.findViewById(R.id.seance_time);
            placeName = (TextView)parent.findViewById(R.id.place_name);
            prices = (TextView)parent.findViewById(R.id.prices);
        }

        public void bind(TimetableItem timetableItem) {
            parent.setBackgroundColor(timetableItem.bgColor);

            long dateValue = TimeUnit.SECONDS.toMillis(timetableItem.date);
            seanceTime.setText(TIME_FORMAT.format(dateValue));
            placeName.setText(timetableItem.placeName);
            prices.setText(timetableItem.prices);
        }
    }
}
