package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kvest.odessatoday.R;
import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private static final int ROW_ITEMS_COUNT = 3;
    public static final String[] PROJECTION = new String[]{CinemaTimetableView.Columns._ID, CinemaTimetableView.Columns.NAME,
                                                           CinemaTimetableView.Columns.DATE, CinemaTimetableView.Columns.PRICES,
                                                           CinemaTimetableView.Columns.FORMAT, CinemaTimetableView.Columns.FILM_ID};
    private static final String TIME_FORMAT_PATTERN = "HH:mm";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT_PATTERN);

    private List<AbsItem> items;
    private LayoutInflater inflater;
    private String[] formats;

    public CinemaTimetableAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        items = new ArrayList<AbsItem>();
        formats = context.getResources().getStringArray(R.array.formats);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public AbsItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            convertView = createView(type, parent);
        }

        //set content
        switch (type) {
            case VIEW_TYPE_FILM_NAME:
                FilmNameItem filmNameItem = (FilmNameItem)getItem(position);
                ((TextView)convertView).setText(filmNameItem.filmName);
                break;
            case VIEW_TYPE_TIMETABLE_ROW :
                TimetableRowItem timetableRowItem = (TimetableRowItem)getItem(position);
                TimetableRowViewHolder holder = (TimetableRowViewHolder) convertView.getTag();
                long date;
                for (int i = 0; i < holder.dates.length; ++i) {
                    if (i < timetableRowItem.rowItems.length) {
                        date = TimeUnit.SECONDS.toMillis(timetableRowItem.rowItems[i].date);
                        holder.dates[i].setText(TIME_FORMAT.format(date));
                        holder.prices[i].setText(timetableRowItem.rowItems[i].prices);
                        holder.formats[i].setText(formats[timetableRowItem.rowItems[i].format]);
                        holder.containers[i].setVisibility(View.VISIBLE);
                    } else {
                        holder.containers[i].setVisibility(View.INVISIBLE);
                    }
                }
                break;
        }

        return convertView;
    }

    private View createView(int type, ViewGroup parent) {
        switch (type) {
            case VIEW_TYPE_FILM_NAME:
                return createFilmNameView(parent);
            case VIEW_TYPE_TIMETABLE_ROW :
                return createTimetableRowView(parent);
        }

        return null;
    }

    private View createFilmNameView(ViewGroup parent) {
        View view =  inflater.inflate(R.layout.cinema_timetable_film_name_item, parent, false);
        return view;
    }

    private View createTimetableRowView(ViewGroup parent) {
        View view =  inflater.inflate(R.layout.cinema_timetable_row_item, parent, false);

        //set view holder
        TimetableRowViewHolder holder = new TimetableRowViewHolder(ROW_ITEMS_COUNT);
        holder.containers[0] = view.findViewById(R.id.timetable_item_container1);
        holder.dates[0] = (TextView)view.findViewById(R.id.date1);
        holder.prices[0] = (TextView)view.findViewById(R.id.prices1);
        holder.formats[0] = (TextView)view.findViewById(R.id.format1);
        holder.containers[1] = view.findViewById(R.id.timetable_item_container2);
        holder.dates[1] = (TextView)view.findViewById(R.id.date2);
        holder.prices[1] = (TextView)view.findViewById(R.id.prices2);
        holder.formats[1] = (TextView)view.findViewById(R.id.format2);
        holder.containers[2] = view.findViewById(R.id.timetable_item_container3);
        holder.dates[2] = (TextView)view.findViewById(R.id.date3);
        holder.prices[2] = (TextView)view.findViewById(R.id.prices3);
        holder.formats[2] = (TextView)view.findViewById(R.id.format3);
        view.setTag(holder);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).itemViewType;
    }

    public void setCursor(Cursor cursor) {
        //delete old items
        items.clear();

        //add new items
        cursor.moveToFirst();
        LongSparseArray<TmpContainer> containers = new LongSparseArray<TmpContainer>();
        while (!cursor.isAfterLast()) {
            //parse data
            long filmId =cursor.getLong(cursor.getColumnIndex(CinemaTimetableView.Columns.FILM_ID));
            long date =cursor.getLong(cursor.getColumnIndex(CinemaTimetableView.Columns.DATE));
            String prices = cursor.getString(cursor.getColumnIndex(CinemaTimetableView.Columns.PRICES));
            int format = cursor.getInt(cursor.getColumnIndex(CinemaTimetableView.Columns.FORMAT));

            //create item
            TimetableItem timetableItem = new TimetableItem(date, prices, format);

            //retrieve TmpContainer or create new if it is not exists
            TmpContainer tmpContainer = containers.get(filmId);
            if (tmpContainer == null) {
                String filmName = cursor.getString(cursor.getColumnIndex(CinemaTimetableView.Columns.NAME));
                tmpContainer = new TmpContainer(filmName);
                containers.put(filmId, tmpContainer);
            }

            //add TimetableItem to the container
            tmpContainer.items.add(timetableItem);

            cursor.moveToNext();
        }

        //copy data from tmp containers to main items list
        for (int i = 0; i < containers.size(); ++i) {
            long filmId = containers.keyAt(i);
            TmpContainer tmpContainer = containers.valueAt(i);

            //add film name
            items.add(new FilmNameItem(filmId, tmpContainer.filmName));

            //add timetable items
            while (!tmpContainer.items.isEmpty()) {
                //calculate timetable items cont
                int count = Math.min(ROW_ITEMS_COUNT, tmpContainer.items.size());

                //generate row items array
                TimetableItem[] rowItems = new TimetableItem[count];
                for (int j = 0; j < count; ++j) {
                    rowItems[j] = tmpContainer.items.remove(0);
                }

                //add row
                items.add(new TimetableRowItem(filmId, rowItems));
            }
        }

        notifyDataSetChanged();
    }

    private static abstract class AbsItem {
        public long id;
        public int itemViewType;

        protected AbsItem(long id, int itemViewType) {
            this.id = id;
            this.itemViewType = itemViewType;
        }
    }

    private static class FilmNameItem extends AbsItem {
        public String filmName;
        private FilmNameItem(long id, String filmName) {
            super(id, VIEW_TYPE_FILM_NAME);

            this.filmName = filmName;
        }
    }

    private static class TimetableRowItem extends AbsItem {
        public TimetableItem[] rowItems;

        private TimetableRowItem(long id, TimetableItem[] rowItems) {
            super(id, VIEW_TYPE_TIMETABLE_ROW);
            this.rowItems = rowItems;
        }
    }

    private static class TimetableItem {
        public long date;
        public String prices;
        public int format;

        private TimetableItem(long date, String prices, int format) {
            this.date = date;
            this.prices = prices;
            this.format = format;
        }
    }

    private static class TmpContainer {
        public String filmName;
        public List<TimetableItem> items;

        public TmpContainer(String filmName) {
            this.filmName = filmName;
            items = new LinkedList<TimetableItem>();
        }
    }

    private static class TimetableRowViewHolder {
        private View[] containers;
        private TextView[] dates;
        private TextView[] prices;
        private TextView[] formats;

        public TimetableRowViewHolder(int itemsCount) {
            containers = new View[itemsCount];
            dates = new TextView[itemsCount];
            prices = new TextView[itemsCount];
            formats = new TextView[itemsCount];
        }
    }
}
