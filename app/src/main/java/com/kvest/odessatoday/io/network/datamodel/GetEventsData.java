package com.kvest.odessatoday.io.network.datamodel;

import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.datamodel.Event;

import java.util.List;

/**
 * Created by kvest on 29.11.15.
 */
public class GetEventsData {
    @SerializedName("date")
    public long date;
    @SerializedName("events")
    public List<Event> events;
}
