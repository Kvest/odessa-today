package com.kvest.odessatoday.io.network.datamodel;

import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.datamodel.FilmWithTimetable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class GetFilmsData {
    @SerializedName("date")
    public long date;
    @SerializedName("films")
    public List<FilmWithTimetable> films;
}
