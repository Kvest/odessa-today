package com.kvest.odessatoday.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kvest on 20.03.2015.
 */
public class FontUtils {
    public static final String HELVETICANEUECYR_BLACK_FONT = "fonts/helveticaneuecyr-black.ttf";
    public static final String HELVETICANEUECYR_BOLD_FONT = "fonts/helveticaneuecyr-bold.ttf";
    public static final String HELVETICANEUECYR_LIGHT_FONT = "fonts/helveticaneuecyr-light.ttf";
    public static final String HELVETICANEUECYR_ROMAN_FONT = "fonts/helveticaneuecyr-roman.ttf";
    public static final String HELVETICANEUECYR_THIN_FONT = "fonts/helveticaneuecyr-thin.ttf";
    public static final String HELVETICANEUECYR_ULTRALIGHT_FONT = "fonts/helveticaneuecyr-ultralight.ttf";
    public static final String HELV_5_NORMAL_ITALIC_FONT = "fonts/helv-5-normal-italic.ttf";
    public static final Map<String, Typeface> fonts = new HashMap<String, Typeface>();

    public static final Typeface getFont(AssetManager assetManager, String fileName) {
        if (!fonts.containsKey(fileName)) {
            fonts.put(fileName, Typeface.createFromAsset(assetManager, fileName));
        }

        return fonts.get(fileName);
    }
}
