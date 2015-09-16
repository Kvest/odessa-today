package com.kvest.odessatoday.datamodel;

import com.google.gson.annotations.SerializedName;

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
}
