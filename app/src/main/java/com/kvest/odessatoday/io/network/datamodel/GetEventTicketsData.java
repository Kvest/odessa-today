package com.kvest.odessatoday.io.network.datamodel;

import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.datamodel.TicketInfo;

import java.util.List;

/**
 * Created by kvest on 19.08.16.
 */
public class GetEventTicketsData {
    @SerializedName("delivery_str")
    public String deliveryStr;
    @SerializedName("tickets")
    public List<TicketInfo> tickets;
}
