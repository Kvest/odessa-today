package com.kvest.odessatoday.io.network.event;

/**
 * Created by kvest on 02.01.16.
 */
public class AnnouncementFilmsLoadedEvent {
    private boolean successful;
    private String errorMessage;

    public AnnouncementFilmsLoadedEvent(boolean successful, String errorMessage) {
        this.successful = successful;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
