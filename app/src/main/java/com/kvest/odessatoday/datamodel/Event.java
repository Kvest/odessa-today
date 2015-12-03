package com.kvest.odessatoday.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kvest on 30.11.15.
 */
public class Event {
    @SerializedName("id")
    public long id;
    @SerializedName("image")
    public String image;
    @SerializedName("name")
    public String name;
    @SerializedName("director")
    public String director;
    @SerializedName("actors")
    public String actors;
    @SerializedName("description")
    public String description;
    @SerializedName("rating")
    public float rating;
    @SerializedName("comments_count")
    public int commentsCount;
    @SerializedName("timetable")
    public Timetable[] timetable;

    public static class Timetable {
        @SerializedName("id")
        public long id;
        @SerializedName("place_id")
        public long placeId;
        @SerializedName("place_name")
        public String placeName;
        @SerializedName("date")
        public long date;
        @SerializedName("prices")
        public String prices;
        @SerializedName("have_tickets")
        public boolean hasTickets;
    }
}
