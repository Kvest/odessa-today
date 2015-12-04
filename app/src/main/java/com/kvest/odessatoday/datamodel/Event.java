package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created by kvest on 30.11.15.
 */
public class Event {
    @SerializedName("id")
    public long id;
    @SerializedName("type")
    public long type;
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

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues(9);
        values.put(Events.Columns.EVENT_ID, id);
        values.put(Events.Columns.EVENT_TYPE, type);
        values.put(Events.Columns.IMAGE, image);
        values.put(Events.Columns.NAME, name);
        values.put(Events.Columns.DIRECTOR, director);
        values.put(Events.Columns.ACTORS, actors);
        values.put(Events.Columns.DESCRIPTION, description);
        values.put(Events.Columns.RATING, rating);
        values.put(Events.Columns.COMMENTS_COUNT, commentsCount);

        return values;
    }
    
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

        public ContentValues getContentValues(long eventId) {
            ContentValues values = new ContentValues(7);
            values.put(EventsTimetable.Columns.TIMETABLE_ID, id);
            values.put(EventsTimetable.Columns.EVENT_ID, eventId);
            values.put(EventsTimetable.Columns.PLACE_ID, placeId);
            values.put(EventsTimetable.Columns.PLACE_NAME, placeName);
            values.put(EventsTimetable.Columns.DATE, date);
            values.put(EventsTimetable.Columns.PRICES, prices);
            values.put(EventsTimetable.Columns.HAS_TICKETS, hasTickets ? 1 : 0);

            return values;
        }
    }
}
