package com.kvest.odessatoday.io.network.datamodel;

import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.datamodel.AnnouncementFilm;
import com.kvest.odessatoday.datamodel.Film;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.14
 * Time: 11:55
 * To change this template use File | Settings | File Templates.
 */
public class GetAnnouncementsData {
    @SerializedName("total_count")
    public int total_count;
    @SerializedName("films")
    public List<AnnouncementFilm> films;
}
