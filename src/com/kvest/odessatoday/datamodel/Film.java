package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;
import com.kvest.odessatoday.provider.TodayProviderContract;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 22:40
 * To change this template use File | Settings | File Templates.
 */
public class Film {
    public long id;
    public String filmname;
    public String country;
    public String year;
    public String director;
    public String actors;
    public String description;
    public String image;
    public String video;
    public String genre;
    public float rating;
    public List<TimetableItem> timetable;

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues(11);
        values.put(TodayProviderContract.Tables.Films.Columns.FILM_ID, id);
        values.put(TodayProviderContract.Tables.Films.Columns.NAME, filmname);
        values.put(TodayProviderContract.Tables.Films.Columns.COUNTRY, country);
        values.put(TodayProviderContract.Tables.Films.Columns.YEAR, year);
        values.put(TodayProviderContract.Tables.Films.Columns.DIRECTOR, director);
        values.put(TodayProviderContract.Tables.Films.Columns.ACTORS, actors);
        values.put(TodayProviderContract.Tables.Films.Columns.DESCRIPTION, description);
        values.put(TodayProviderContract.Tables.Films.Columns.IMAGE, image);
        values.put(TodayProviderContract.Tables.Films.Columns.VIDEO, video);
        values.put(TodayProviderContract.Tables.Films.Columns.GENRE, genre);
        values.put(TodayProviderContract.Tables.Films.Columns.RATING, rating);

        return values;
    }
}
