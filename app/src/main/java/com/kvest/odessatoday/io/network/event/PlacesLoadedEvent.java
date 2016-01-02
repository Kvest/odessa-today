package com.kvest.odessatoday.io.network.event;

/**
 * Created by kvest on 02.01.16.
 */
public class PlacesLoadedEvent {
    private int placeType;
    private boolean successful;
    private String errorMessage;

    public PlacesLoadedEvent(int placeType, boolean successful, String errorMessage) {
        this.placeType = placeType;
        this.successful = successful;
        this.errorMessage = errorMessage;
    }

    public int getPlaceType() {
        return placeType;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
