package com.kvest.odessatoday.io.network.datamodel;

import com.google.gson.annotations.SerializedName;
import com.kvest.odessatoday.datamodel.Comment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 29.07.14
 * Time: 0:06
 * To change this template use File | Settings | File Templates.
 */
public class GetCommentsData {
    @SerializedName("comments")
    public List<Comment> comments;
    @SerializedName("comments_remained")
    public int comments_remained;
}
