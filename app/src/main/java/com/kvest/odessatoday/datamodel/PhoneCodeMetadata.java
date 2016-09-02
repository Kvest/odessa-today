package com.kvest.odessatoday.datamodel;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by kvest on 01.09.16.
 */
public class PhoneCodeMetadata {
    public static final int ID_UKRAINE = 1;
    public static final int ID_RUSSIA = 2;
    public static final int ID_BELARUS = 3;
    public static final int ID_ARMENIA = 4;
    public static final int ID_AZERBAIJAN = 5;
    public static final int ID_KAZAKHSTAN = 6;
    public static final int ID_KYRGYZSTAN = 7;
    public static final int ID_MOLDOVA = 8;
    public static final int ID_TAJIKISTAN = 9;
    public static final int ID_TURKMENISTAN = 10;
    public static final int ID_UZBEKISTAN = 11;

    public int id;
    @StringRes
    public int countryNameRes;
    @DrawableRes
    public int flagRes;
    public String code;

    public PhoneCodeMetadata(int id,@StringRes int countryNameRes,
                             @DrawableRes int flagRes, String code) {
        this.id = id;
        this.countryNameRes = countryNameRes;
        this.flagRes = flagRes;
        this.code = code;
    }
}
