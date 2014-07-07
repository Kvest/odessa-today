package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;
import com.kvest.odessatoday.provider.TodayProviderContract;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 16.06.14
 * Time: 22:26
 * To change this template use File | Settings | File Templates.
 */
public class Cinema {
    public long id;
    public String name;
    public String address;
    public String phones;
    public String transport;
    public String description;
    public String worktime;
    public String image;

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues(8);
        values.put(TodayProviderContract.Tables.Cinemas.Columns.CINEMA_ID, id);
        values.put(TodayProviderContract.Tables.Cinemas.Columns.NAME, name);
        values.put(TodayProviderContract.Tables.Cinemas.Columns.ADDRESS, address);
        values.put(TodayProviderContract.Tables.Cinemas.Columns.PHONES, phones);
        values.put(TodayProviderContract.Tables.Cinemas.Columns.TRANSPORT, transport);
        values.put(TodayProviderContract.Tables.Cinemas.Columns.DESCRIPTION, description);
        values.put(TodayProviderContract.Tables.Cinemas.Columns.WORK_TIME, worktime);
        values.put(TodayProviderContract.Tables.Cinemas.Columns.IMAGE, image);

        return values;
    }
}
