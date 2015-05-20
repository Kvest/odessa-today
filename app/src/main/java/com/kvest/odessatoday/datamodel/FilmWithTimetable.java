package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;
import android.text.TextUtils;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 22:40
 * To change this template use File | Settings | File Templates.
 */
public class FilmWithTimetable extends Film {
    public List<TimetableItem> timetable;
}
