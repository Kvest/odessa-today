package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
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
    @SerializedName("id")
    public long id;
    @SerializedName("filmname")
    public String filmname;
    @SerializedName("country")
    public String country;
    @SerializedName("year")
    public String year;
    @SerializedName("director")
    public String director;
    @SerializedName("actors")
    public String actors;
    @SerializedName("description")
    public String description;
    @SerializedName("image")
    public String image;
    @SerializedName("video")
    public String video;
    @SerializedName("genre")
    public String genre;
    @SerializedName("rating")
    public float rating;
    @SerializedName("comments_count")
    public int comments_count;
    @SerializedName("is_premiere")
    public int is_premiere;
    @SerializedName("film_duration")
    public int film_duration;
    @SerializedName("posters")
    public String[] posters;
    @SerializedName("share_text")
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
