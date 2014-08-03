package com.kvest.odessatoday.provider;

import android.content.Context;
import android.content.CursorLoader;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 31.07.14
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class CursorLoaderBuilder {
    public static CursorLoader getFilmsFullTimetable(Context context, long filmId, String[] projection, String order) {
        String selection = Tables.FilmsFullTimetable.Columns.FILM_ID + "=?";
        return new CursorLoader(context, FULL_TIMETABLE_URI, projection,
                                selection, new String[]{Long.toString(filmId)}, order);
    }
}
