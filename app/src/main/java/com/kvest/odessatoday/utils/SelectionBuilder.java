package com.kvest.odessatoday.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kvest on 07.02.16.
 */
public class SelectionBuilder {
    private String selection;
    private List<String> selectionArgs;

    public SelectionBuilder() {
        selection = null;
        selectionArgs = new ArrayList<>();
    }

    public void and(String selection, String args) {
        if (this.selection == null) {
            this.selection = selection;
        } else {
            this.selection += " AND " + selection;
        }

        selectionArgs.add(args);
    }

    public void or(String selection, String args) {
        if (this.selection == null) {
            this.selection = selection;
        } else {
            this.selection += " OR " + selection;
        }

        selectionArgs.add(args);
    }

    public String getSelection() {
        return selection;
    }

    public String[] getSelectionArgs() {
        if (selectionArgs.isEmpty()) {
            return null;
        }

        return selectionArgs.toArray(new String[selectionArgs.size()]);
    }
}
