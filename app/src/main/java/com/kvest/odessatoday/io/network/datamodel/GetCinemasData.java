package com.kvest.odessatoday.io.network.datamodel;

import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.datamodel.Cinema;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 16.06.14
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
public class GetCinemasData {
    @SerializedName("cinemas")
    public List<Cinema> cinemas;
}
