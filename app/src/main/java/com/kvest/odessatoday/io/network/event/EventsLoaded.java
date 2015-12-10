package com.kvest.odessatoday.io.network.event;

/**
 * Created by kvest on 10.12.15.
 */
public class EventsLoaded {
    private int type = -1;
    private long placeId = -1;
    private boolean successful;

    public EventsLoaded(int type, long placeId, boolean successful) {
        this.type = type;
        this.placeId = placeId;
        this.successful = successful;
    }

    public int getType() {
        return type;
    }

    public long getPlaceId() {
        return placeId;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
