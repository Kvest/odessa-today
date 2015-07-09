package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;
/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 29.07.14
 * Time: 0:07
 * To change this template use File | Settings | File Templates.
 */
public class Comment {
    @SerializedName("id")
    public long id;
    @SerializedName("date")
    public long date;
    @SerializedName("name")
    public String name;
    @SerializedName("text")
    public String text;

    public ContentValues getContentValues(long targetId, int targetType) {
        ContentValues values = new ContentValues(6);
        values.put(Comments.Columns.TARGET_ID, targetId);
        values.put(Comments.Columns.TARGET_TYPE, targetType);
        values.put(Comments.Columns.COMMENT_ID, id);
        values.put(Comments.Columns.DATE, date);
        values.put(Comments.Columns.NAME, name);
        values.put(Comments.Columns.TEXT, text);

        return values;
    }
}
