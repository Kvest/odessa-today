package com.kvest.odessatoday.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 29.12.14
 * Time: 20:58
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementFilmsAdapter extends CursorAdapter {
    public static final String[] PROJECTION = new String[]{Tables.AnnouncementFilmsView.Columns.FILM_ID + " as " + Tables.AnnouncementFilmsView.Columns._ID};

    public AnnouncementFilmsAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
