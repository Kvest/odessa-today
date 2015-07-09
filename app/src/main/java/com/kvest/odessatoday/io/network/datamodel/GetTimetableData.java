package com.kvest.odessatoday.io.network.datamodel;

import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.datamodel.TimetableItem;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 23.07.14
 * Time: 23:22
 * To change this template use File | Settings | File Templates.
 */
public class GetTimetableData {
    @SerializedName("timetable")
    public List<TimetableItem> timetable;
}
