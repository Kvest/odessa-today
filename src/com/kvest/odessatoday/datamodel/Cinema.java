package com.kvest.odessatoday.datamodel;

import android.content.ContentValues;
import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

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
    public int comments_count;

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues(8);
        values.put(Cinemas.Columns.CINEMA_ID, id);
        values.put(Cinemas.Columns.NAME, name);
        values.put(Cinemas.Columns.ADDRESS, address);
        values.put(Cinemas.Columns.PHONES, phones);
        values.put(Cinemas.Columns.TRANSPORT, transport);
        values.put(Cinemas.Columns.DESCRIPTION, description);
        values.put(Cinemas.Columns.WORK_TIME, worktime);
        values.put(Cinemas.Columns.IMAGE, image);
        values.put(Cinemas.Columns.COMMENTS_COUNT, comments_count);

        return values;
    }
}
