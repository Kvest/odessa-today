package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;
import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 22:38
 * To change this template use File | Settings | File Templates.
 */
public class TimetableItem {
    public long id;
    public long cinema_id;
    public long date;
    public String prices;
    public int format;

    public ContentValues getContentValues(long filmId) {
        ContentValues values = new ContentValues(6);
        values.put(FilmsTimetable.Columns.TIMETABLE_ID, id);
        values.put(FilmsTimetable.Columns.CINEMA_ID, cinema_id);
        values.put(FilmsTimetable.Columns.FILM_ID, filmId);
        values.put(FilmsTimetable.Columns.DATE, date);
        values.put(FilmsTimetable.Columns.PRICES, prices);
        values.put(FilmsTimetable.Columns.FORMAT, format);

        return values;
    }
}
