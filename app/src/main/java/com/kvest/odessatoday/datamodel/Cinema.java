package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;
import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.utils.Utils;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 16.06.14
 * Time: 22:26
 * To change this template use File | Settings | File Templates.
 */
public class Cinema {
    @SerializedName("id")
    public long id;
    @SerializedName("name")
    public String name;
    @SerializedName("address")
    public String address;
    @SerializedName("phones")
    public String phones;
    @SerializedName("transport")
    public String transport;
    @SerializedName("description")
    public String description;
    @SerializedName("rating")
    public float rating;
    @SerializedName("worktime")
    public String worktime;
    @SerializedName("images")
    public String[] images;
    @SerializedName("comments_count")
    public int comments_count;
    @SerializedName("lat")
    public double lat;
    @SerializedName("lon")
    public double lon;

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues(12);
        values.put(Cinemas.Columns.CINEMA_ID, id);
        values.put(Cinemas.Columns.NAME, name);
        values.put(Cinemas.Columns.ADDRESS, address);
        values.put(Cinemas.Columns.PHONES, phones);
        values.put(Cinemas.Columns.TRANSPORT, transport);
        values.put(Cinemas.Columns.DESCRIPTION, description);
        values.put(Cinemas.Columns.WORK_TIME, worktime);
        values.put(Cinemas.Columns.IMAGE, images != null ? Utils.images2String(images) : null);
        values.put(Cinemas.Columns.COMMENTS_COUNT, comments_count);
        values.put(Cinemas.Columns.LON, lon);
        values.put(Cinemas.Columns.LAT, lat);
        values.put(Cinemas.Columns.RATING, rating);

        return values;
    }
}
