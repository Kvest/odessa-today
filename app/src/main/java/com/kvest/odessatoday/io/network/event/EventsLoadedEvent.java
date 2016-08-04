package com.kvest.odessatoday.io.network.event;

/**
 * Created by kvest on 10.12.15.
 */
public class EventsLoadedEvent {
    private int type = -1;
    private long placeId = -1;
    private boolean successful;
    private int eventsCount;

    public EventsLoadedEvent(int type, long placeId, boolean successful, int eventsCount) {
        this.type = type;
        this.placeId = placeId;
        this.successful = successful;
        this.eventsCount = eventsCount;
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

    public int getEventsCount() {
        return eventsCount;
    }
}
