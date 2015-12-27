package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.widget.FormatTextView;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 22.11.14
 * Time: 20:38
 * To change this template use File | Settings | File Templates.
 */
public class CinemaTimetableAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_FILM_NAME = 0;
    private static final int VIEW_TYPE_TIMETABLE_ROW = 1;
    public static final String[] PROJECTION = new String[]{CinemaTimetableView.Columns._ID, CinemaTimetableView.Columns.NAME,
                                                           CinemaTimetableView.Columns.DATE, CinemaTimetableView.Columns.PRICES,
                                                           CinemaTimetableView.Columns.FORMAT, CinemaTimetableView.Columns.FILM_ID};
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private static final String MIN_MAX_PRICES_SEPARATOR = " / ";
    private static final Pattern PRICES_PATTERN = Pattern.compile("(\\d+)");
    private static final int PRICES_GROUP = 1;

    private LayoutInflater inflater;
    private List<BaseTimetableItem> dataset;
    private String currencyStr;

    public CinemaTimetableAdapter(Context context) {
        super();

        inflater = LayoutInflater.from(context);
        dataset = new ArrayList<>();
        currencyStr = context.getString(R.string.currency);
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
    public boolean isEnabled(int position) {
        return getItemViewType(position) == VIEW_TYPE_FILM_NAME;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (convertView == null) {
            switch (viewType) {
                case VIEW_TYPE_FILM_NAME:
                    convertView = createFilmItemView(parent);
                    break;
                case VIEW_TYPE_TIMETABLE_ROW :
                    convertView = createTimetableItemView(parent);
                    break;
            }
        }

        //bind data to view
        switch (viewType) {
            case VIEW_TYPE_FILM_NAME :
                ((FilmItemViewHolder)convertView.getTag()).bind(((FilmItem) dataset.get(position)));
                break;
            case VIEW_TYPE_TIMETABLE_ROW :
                ((TimetableItemViewHolder)convertView.getTag()).bind(((TimetableItem) dataset.get(position)));
                break;
        }

        return convertView;
    }

    private View createFilmItemView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.timetable_list_film_item, parent, false);

        //create holder
        FilmItemViewHolder holder = new FilmItemViewHolder(view);
        view.setTag(holder);

        return view;
    }

    private View createTimetableItemView(ViewGroup parent) {
        View view = inflater.inflate(R.layout.timetable_list_seance_item, parent, false);

        //create holder
        TimetableItemViewHolder holder = new TimetableItemViewHolder(view);
        view.setTag(holder);

        return view;
    }

    public void setCursor(Cursor cursor) {
        dataset.clear();

        if (cursor != null && cursor.moveToFirst()) {
            long prevFilmId = -1;
            long filmId;
            int filmIdIndex = cursor.getColumnIndex(CinemaTimetableView.Columns.FILM_ID);
            int idIndex = cursor.getColumnIndex(CinemaTimetableView.Columns._ID);
            int filmNameIndex = cursor.getColumnIndex(CinemaTimetableView.Columns.NAME);
            int dateIndex = cursor.getColumnIndex(CinemaTimetableView.Columns.DATE);
            int pricesIndex = cursor.getColumnIndex(CinemaTimetableView.Columns.PRICES);
            int formatIndex = cursor.getColumnIndex(CinemaTimetableView.Columns.FORMAT);
            do {
                filmId = cursor.getLong(filmIdIndex);
                if (filmId != prevFilmId) {
                    FilmItem cinema = new FilmItem(filmId);
                    cinema.filmName = cursor.getString(filmNameIndex);

                    dataset.add(cinema);

                    prevFilmId = filmId;
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

    private static abstract class BaseTimetableItem {
        public final long id;

        public BaseTimetableItem(long id) {
            this.id = id;
        }

        public abstract int getViewType();
    }

    private static class FilmItem extends BaseTimetableItem {
        public String filmName;

        public FilmItem(long id) {
            super(id);
        }

        public int getViewType() {
            return VIEW_TYPE_FILM_NAME;
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
            return VIEW_TYPE_TIMETABLE_ROW;
        }
    }

    private static class FilmItemViewHolder {
        private TextView filmName;

        public FilmItemViewHolder(View view) {
            filmName = (TextView) view.findViewById(R.id.film_name);
        }

        public void bind(FilmItem filmItem) {
            filmName.setText(filmItem.filmName);
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
