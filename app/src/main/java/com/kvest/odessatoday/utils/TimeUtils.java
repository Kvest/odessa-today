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
public class TimeUtils {
    /**
     * Method returns end for the date in seconds
     * @param date Date in seconds
     * @return End for the date in seconds
     */
    public static long getEndOfTheDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(date));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        long result = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        return result;
    }

    /**
     * Method returns beginning for the date in seconds
     * @param date Date in seconds
     * @return Beginning for the date in seconds
     */
    public static long getBeginningOfTheDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(date));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long result = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        return result;
    }

    /**
     * Method checks is the date current day
     * @param date Date in seconds
     * @return True if date is current date, false otherwise
     */
    public static boolean isCurrentDay(long date) {
        return getBeginningOfTheDay(date) == getBeginningOfTheDay(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
    }
}
