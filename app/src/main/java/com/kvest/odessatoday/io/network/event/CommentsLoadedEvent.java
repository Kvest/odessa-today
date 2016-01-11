package com.kvest.odessatoday.io.network.event;

/**
 * Created by kvest on 02.01.16.
 */
public class CommentsLoadedEvent {
    private long targetId;
    private int targetType;
    private boolean successful;
    private boolean hasMoreComments;
    private String errorMessage;

    public CommentsLoadedEvent(long targetId, int targetType, boolean hasMoreComments) {
        this.successful = true;
        this.errorMessage = null;
        this.targetId = targetId;
        this.targetType = targetType;
        this.hasMoreComments = hasMoreComments;
    }

    public CommentsLoadedEvent(long targetId, int targetType, String errorMessage) {
        this.successful = false;
        this.errorMessage = errorMessage;
        this.targetId = targetId;
        this.targetType = targetType;
        this.hasMoreComments = true;
    }

    public long getTargetId() {
        return targetId;
    }

    public int getTargetType() {
        return targetType;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasMoreComments() {
        return hasMoreComments;
    }
}
