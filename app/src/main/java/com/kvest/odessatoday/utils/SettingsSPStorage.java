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
    private static final String KEY_COMMENT_AUTHOR_NAME = "com.kvest.odessatoday.SettingsSPStorage.COMMENT_AUTHOR_NAME";
    private static final String KEY_CURRENT_THEME = "com.kvest.odessatoday.SettingsSPStorage.CURRENT_THEME";
    private static final String KEY_USER_NAME = "com.kvest.odessatoday.SettingsSPStorage.USER_NAME";
    private static final String KEY_PHONE = "com.kvest.odessatoday.SettingsSPStorage.PHONE";


    public static String getCommentAuthorName(Context context) {
        return getStringValue(context, KEY_COMMENT_AUTHOR_NAME, "");
    }

    public static void setCommentAuthorName(Context context, String value) {
        setStringValue(context, KEY_COMMENT_AUTHOR_NAME, value);
    }

    public static int getCurrentTheme(Context context) {
        SharedPreferences pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        return pref.getInt(KEY_CURRENT_THEME, Constants.ThemeType.DAY);
    }

    public static void setCurrentTheme(Context context, int value) {
        SharedPreferences pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        try {
            editor.putInt(KEY_CURRENT_THEME, value);
        } finally {
            editor.commit();
        }
    }

    public static String getUserName(Context context) {
        return getStringValue(context, KEY_USER_NAME, "");
    }

    public static void setUserName(Context context, String value) {
        setStringValue(context, KEY_USER_NAME, value);
    }

    public static String getPhone(Context context) {
        return getStringValue(context, KEY_PHONE, "");
    }

    public static void setPhone(Context context, String value) {
        setStringValue(context, KEY_PHONE, value);
    }

    private static String getStringValue(Context context, String key, String defaultValue) {
        SharedPreferences pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        return pref.getString(key, defaultValue);
    }

    private static void setStringValue(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        try {
            editor.putString(key, value);
        } finally {
            editor.commit();
        }
    }
}
