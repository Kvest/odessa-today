package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;
import android.text.TextUtils;
import com.kvest.odessatoday.provider.TodayProviderContract;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.14
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
public class Film {
    private static final String POSTER_SEPARATOR = ",";
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
    public String[] posters;
    public String share_text;

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues(16);
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
        values.put(TodayProviderContract.Tables.Films.Columns.COMMENTS_COUNT, comments_count);
        values.put(TodayProviderContract.Tables.Films.Columns.IS_PREMIERE, is_premiere);
        values.put(TodayProviderContract.Tables.Films.Columns.FILM_DURATION, film_duration);
        values.put(TodayProviderContract.Tables.Films.Columns.POSTERS, posters2String(posters));
        values.put(TodayProviderContract.Tables.Films.Columns.SHARE_TEXT, share_text);

        return values;
    }

    public static String posters2String(String[] posters) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0 ; i < posters.length; ++i) {
            if (i > 0) {
                builder.append(POSTER_SEPARATOR);
            }
            builder.append(posters[i]);
        }

        return builder.toString();
    }

    public static String[] string2Posters(String value) {
        if (!TextUtils.isEmpty(value)) {
            return value.split(POSTER_SEPARATOR);
        } else {
            return new String[0];
        }
    }
}
