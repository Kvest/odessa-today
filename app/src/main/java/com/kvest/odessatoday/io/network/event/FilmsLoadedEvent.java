package com.kvest.odessatoday.io.network.event;

/**
 * Created by kvest on 02.01.16.
 */
public class FilmsLoadedEvent {
    private boolean successful;
    private String errorMessage;

    public final long startDate;
    public final long endDate;
    public final int filmsCount;

    public FilmsLoadedEvent(boolean successful, long startDate, long endDate, int filmsCount) {
        this.successful = successful;
        this.startDate = startDate;
        this.endDate = endDate;
        this.filmsCount = filmsCount;
    }

    public FilmsLoadedEvent(boolean successful, String errorMessage, long startDate,
                            long endDate, int filmsCount) {
        this.successful = successful;
        this.errorMessage = errorMessage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.filmsCount = filmsCount;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
