package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kvest.odessatoday.R;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 31.07.14
 * Time: 23:24
 * To change this template use File | Settings | File Templates.
 */
public class TimetableAdapter extends CursorAdapter {
    public static final String[] PROJECTION = new String[]{Tables.FilmsFullTimetable.Columns._ID, Tables.FilmsFullTimetable.Columns.CINEMA_NAME,
                                                           Tables.FilmsFullTimetable.Columns.DATE, Tables.FilmsFullTimetable.Columns.PRICES};
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private int cinemaNameIndex = -1;
    private int dateIndex = -1;
    private int pricesIndex = -1;

    public TimetableAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.timetable_list_item, viewGroup, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.cinemaName = (TextView)view.findViewById(R.id.cinema_name);
        holder.date = (TextView)view.findViewById(R.id.date);
        holder.prices = (TextView)view.findViewById(R.id.prices);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }
        holder.cinemaName.setText(cursor.getString(cinemaNameIndex));
        long dateValue = TimeUnit.SECONDS.toMillis(cursor.getLong(dateIndex));
        holder.date.setText(DATE_FORMAT.format(dateValue));
        holder.prices.setText(cursor.getString(pricesIndex));
    }

    private boolean isColumnIndexesCalculated() {
        return (cinemaNameIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        cinemaNameIndex = cursor.getColumnIndex(Tables.FilmsFullTimetable.Columns.CINEMA_NAME);
        dateIndex = cursor.getColumnIndex(Tables.FilmsFullTimetable.Columns.DATE);
        pricesIndex = cursor.getColumnIndex(Tables.FilmsFullTimetable.Columns.PRICES);
    }

    private static class ViewHolder {
        private TextView cinemaName;
        private TextView date;
        private TextView prices;
    }
}
