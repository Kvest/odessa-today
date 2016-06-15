package com.kvest.odessatoday.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roman on 5/17/16.
 */
public class Sector {
    @SerializedName("id")
    public long id;
    @SerializedName("name")
    public String name;
    @SerializedName("price")
    public String price;
}
