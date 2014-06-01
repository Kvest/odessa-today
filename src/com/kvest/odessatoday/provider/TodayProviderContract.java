package com.kvest.odessatoday.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.05.14
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
public class TodayProviderContract {
    public static final String CONTENT_AUTHORITY = "com.kvest.odessatoday";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String FILMS_PATH = "films";

    public interface Tables {
        interface Films {
            String TABLE_NAME = "films";

            interface Columns extends BaseColumns {
                String FILM_ID = "film_id";
                String DATE = "date";
                String NAME = "name";
                String COUNTRY = "country";
                String YEAR = "year";
                String DIRECTOR = "director";
                String ACTORS = "actors";
                String DESCRIPTION = "description";
                String IMAGE = "image";
                String VIDEO = "video";
                String GENRE = "genre";
                String RATING = "rating";
                String COMMENTS_COUNT = "comments_count";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.FILM_ID + " INTEGER,"
                    + Columns.DATE + " INTEGER,"
                    + Columns.NAME + " TEXT,"
                    + Columns.COUNTRY + " TEXT,"
                    + Columns.YEAR + " INTEGER,"
                    + Columns.DIRECTOR + " TEXT, "
                    + Columns.ACTORS + " TEXT, "
                    + Columns.DESCRIPTION + " TEXT, "
                    + Columns.IMAGE + " TEXT, "
                    + Columns.VIDEO + " TEXT, "
                    + Columns.GENRE + " TEXT, "
                    + Columns.RATING + " INTEGER, "
                    + Columns.COMMENTS_COUNT + " INTEGER, "
                    + "UNIQUE (" + Columns.FILM_ID + ") ON CONFLICT REPLACE)";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }
}
