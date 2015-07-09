package com.kvest.odessatoday.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kvest on 19.04.2015.
 */
public class AnnouncementFilm extends Film {
    @SerializedName("premiere_date")
    public long premiere_date;
}
