package com.kvest.odessatoday.utils;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 15.06.14
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class Utils {
    private static final long SECONDS_IN_DAY = TimeUnit.HOURS.toSeconds(24);

    public static long getEndOfTheDay(long date) {


        return (date + (SECONDS_IN_DAY - date % SECONDS_IN_DAY) - 1);
    }

    public static long toLocalDate(long date) {
        //get timezone
        TimeZone timeZone = TimeZone.getDefault();

        return date  + TimeUnit.MILLISECONDS.toSeconds(timeZone.getOffset(date));
    }

    public static long toUtcDate(long date) {
        //get timezone
        TimeZone timeZone = TimeZone.getDefault();

        return date  - TimeUnit.MILLISECONDS.toSeconds(timeZone.getOffset(date));
    }
}
