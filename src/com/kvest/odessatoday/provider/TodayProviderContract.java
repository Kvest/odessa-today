package com.kvest.odessatoday.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import com.kvest.odessatoday.utils.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.05.14
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
public class TodayProviderContract {
    public static final String CONTENT_AUTHORITY = "com.kvest.odessatoday";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String FILMS_PATH = "films";
    static final String TIMETABLE_PATH = "timetable";
    static final String FULL_TIMETABLE_PATH = "full_timetable";
    static final String CINEMAS_PATH = "cinemas";
    static final String COMMENTS_PATH = "comments";

    public static final Uri FILMS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, FILMS_PATH);
    public static final Uri TIMETABLE_URI = Uri.withAppendedPath(BASE_CONTENT_URI, FILMS_PATH + "/" + TIMETABLE_PATH);
    public static final Uri FULL_TIMETABLE_URI = Uri.withAppendedPath(BASE_CONTENT_URI, FILMS_PATH + "/" + FULL_TIMETABLE_PATH);
    public static final Uri CINEMAS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CINEMAS_PATH);
    public static final Uri COMMENTS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, COMMENTS_PATH);

    public interface Tables {
        interface Films {
            String TABLE_NAME = "films";

            interface Columns extends BaseColumns {
                String FILM_ID = "film_id";
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
                String IS_PREMIERE = "is_premiere";
                String FILM_DURATION = "film_duration";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.FILM_ID + " INTEGER,"
                    + Columns.NAME + " TEXT,"
                    + Columns.COUNTRY + " TEXT,"
                    + Columns.YEAR + " INTEGER,"
                    + Columns.DIRECTOR + " TEXT, "
                    + Columns.ACTORS + " TEXT, "
                    + Columns.DESCRIPTION + " TEXT, "
                    + Columns.IMAGE + " TEXT, "
                    + Columns.VIDEO + " TEXT, "
                    + Columns.GENRE + " TEXT, "
                    + Columns.RATING + " REAL, "
                    + Columns.COMMENTS_COUNT + " INTEGER DEFAULT 0, "
                    + Columns.IS_PREMIERE + " INTEGER DEFAULT 0, "
                    + Columns.FILM_DURATION + " INTEGER DEFAULT 0, "
                    + "UNIQUE (" + Columns.FILM_ID + ") ON CONFLICT REPLACE)";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        interface FilmsFullTimetable {
            String TABLE_NAME = FilmsTimetable.TABLE_NAME + " LEFT OUTER JOIN " + Cinemas.TABLE_NAME + " ON " +
                                FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns.CINEMA_ID + "=" +
                                Cinemas.TABLE_NAME + "." + Cinemas.Columns.CINEMA_ID;

            interface Columns extends BaseColumns {
                String _ID = FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns._ID;
                String TIMETABLE_ID = FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns.TIMETABLE_ID;
                String FILM_ID = FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns.FILM_ID;
                String CINEMA_ID = FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns.CINEMA_ID;
                String DATE = FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns.DATE;
                String PRICES = FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns.PRICES;
                String FORMAT = FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns.FORMAT;
                String CINEMA_NAME = Cinemas.TABLE_NAME + "." + Cinemas.Columns.NAME;
                String CINEMA_ADDRESS = Cinemas.TABLE_NAME + "." + Cinemas.Columns.ADDRESS;
                String CINEMA_PHONES = Cinemas.TABLE_NAME + "." + Cinemas.Columns.PHONES;
                String CINEMA_TRANSPORT = Cinemas.TABLE_NAME + "." + Cinemas.Columns.TRANSPORT;
                String CINEMA_DESCRIPTION = Cinemas.TABLE_NAME + "." + Cinemas.Columns.DESCRIPTION;
                String CINEMA_WORK_TIME = Cinemas.TABLE_NAME + "." + Cinemas.Columns.WORK_TIME;
                String CINEMA_IMAGE = Cinemas.TABLE_NAME + "." + Cinemas.Columns.IMAGE;
            }

            String TIMETABLE_ORDER_ASC = FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns.DATE + " ASC";
        }

        interface FilmsTimetable {
            String TABLE_NAME = "films_timetable";

            interface Columns extends BaseColumns {
                String TIMETABLE_ID = "timetable_id";
                String FILM_ID = "film_id";
                String CINEMA_ID = "cinema_id";
                String DATE = "date";
                String PRICES = "prices";
                String FORMAT = "format";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.TIMETABLE_ID + " INTEGER,"
                    + Columns.FILM_ID + " INTEGER,"
                    + Columns.CINEMA_ID + " INTEGER,"
                    + Columns.DATE + " INTEGER,"
                    + Columns.PRICES + " TEXT,"
                    + Columns.FORMAT + " INTEGER, "
                    + "UNIQUE(" + Columns.TIMETABLE_ID + ") ON CONFLICT REPLACE);";
//                    + "FOREIGN KEY(" + Columns.FILM_ID + ") REFERENCES " + Films.TABLE_NAME + "(" + Films.Columns.FILM_ID + ") ON UPDATE NO ACTION ON DELETE CASCADE);";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
            String GET_FILMS_ID_BY_PERIOD_SQL = "SELECT DISTINCT " + Columns.FILM_ID + " FROM " + TABLE_NAME +
                                                " WHERE " + Columns.DATE + ">=? AND " + Columns.DATE + "<=?";
        }

        interface Comments {
            String TABLE_NAME = "comments";
            interface Columns extends BaseColumns {
                String TARGET_ID = "target_id";
                String TARGET_TYPE = "target_type";
                String COMMENT_ID = "comment_id";
                String DATE = "date";
                String NAME = "name";
                String TEXT = "text";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.TARGET_ID + " INTEGER,"
                    + Columns.TARGET_TYPE + " INTEGER DEFAULT " + Constants.CommentTargetType.UNKNOWN + ","
                    + Columns.COMMENT_ID + " INTEGER,"
                    + Columns.DATE + " INTEGER,"
                    + Columns.NAME + " TEXT,"
                    + Columns.TEXT + " TEXT,"
                    + "UNIQUE(" + Columns.COMMENT_ID + ") ON CONFLICT REPLACE);";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        interface Cinemas {
            String TABLE_NAME = "cinemas";
            interface Columns extends BaseColumns {
                String CINEMA_ID = "cinema_id";
                String NAME = "name";
                String ADDRESS = "address";
                String PHONES = "phones";
                String TRANSPORT = "transport";
                String DESCRIPTION = "description";
                String WORK_TIME = "worktime";
                String IMAGE = "image";
                String COMMENTS_COUNT = "comments_count";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.CINEMA_ID + " INTEGER,"
                    + Columns.NAME + " TEXT,"
                    + Columns.ADDRESS + " TEXT,"
                    + Columns.PHONES + " TEXT,"
                    + Columns.TRANSPORT + " TEXT,"
                    + Columns.DESCRIPTION + " TEXT,"
                    + Columns.WORK_TIME + " TEXT,"
                    + Columns.IMAGE + " TEXT,"
                    + Columns.COMMENTS_COUNT + " INTEGER DEFAULT 0, "
                    + "UNIQUE(" + Columns.CINEMA_ID + ") ON CONFLICT REPLACE);";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }
}
