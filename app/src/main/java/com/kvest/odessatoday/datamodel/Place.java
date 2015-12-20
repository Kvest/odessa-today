package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.utils.Utils;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created by kvest on 16.09.15.
 */
public class Place {
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
    @SerializedName("comments_count")
    public int commentsCount;
    @SerializedName("worktime")
    public String worktime;
    @SerializedName("images")
    public String[] images;
    @SerializedName("lat")
    public double lat;
    @SerializedName("lon")
    public double lon;
    @SerializedName("rating")
    public float rating;

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues(13); // + 1 for the Places.Columns.PLACE_TYPE
        values.put(Places.Columns.PLACE_ID, id);
        values.put(Places.Columns.NAME, name);
        values.put(Places.Columns.ADDRESS, address);
        values.put(Places.Columns.PHONES, phones);
        values.put(Places.Columns.TRANSPORT, transport);
        values.put(Places.Columns.DESCRIPTION, description);
        values.put(Places.Columns.WORK_TIME, worktime);
        values.put(Places.Columns.IMAGE, images != null ? Utils.images2String(images) : null);
        values.put(Places.Columns.COMMENTS_COUNT, commentsCount);
        values.put(Places.Columns.LON, lon);
        values.put(Places.Columns.LAT, lat);
        values.put(Places.Columns.RATING, rating);

        return values;
    }
}
