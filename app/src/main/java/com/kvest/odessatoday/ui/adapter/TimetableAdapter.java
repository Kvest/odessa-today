package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
import static com.kvest.odessatoday.utils.Constants.*;
/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 31.07.14
 * Time: 23:24
 * To change this template use File | Settings | File Templates.
 */
public class TimetableAdapter extends CursorAdapter {
    public static final String[] PROJECTION = new String[]{Tables.FilmsFullTimetableView.Columns._ID, Tables.FilmsFullTimetableView.Columns.CINEMA_NAME,
                                                           Tables.FilmsFullTimetableView.Columns.DATE, Tables.FilmsFullTimetableView.Columns.PRICES,
                                                           Tables.FilmsFullTimetableView.Columns.CINEMA_ADDRESS, Tables.FilmsFullTimetableView.Columns.FORMAT,
                                                           Tables.FilmsFullTimetableView.Columns.CINEMA_ID};
    private static final String TIME_FORMAT_PATTERN = "HH:mm";
    private final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat(TIME_FORMAT_PATTERN);

    private int cinemaIdIndex = -1;
    private int cinemaNameIndex = -1;
    private int dateIndex = -1;
    private int pricesIndex = -1;
    private int formatIndex = -1;
    private int cinemaAddressIndex = -1;

    private Typeface robotoLightTypeface;

    public TimetableAdapter(Context context) {
        super(context, null, 0);

        //create typeface
        robotoLightTypeface = Typeface.create(Constants.ROBOTO_LIGHT_FONT_NAME, Typeface.NORMAL);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.timetable_list_item, viewGroup, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.filmFormat = (ImageView)view.findViewById(R.id.film_format);
        holder.cinemaAddress = (TextView)view.findViewById(R.id.cinema_address);
        holder.cinemaName = (TextView)view.findViewById(R.id.cinema_name);
        holder.time = (TextView)view.findViewById(R.id.time);
        holder.prices = (TextView)view.findViewById(R.id.prices);

        //set typefaces
        holder.cinemaName.setTypeface(robotoLightTypeface);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }

        holder.cinemaId = cursor.getLong(cinemaIdIndex);
        holder.cinemaName.setText(cursor.getString(cinemaNameIndex));
        holder.cinemaAddress.setText(cursor.getString(cinemaAddressIndex));
        long dateValue = TimeUnit.SECONDS.toMillis(cursor.getLong(dateIndex));
        holder.time.setText(TIME_FORMAT.format(dateValue));
        holder.prices.setText(cursor.getString(pricesIndex));

        //film format
        switch (cursor.getInt(formatIndex)) {
            case FilmFormat.THIRTY_FIFE_MM:
                holder.filmFormat.setImageResource(R.drawable.thirty_five_mm);
                break;
            case FilmFormat.TWO_D:
                holder.filmFormat.setImageResource(R.drawable.two_d);
                break;
            case FilmFormat.THREE_D:
                holder.filmFormat.setImageResource(R.drawable.three_d);
                break;
            case FilmFormat.IMAX_THREE_D:
                holder.filmFormat.setImageResource(R.drawable.imax3d);
                break;
            case FilmFormat.IMAX:
                holder.filmFormat.setImageResource(R.drawable.imax);
                break;
            case FilmFormat.FIVE_D:
                holder.filmFormat.setImageResource(R.drawable.five_d);
                break;
            case FilmFormat.FOUR_DX:
                holder.filmFormat.setImageResource(R.drawable.imax4dx);
                break;
            default:
                holder.filmFormat.setImageResource(R.drawable.ic_empty_film_format);
        }
    }

    public long getCinemaId(View view, int position, long id) {
        ViewHolder holder = (ViewHolder)view.getTag();

        return holder.cinemaId;
    }

    private boolean isColumnIndexesCalculated() {
        return (cinemaNameIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        cinemaIdIndex = cursor.getColumnIndex(Tables.FilmsFullTimetableView.Columns.CINEMA_ID);
        cinemaNameIndex = cursor.getColumnIndex(Tables.FilmsFullTimetableView.Columns.CINEMA_NAME);
        dateIndex = cursor.getColumnIndex(Tables.FilmsFullTimetableView.Columns.DATE);
        pricesIndex = cursor.getColumnIndex(Tables.FilmsFullTimetableView.Columns.PRICES);
        formatIndex = cursor.getColumnIndex(Tables.FilmsFullTimetableView.Columns.FORMAT);
        cinemaAddressIndex = cursor.getColumnIndex(Tables.FilmsFullTimetableView.Columns.CINEMA_ADDRESS);
    }

    private static class ViewHolder {
        private long cinemaId;
        private ImageView filmFormat;
        private TextView cinemaName;
        private TextView cinemaAddress;
        private TextView time;
        private TextView prices;
    }
}
