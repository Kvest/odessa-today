package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.FontUtils;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 20.08.14
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class CinemasAdapter extends CursorAdapter {
    public static final String[] PROJECTION = new String[]{Tables.Cinemas.Columns.CINEMA_ID + " as " + Tables.Cinemas.Columns._ID,
                                                           Tables.Cinemas.Columns.NAME, Tables.Cinemas.Columns.ADDRESS,
                                                           Tables.Cinemas.Columns.COMMENTS_COUNT};

    private int nameColumnIndex = -1;
    private int addressColumnIndex = -1;
    private int commentsCountColumnIndex = -1;

    private Typeface verandaRegularTypeface;

    public CinemasAdapter(Context context) {
        super(context, null, 0);

        //retrieve font for address and comments
        verandaRegularTypeface = FontUtils.getFont(context.getAssets(), FontUtils.VERANDA_REGULAR_FONT);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cinemas_list_item, viewGroup, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView)view.findViewById(R.id.cinema_name);
        holder.address = (TextView)view.findViewById(R.id.cinema_address);
        holder.commentsCount = (TextView)view.findViewById(R.id.comments_count);

        //set fonts
        holder.address.setTypeface(verandaRegularTypeface);
        holder.commentsCount.setTypeface(verandaRegularTypeface);
        ((TextView)view.findViewById(R.id.comments_count_label)).setTypeface(verandaRegularTypeface);

        view.setTag(holder);

        return view;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }

        holder.name.setText(cursor.getString(nameColumnIndex));
        holder.address.setText(cursor.getString(addressColumnIndex));
        holder.commentsCount.setText(Integer.toString(cursor.getInt(commentsCountColumnIndex)));
    }

    private boolean isColumnIndexesCalculated() {
        return (nameColumnIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        nameColumnIndex = cursor.getColumnIndex(Tables.Cinemas.Columns.NAME);
        addressColumnIndex = cursor.getColumnIndex(Tables.Cinemas.Columns.ADDRESS);
        commentsCountColumnIndex = cursor.getColumnIndex(Tables.Cinemas.Columns.COMMENTS_COUNT);
    }

    private static class ViewHolder {
        private TextView name;
        private TextView address;
        private TextView commentsCount;
    }
}
