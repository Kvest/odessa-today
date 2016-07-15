package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.TimeUtils;
import com.kvest.odessatoday.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created by kvest on 22.12.15.
 */
public class PlaceTimetableAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 3;
    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_EVENT_INFO = 1;
    private static final int VIEW_TYPE_TIMETABLE_ROW = 2;
    public static final String[] PROJECTION = new String[] {EventsTimetableView.Columns._ID, EventsTimetableView.Columns.DATE,
                                                            EventsTimetableView.Columns.RATING, EventsTimetableView.Columns.COMMENTS_COUNT,
                                                            EventsTimetableView.Columns.NAME, EventsTimetableView.Columns.HAS_TICKETS,
                                                            EventsTimetableView.Columns.PRICES, EventsTimetableView.Columns.EVENT_ID,
                                                            EventsTimetableView.Columns.EVENT_TYPE};

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM, yyy");

    private static final String MIN_MAX_PRICES_SEPARATOR = " / ";
    private static final Pattern PRICES_PATTERN = Pattern.compile("(\\d+)");
    private static final int PRICES_GROUP = 1;

    private Context context;
    private LayoutInflater inflater;
    private List<BaseTimetableItem> dataset;
    private String currencyStr;
    private int evenItemBgResId, oddItemBgResId;

    public PlaceTimetableAdapter(Context context) {
        super();

        this.context = context;
        inflater = LayoutInflater.from(context);
        dataset = new ArrayList<>();

        initResources(context);
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
    public boolean isEnabled(int position) {
        return getItemViewType(position) == VIEW_TYPE_EVENT_INFO;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (convertView == null) {
            switch (viewType) {
                case VIEW_TYPE_DATE :
                    convertView = createDateItemView(parent);
                    break;
                case VIEW_TYPE_EVENT_INFO :
                    convertView = createEventInfoItemView(parent);
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
            case VIEW_TYPE_EVENT_INFO :
                ((EventInfoItemViewHolder) convertView.getTag()).bind(context, (EventInfoItem)dataset.get(position));
                break;
            case VIEW_TYPE_TIMETABLE_ROW :
                ((TimetableItemViewHolder)convertView.getTag()).bind(((TimetableItem) dataset.get(position)));
                break;
        }

        return convertView;
    }

    private View createDateItemView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.place_timetable_list_date_item, parent, false);

        //create holder
        DateItemViewHolder holder = new DateItemViewHolder(view);
        view.setTag(holder);

        return view;
    }

    private View createEventInfoItemView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.place_timetable_list_event_info_item, parent, false);

        //create holder
        EventInfoItemViewHolder holder = new EventInfoItemViewHolder(view);
        view.setTag(holder);

        return view;
    }

    private View createTimetableItemView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.place_timetable_list_seance_item, parent, false);

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
            long prevEventId = -1;
            long eventId;

            int idIndex = cursor.getColumnIndex(EventsTimetableView.Columns._ID);
            int eventIdIndex = cursor.getColumnIndex(EventsTimetableView.Columns.EVENT_ID);
            int dateIndex = cursor.getColumnIndex(EventsTimetableView.Columns.DATE);
            int ratingIndex = cursor.getColumnIndex(EventsTimetableView.Columns.RATING);
            int commentsCountIndex = cursor.getColumnIndex(EventsTimetableView.Columns.COMMENTS_COUNT);
            int eventNameIndex = cursor.getColumnIndex(EventsTimetableView.Columns.NAME);
            int eventTypeIndex = cursor.getColumnIndex(EventsTimetableView.Columns.EVENT_TYPE);
            int hasTicketsIndex = cursor.getColumnIndex(EventsTimetableView.Columns.HAS_TICKETS);
            int pricesIndex = cursor.getColumnIndex(EventsTimetableView.Columns.PRICES);
            int dayNumber = -1;
            do {
                date = cursor.getLong(dateIndex);
                if (prevDay != TimeUtils.getBeginningOfTheDay(date)) {
                    dayNumber++;

                    DateItem dateItem = new DateItem(date);
                    dateItem.date = date;
                    dateItem.backgroundResId = ((dayNumber % 2) == 0 ? evenItemBgResId : oddItemBgResId);
                    dataset.add(dateItem);

                    prevDay = TimeUtils.getBeginningOfTheDay(date);
                    prevEventId = -1;
                }

                eventId = cursor.getLong(eventIdIndex);
                if (eventId != prevEventId) {
                    EventInfoItem eventInfo = new EventInfoItem(eventId);
                    eventInfo.rating = cursor.getFloat(ratingIndex);
                    eventInfo.commentsCount = cursor.getInt(commentsCountIndex);
                    eventInfo.eventName = cursor.getString(eventNameIndex);
                    eventInfo.eventType = cursor.getInt(eventTypeIndex);
                    eventInfo.backgroundResId = ((dayNumber % 2) == 0 ? evenItemBgResId : oddItemBgResId);

                    dataset.add(eventInfo);

                    prevEventId = eventId;
                }

                TimetableItem timetableItem = new TimetableItem(cursor.getLong(idIndex));
                timetableItem.date = date;
                timetableItem.hasTickets = (cursor.getInt(hasTicketsIndex) != 0);
                timetableItem.prices = convertPrices(cursor.getString(pricesIndex));
                timetableItem.backgroundResId = ((dayNumber % 2) == 0 ? evenItemBgResId : oddItemBgResId);

                dataset.add(timetableItem);
            } while (cursor.moveToNext());
        }

        notifyDataSetChanged();
    }

    private void initResources(Context context) {
        // The attributes you want retrieved
        int[] attrs = {R.attr.ListEvenItemBgRes, R.attr.ListOddItemBgRes};

        //Parse style, using Context.obtainStyledAttributes()
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            // Fetching the resources defined in the style
            evenItemBgResId = ta.getResourceId(0, 0);
            oddItemBgResId = ta.getResourceId(1, 0);
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
        public int backgroundResId;

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

    private static class EventInfoItem extends BaseTimetableItem {
        public float rating;
        public int commentsCount;
        public String eventName;
        public int eventType;

        public EventInfoItem(long id) {
            super(id);
        }

        public int getViewType() {
            return VIEW_TYPE_EVENT_INFO;
        }
    }

    private static class TimetableItem extends BaseTimetableItem {
        public long date;
        public boolean hasTickets;
        public String prices;

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
            date.setBackgroundResource(dateItem.backgroundResId);

            long dateValue = TimeUnit.SECONDS.toMillis(dateItem.date);
            date.setText(DATE_FORMAT.format(dateValue));
        }
    }

    private static class EventInfoItemViewHolder {
        private View parent;
        private RatingBar rating;
        private TextView commentsCount;
        private TextView eventName;
        private TextView eventType;

        public EventInfoItemViewHolder(View view) {
            parent = view;
            rating = (RatingBar) parent.findViewById(R.id.event_rating);
            commentsCount = (TextView) parent.findViewById(R.id.comments_count);
            eventName = (TextView) parent.findViewById(R.id.event_name);
            eventType = (TextView) parent.findViewById(R.id.event_type);
        }

        public void bind(Context context, EventInfoItem eventInfoItem) {
            parent.setBackgroundResource(eventInfoItem.backgroundResId);

            rating.setRating(eventInfoItem.rating);
            commentsCount.setText(Utils.createCountString(context, eventInfoItem.commentsCount, Utils.COMMENTS_COUNT_PATTERNS));
            eventName.setText(eventInfoItem.eventName);
            setEventType(eventInfoItem.eventType);
        }

        private void setEventType(int eventTypeValue) {
            eventType.setVisibility(View.VISIBLE);
            switch (eventTypeValue) {
                case Constants.EventType.CONCERT :
                    eventType.setText(R.string.menu_concert);
                    break;
                case Constants.EventType. PARTY :
                    eventType.setText(R.string.menu_party);
                    break;
                case Constants.EventType.SPECTACLE :
                    eventType.setText(R.string.menu_spectacle);
                    break;
                case Constants.EventType.EXHIBITION :
                    eventType.setText(R.string.menu_exhibition);
                    break;
                case Constants.EventType.SPORT :
                    eventType.setText(R.string.menu_sport);
                    break;
                case Constants.EventType.WORKSHOP :
                    eventType.setText(R.string.menu_workshop);
                    break;
                default:
                    eventType.setText("");
                    eventType.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private static class TimetableItemViewHolder {
        private View parent;
        private TextView time;
        private TextView hasTickets;
        private TextView prices;

        public TimetableItemViewHolder(View view) {
            parent = view;
            time = (TextView) parent.findViewById(R.id.seance_time);
            hasTickets = (TextView) parent.findViewById(R.id.has_tickets);
            prices = (TextView) parent.findViewById(R.id.prices);
        }

        public void bind(TimetableItem timetableItem) {
            parent.setBackgroundResource(timetableItem.backgroundResId);

            long dateValue = TimeUnit.SECONDS.toMillis(timetableItem.date);
            time.setText(TIME_FORMAT.format(dateValue));

            hasTickets.setVisibility(timetableItem.hasTickets ? View.VISIBLE : View.GONE);
            prices.setText(timetableItem.prices);
        }
    }
}
