package com.kvest.odessatoday.io.network.datamodel;

import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.datamodel.Place;

import java.util.List;

/**
 * Created by kvest on 16.09.15.
 */
public class GetPlacesData {
    @SerializedName("type")
    public int type;

    @SerializedName("places")
    public List<Place> places;

    @SerializedName("places_remained")
    public int placesRemained;

}
