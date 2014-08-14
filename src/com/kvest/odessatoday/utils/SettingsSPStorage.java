package com.kvest.odessatoday.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * User: roman
 * Date: 8/14/14
 * Time: 12:58 PM
 */
public abstract class SettingsSPStorage {
    private static final String STORAGE_NAME = "com.kvest.odessatoday.SettingsSPStorage.SETTINGS";
    private static final String COMMENT_AUTHOR_NAME = "com.kvest.odessatoday.SettingsSPStorage.COMMENT_AUTHOR_NAME";

    public static String getCommentAuthorName(Context context) {
        SharedPreferences pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        return pref.getString(COMMENT_AUTHOR_NAME, "");
    }

    public static void setCommentAuthorName(Context context, String value) {
        SharedPreferences pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        try {
            editor.putString(COMMENT_AUTHOR_NAME, value);
        } finally {
            editor.commit();
        }
    }
}
