package com.kvest.odessatoday.ui.widget;

/**
 * Created by kvest on 9/5/16.
 */
class Range {
    private int start;
    private int end;

    Range() {
        start = -1;
        end = -1;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
