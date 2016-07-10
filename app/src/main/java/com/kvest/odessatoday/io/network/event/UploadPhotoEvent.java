package com.kvest.odessatoday.io.network.event;

/**
 * Created by roman on 6/24/16.
 */
public class UploadPhotoEvent {
    private boolean successful;
    private String errorMessage;
    private String[] newPhotos;

    public UploadPhotoEvent(boolean successful, String errorMessage) {
        this.successful = successful;
        this.errorMessage = errorMessage;
        this.newPhotos = null;
    }

    public UploadPhotoEvent(String[] newPhotos) {
        this.successful = true;
        this.newPhotos = newPhotos;
        this.errorMessage = null;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String[] getNewPhotos() {
        return newPhotos;
    }
}
