package com.kvest.odessatoday.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 06.09.14
 * Time: 22:46
 * To change this template use File | Settings | File Templates.
 */
public class KeyboardUtils {
    /**
     * Hide keyboard
     */
    public static void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Show keyboard
     */
    public static void showKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, 0);
        }
    }
}
