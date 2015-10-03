package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.widget.FormatTextView;
import com.kvest.odessatoday.utils.FontUtils;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kvest on 02.10.15.
 */
public class TimetableAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_CINEMA = 0;
    private static final int VIEW_TYPE_TIMETABLE = 1;
    private static final String TIME_FORMAT_PATTERN = "HH:mm";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT_PATTERN);

    private static final String MIN_MAX_PRICES_SEPARATOR = " / ";
    private static final Pattern PRICES_PATTERN = Pattern.compile("(\\d+)");
    private static final int PRICES_GROUP = 1;

    public static final String[] PROJECTION = new String[]{FilmsFullTimetableView.Columns._ID, FilmsFullTimetableView.Columns.CINEMA_NAME,
                                                           FilmsFullTimetableView.Columns.DATE, FilmsFullTimetableView.Columns.PRICES,
                                                           FilmsFullTimetableView.Columns.CINEMA_ADDRESS, FilmsFullTimetableView.Columns.FORMAT,
                                                           FilmsFullTimetableView.Columns.CINEMA_ID};

    private LayoutInflater inflater;
    private List<BaseTimetableItem> dataset;
    private String currencyStr;
    private Typeface helveticaneuecyrRoman, helveticaneuecyrBold;

    public TimetableAdapter(Context context) {
        super();

        inflater = LayoutInflater.from(context);
        dataset = new ArrayList<>();
        currencyStr = context.getString(R.string.currency);

        //retrieve fonts
        helveticaneuecyrRoman = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_ROMAN_FONT);
        helveticaneuecyrBold = FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_BOLD_FONT);
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
                case VIEW_TYPE_CINEMA :
                    convertView = createCinemaItemView(parent);
                    break;
                case VIEW_TYPE_TIMETABLE :
                    convertView = createTimetableItemView(parent);
                    break;
            }
        }

        //bind data to view
        switch (viewType) {
            case VIEW_TYPE_CINEMA :
                ((CinemaItemViewHolder)convertView.getTag()).bind(((CinemaItem) dataset.get(position)));
                break;
            case VIEW_TYPE_TIMETABLE :
                ((TimetableItemViewHolder)convertView.getTag()).bind(((TimetableItem) dataset.get(position)));
                break;
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == VIEW_TYPE_CINEMA;
    }

    public void swapCursor(Cursor cursor) {
        dataset.clear();

        if (cursor != null && cursor.moveToFirst()) {
            long prevCinemaId = -1;
            long cinemaId;
            int cinemaIdIndex = cursor.getColumnIndex(FilmsFullTimetableView.Columns.CINEMA_ID);
            int idIndex = cursor.getColumnIndex(FilmsFullTimetableView.Columns._ID);
            int cinemaNameIndex = cursor.getColumnIndex(FilmsFullTimetableView.Columns.CINEMA_NAME);
            int dateIndex = cursor.getColumnIndex(FilmsFullTimetableView.Columns.DATE);
            int pricesIndex = cursor.getColumnIndex(FilmsFullTimetableView.Columns.PRICES);
            int formatIndex = cursor.getColumnIndex(FilmsFullTimetableView.Columns.FORMAT);
            int cinemaAddressIndex = cursor.getColumnIndex(FilmsFullTimetableView.Columns.CINEMA_ADDRESS);
            do {
                cinemaId = cursor.getLong(cinemaIdIndex);
                if (cinemaId != prevCinemaId) {
                    CinemaItem cinema = new CinemaItem(cinemaId);
                    cinema.cinemaName = cursor.getString(cinemaNameIndex);
                    cinema.cinemaAddress = cursor.getString(cinemaAddressIndex);

                    dataset.add(cinema);

                    prevCinemaId = cinemaId;
                }

                TimetableItem item = new TimetableItem(cursor.getLong(idIndex));
                item.date = cursor.getLong(dateIndex);
                item.format = cursor.getInt(formatIndex);
                item.prices = convertPrices(cursor.getString(pricesIndex));

                dataset.add(item);
            } while (cursor.moveToNext());
        }

        notifyDataSetChanged();
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

    private View createCinemaItemView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.timetable_list_cinema_item, parent, false);

        //create holder
        CinemaItemViewHolder holder = new CinemaItemViewHolder(view);
        view.setTag(holder);

        //set fonts
        holder.cinemaName.setTypeface(helveticaneuecyrBold);
        holder.cinemaAddress.setTypeface(helveticaneuecyrRoman);

        return view;
    }

    private View createTimetableItemView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.timetable_list_seance_item, parent, false);

        //create holder
        TimetableItemViewHolder holder = new TimetableItemViewHolder(view);
        view.setTag(holder);

        //set fonts
        holder.seanceTime.setTypeface(helveticaneuecyrRoman);
        holder.prices.setTypeface(helveticaneuecyrRoman);

        return view;
    }

    private static abstract class BaseTimetableItem {
        public long id;

        public BaseTimetableItem(long id) {
            this.id = id;
        }

        public abstract int getViewType();
    }

    private static class CinemaItem extends BaseTimetableItem {
        public String cinemaName;
        public String cinemaAddress;

        public CinemaItem(long id) {
            super(id);
        }

        public int getViewType() {
            return VIEW_TYPE_CINEMA;
        }
    }

    private static class TimetableItem extends BaseTimetableItem {
        public long date;
        public int format;
        public String prices;

        public TimetableItem(long id) {
            super(id);
        }

        public int getViewType() {
            return VIEW_TYPE_TIMETABLE;
        }
    }

    private static class CinemaItemViewHolder {
        private TextView cinemaName;
        private TextView cinemaAddress;

        public CinemaItemViewHolder(View view) {
            cinemaName = (TextView) view.findViewById(R.id.cinema_name);
            cinemaAddress = (TextView) view.findViewById(R.id.cinema_address);
        }

        public void bind(CinemaItem cinemaItem) {
            cinemaName.setText(cinemaItem.cinemaName);
            cinemaAddress.setText(cinemaItem.cinemaAddress);
        }
    }

    private static class TimetableItemViewHolder {
        private TextView seanceTime;
        private FormatTextView format;
        private TextView prices;

        public TimetableItemViewHolder(View view) {
            seanceTime = (TextView)view.findViewById(R.id.seance_time);
            format = (FormatTextView)view.findViewById(R.id.format);
            prices = (TextView)view.findViewById(R.id.prices);
        }

        public void bind(TimetableItem timetableItem) {
            long dateValue = TimeUnit.SECONDS.toMillis(timetableItem.date);
            seanceTime.setText(TIME_FORMAT.format(dateValue));
            format.setFormat(timetableItem.format);
            prices.setText(timetableItem.prices);
        }
    }
}
