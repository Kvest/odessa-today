package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;
import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

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
    public int comments_count;
    public int is_premiere;
    public int film_duration;
    public List<TimetableItem> timetable;

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues(12);
        values.put(Films.Columns.FILM_ID, id);
        values.put(Films.Columns.NAME, filmname);
        values.put(Films.Columns.COUNTRY, country);
        values.put(Films.Columns.YEAR, year);
        values.put(Films.Columns.DIRECTOR, director);
        values.put(Films.Columns.ACTORS, actors);
        values.put(Films.Columns.DESCRIPTION, description);
        values.put(Films.Columns.IMAGE, image);
        values.put(Films.Columns.VIDEO, video);
        values.put(Films.Columns.GENRE, genre);
        values.put(Films.Columns.RATING, rating);
        values.put(Films.Columns.COMMENTS_COUNT, comments_count);
        values.put(Films.Columns.IS_PREMIERE, is_premiere);
        values.put(Films.Columns.FILM_DURATION, film_duration);

        return values;
    }
}
