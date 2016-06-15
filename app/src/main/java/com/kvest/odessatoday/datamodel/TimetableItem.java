package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 22:38
 * To change this template use File | Settings | File Templates.
 */
public class TimetableItem {
    @SerializedName("id")
    public long id;
    @SerializedName("cinema_id")
    public long cinema_id;
    @SerializedName("date")
    public long date;
    @SerializedName("prices")
    public String prices;
    @SerializedName("format")
    public int format;
    @SerializedName("have_tickets")
    public boolean hasTickets;
    @SerializedName("sectors")
    public Sector[] sectors;

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
