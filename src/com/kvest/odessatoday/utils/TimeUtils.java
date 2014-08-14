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
     * Method returns end for the date in the local date-time
     * @param date Local date-time
     * @return End for the date in the local date-time
     */
    public static long getEndOfTheDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(toUtcDate(date)));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        long result = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        result = toLocalDate(result);
        return result;
    }

    /**
     * Method returns beginning for the date in the local date-time
     * @param date Local date-time
     * @return Beginning for the date in the local date-time
     */
    public static long getBeginningOfTheDay(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(toUtcDate(date)));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long result = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        result = toLocalDate(result);
        return result;
    }

    /**
     * Convert UTC date to local
     * @param date UTC date in seconds
     * @return Local date
     */
    public static long toLocalDate(long date) {
        //get timezone
        TimeZone timeZone = TimeZone.getDefault();

        return date + TimeUnit.MILLISECONDS.toSeconds(timeZone.getOffset(date));
    }

    /**
     * Convert local date to UTC
     * @param date Local date in seconds
     * @return UTC date
     */
    public static long toUtcDate(long date) {
        //get timezone
        TimeZone timeZone = TimeZone.getDefault();

        return date - TimeUnit.MILLISECONDS.toSeconds(timeZone.getOffset(date));
    }

    /**
     * Method checks is the date current day
     * @param date Local date-time
     * @return True if date is current date, false otherwise
     */
    public static boolean isCurrentDay(long date) {
        //get start of the current date in seconds
        long currentDate = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        currentDate = getBeginningOfTheDay(toLocalDate(currentDate));

        return (date >= currentDate && date < (currentDate + TimeUnit.DAYS.toSeconds(1)));
    }
}
