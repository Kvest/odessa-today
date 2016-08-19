package com.kvest.odessatoday.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kvest on 19.08.16.
 */
public class TicketInfo {
    @SerializedName("date")
    public long date;
    @SerializedName("sectors")
    public Sector[] sectors;
}
