package com.kvest.odessatoday.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kvest on 20.03.2015.
 */
public class FontUtils {
    public static final String VERANDA_REGULAR_FONT = "fonts/veranda_regular.ttf";
    public static final Map<String, Typeface> fonts = new HashMap<String, Typeface>();

    public static final Typeface getFont(AssetManager assetManager, String fileName) {
        if (!fonts.containsKey(fileName)) {
            fonts.put(fileName, Typeface.createFromAsset(assetManager, fileName));
        }

        return fonts.get(fileName);
    }
}
