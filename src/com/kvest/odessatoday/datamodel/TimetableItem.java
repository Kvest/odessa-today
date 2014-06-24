package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;
import com.kvest.odessatoday.provider.TodayProviderContract;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 22:38
 * To change this template use File | Settings | File Templates.
 */
public class TimetableItem {
    public long cinema_id;
    public long date;
    public String prices;
    public int format;

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues(4);
        values.put(TodayProviderContract.Tables.FilmsTimetable.Columns.CINEMA_ID, cinema_id);
        values.put(TodayProviderContract.Tables.FilmsTimetable.Columns.DATE, date);
        values.put(TodayProviderContract.Tables.FilmsTimetable.Columns.PRICES, prices);
        values.put(TodayProviderContract.Tables.FilmsTimetable.Columns.FORMAT, format);

        return values;
    }
}
