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
    static final String FULL_PATH = "full";
    static final String CINEMA_VIEW_PATH = "cinema_view";
    static final String CINEMAS_PATH = "cinemas";
    static final String COMMENTS_PATH = "comments";
    static final String COMMENTS_RATING_PATH = "comments_rating";
    static final String ANNOUNCEMENTS_PATH = "announcements";
    static final String ANNOUNCEMENT_FILMS_VIEW_PATH = "announcement_films_view";
    static final String PLACES_PATH = "places";
    static final String EVENTS_PATH = "events";
    static final String EVENTS_VIEW_PATH = "events_view";

    public static final Uri FILMS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, FILMS_PATH);
    public static final Uri FILM_TIMETABLE_URI = Uri.withAppendedPath(FILMS_URI, TIMETABLE_PATH);
    public static final Uri FULL_TIMETABLE_URI = Uri.withAppendedPath(FILM_TIMETABLE_URI, FULL_PATH);
    public static final Uri CINEMA_TIMETABLE_URI = Uri.withAppendedPath(FILM_TIMETABLE_URI, CINEMA_VIEW_PATH);
    public static final Uri CINEMAS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CINEMAS_PATH);
    public static final Uri COMMENTS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, COMMENTS_PATH);
    public static final Uri COMMENTS_RATING_URI = Uri.withAppendedPath(BASE_CONTENT_URI, COMMENTS_RATING_PATH);
    public static final Uri ANNOUNCEMENT_FILMS_URI = Uri.withAppendedPath(FILMS_URI, ANNOUNCEMENTS_PATH);
    public static final Uri ANNOUNCEMENT_FILMS_VIEW_URI = Uri.withAppendedPath(ANNOUNCEMENT_FILMS_URI, ANNOUNCEMENT_FILMS_VIEW_PATH);
    public static final Uri PLACES_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PLACES_PATH);
    public static final Uri EVENTS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, EVENTS_PATH);
    public static final Uri EVENTS_TIMETABLE_URI = Uri.withAppendedPath(EVENTS_URI, TIMETABLE_PATH);
    public static final Uri EVENTS_TIMETABLE_VIEW_URI = Uri.withAppendedPath(EVENTS_TIMETABLE_URI, EVENTS_VIEW_PATH);

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
                String RATED = "rated";
                String COMMENTS_COUNT = "comments_count";
                String IS_PREMIERE = "is_premiere";
                String FILM_DURATION = "film_duration";
                String POSTERS = "posters";
                String SHARE_TEXT = "share_text";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
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
                    + Columns.RATED + " INTEGER DEFAULT 0, "
                    + Columns.COMMENTS_COUNT + " INTEGER DEFAULT 0, "
                    + Columns.IS_PREMIERE + " INTEGER DEFAULT 0, "
                    + Columns.FILM_DURATION + " INTEGER DEFAULT 0, "
                    + Columns.POSTERS + " TEXT, "
                    + Columns.SHARE_TEXT + " TEXT DEFAULT \'\', "
                    + "UNIQUE (" + Columns.FILM_ID + ") ON CONFLICT REPLACE)";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        interface FilmsFullTimetableView {
            String VIEW_NAME = "films_full_timetable_view";
            String CREATE_VIEW_SQL = "CREATE VIEW IF NOT EXISTS " + VIEW_NAME + " AS SELECT * FROM " +
                        FilmsTimetable.TABLE_NAME + " LEFT OUTER JOIN " + Cinemas.TABLE_NAME + " ON " +
                        FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns.CINEMA_ID + "=" +
                        Cinemas.TABLE_NAME + "." + Cinemas.Columns.CINEMA_ID;

            interface Columns extends BaseColumns {
                String TIMETABLE_ID = FilmsTimetable.Columns.TIMETABLE_ID;
                String FILM_ID = FilmsTimetable.Columns.FILM_ID;
                String CINEMA_ID = FilmsTimetable.Columns.CINEMA_ID;
                String DATE = FilmsTimetable.Columns.DATE;
                String PRICES = FilmsTimetable.Columns.PRICES;
                String FORMAT = FilmsTimetable.Columns.FORMAT;
                String _ID1 = Cinemas.Columns._ID + ":1";
                String CINEMA_ID1 = Cinemas.Columns.CINEMA_ID + ":1";
                String CINEMA_NAME = Cinemas.Columns.NAME;
                String CINEMA_ADDRESS = Cinemas.Columns.ADDRESS;
                String CINEMA_PHONES = Cinemas.Columns.PHONES;
                String CINEMA_TRANSPORT = Cinemas.Columns.TRANSPORT;
                String CINEMA_DESCRIPTION = Cinemas.Columns.DESCRIPTION;
                String CINEMA_WORK_TIME = Cinemas.Columns.WORK_TIME;
                String CINEMA_IMAGE = Cinemas.Columns.IMAGE;
                String CINEMA_COMMENTS_COUNT = Cinemas.Columns.COMMENTS_COUNT;
            }

            String ORDER_ASC = Columns.DATE + " ASC";
            String ORDER_CINEMA_ASC_DATE_ASC = Columns.CINEMA_ID + " ASC," + Columns.DATE + " ASC";
        }

        interface CinemaTimetableView {
            String VIEW_NAME = "cinema_timetable_view";

            String CREATE_VIEW_SQL = "CREATE VIEW IF NOT EXISTS " + VIEW_NAME + " AS SELECT * FROM " + FilmsTimetable.TABLE_NAME +
                                     " LEFT OUTER JOIN " + Films.TABLE_NAME + " ON " +
                                     FilmsTimetable.TABLE_NAME + "." + FilmsTimetable.Columns.FILM_ID + "=" +
                                     Films.TABLE_NAME + "." + Films.Columns.FILM_ID + ";";

            interface Columns extends BaseColumns {
                String TIMETABLE_ID = FilmsTimetable.Columns.TIMETABLE_ID;
                String FILM_ID = FilmsTimetable.Columns.FILM_ID;
                String CINEMA_ID = FilmsTimetable.Columns.CINEMA_ID;
                String DATE = FilmsTimetable.Columns.DATE;
                String PRICES = FilmsTimetable.Columns.PRICES;
                String FORMAT = FilmsTimetable.Columns.FORMAT;
                String _ID1 = Films.Columns._ID + ":1";
                String FILM_ID1 = Films.Columns.FILM_ID + ":1";
                String NAME = Films.Columns.NAME;
                String COUNTRY = Films.Columns.COUNTRY;
                String YEAR = Films.Columns.YEAR;
                String DIRECTOR = Films.Columns.DIRECTOR;
                String ACTORS = Films.Columns.ACTORS;
                String DESCRIPTION = Films.Columns.DESCRIPTION;
                String IMAGE = Films.Columns.IMAGE;
                String VIDEO = Films.Columns.VIDEO;
                String GENRE = Films.Columns.GENRE;
                String RATING = Films.Columns.RATING;
                String COMMENTS_COUNT = Films.Columns.COMMENTS_COUNT;
                String IS_PREMIERE = Films.Columns.IS_PREMIERE;
                String FILM_DURATION = Films.Columns.FILM_DURATION;
                String POSTERS = Films.Columns.POSTERS;
            }

            String ORDER_ASC = Columns.DATE + " ASC";
            String ORDER_FILM_ASC_DATE_ASC = Columns.FILM_ID + " ASC," + Columns.DATE + " ASC";
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
                    + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.TIMETABLE_ID + " INTEGER,"
                    + Columns.FILM_ID + " INTEGER,"
                    + Columns.CINEMA_ID + " INTEGER,"
                    + Columns.DATE + " INTEGER,"
                    + Columns.PRICES + " TEXT,"
                    + Columns.FORMAT + " INTEGER DEFAULT " + Constants.FilmFormat.UNKNOWN + ", "
                    + "UNIQUE(" + Columns.TIMETABLE_ID + ") ON CONFLICT REPLACE);";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
            String GET_FILMS_ID_BY_PERIOD_SQL = "SELECT DISTINCT " + Columns.FILM_ID + " FROM " + TABLE_NAME +
                                                " WHERE " + Columns.DATE + ">=? AND " + Columns.DATE + "<=?";

            String DISTINCT_FILM_ID = "distinct " + Columns.FILM_ID;
        }

        interface Comments {
            String TABLE_NAME = "comments";
            interface Columns extends BaseColumns {
                String SYNC_STATUS = "sync_status";
                String TARGET_ID = "target_id";
                String TARGET_TYPE = "target_type";
                String COMMENT_ID = "comment_id";
                String DATE = "date";
                String NAME = "name";
                String TEXT = "text";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.SYNC_STATUS + " INTEGER DEFAULT " + Constants.SyncStatus.UP_TO_DATE + ","
                    + Columns.TARGET_ID + " INTEGER,"
                    + Columns.TARGET_TYPE + " INTEGER DEFAULT " + Constants.TargetType.UNKNOWN + ","
                    + Columns.COMMENT_ID + " INTEGER,"
                    + Columns.DATE + " INTEGER,"
                    + Columns.NAME + " TEXT,"
                    + Columns.TEXT + " TEXT,"
                    + "UNIQUE(" + Columns.COMMENT_ID + ") ON CONFLICT REPLACE);";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
            String DATE_ORDER_DESC = Comments.Columns.DATE + " DESC";
            String DATE_ORDER_ASC = Comments.Columns.DATE + " ASC";
        }

        interface CommentsRating {
            String TABLE_NAME = "comments_rating";
            interface Columns extends BaseColumns {
                String COMMENT_ID = "comment_id";
                String TARGET_ID = "target_id";
                String RATING = "rating";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.COMMENT_ID + " INTEGER NOT NULL,"
                    + Columns.TARGET_ID + " INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE,"
                    + Columns.RATING + " REAL)";

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
                String RATING = "rating";
                String LON = "lon";
                String LAT = "lat";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.CINEMA_ID + " INTEGER,"
                    + Columns.NAME + " TEXT,"
                    + Columns.ADDRESS + " TEXT,"
                    + Columns.PHONES + " TEXT,"
                    + Columns.TRANSPORT + " TEXT,"
                    + Columns.DESCRIPTION + " TEXT,"
                    + Columns.WORK_TIME + " TEXT,"
                    + Columns.IMAGE + " TEXT,"
                    + Columns.COMMENTS_COUNT + " INTEGER DEFAULT 0, "
                    + Columns.LON + " REAL,"
                    + Columns.LAT + " REAL,"
                    + Columns.RATING + " REAL,"
                    + "UNIQUE(" + Columns.CINEMA_ID + ") ON CONFLICT REPLACE);";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        interface AnnouncementsMetadata {
            String TABLE_NAME = "announcements_metadata";
            interface Columns extends BaseColumns {
                String FILM_ID = "film_id";
                String PREMIERE_DATE = "premiere_date";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.FILM_ID + " INTEGER,"
                    + Columns.PREMIERE_DATE + " INTEGER,"
                    + "UNIQUE(" + Columns.FILM_ID + ") ON CONFLICT REPLACE);";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        interface AnnouncementFilmsView {
            String VIEW_NAME = "announcement_films_view";

            String CREATE_VIEW_SQL = "CREATE VIEW IF NOT EXISTS " + VIEW_NAME + " AS SELECT * FROM " + AnnouncementsMetadata.TABLE_NAME +
                    " LEFT OUTER JOIN " + Films.TABLE_NAME + " ON " +
                    AnnouncementsMetadata.TABLE_NAME + "." + AnnouncementsMetadata.Columns.FILM_ID + "=" +
                    Films.TABLE_NAME + "." + Films.Columns.FILM_ID + ";";

            interface Columns extends BaseColumns {
                String FILM_ID = Films.Columns.FILM_ID;
                String NAME = Films.Columns.NAME;
                String COUNTRY = Films.Columns.COUNTRY;
                String YEAR = Films.Columns.YEAR;
                String DIRECTOR = Films.Columns.DIRECTOR;
                String ACTORS = Films.Columns.ACTORS;
                String DESCRIPTION = Films.Columns.DESCRIPTION;
                String IMAGE = Films.Columns.IMAGE;
                String VIDEO = Films.Columns.VIDEO;
                String GENRE = Films.Columns.GENRE;
                String RATING = Films.Columns.RATING;
                String COMMENTS_COUNT = Films.Columns.COMMENTS_COUNT;
                String IS_PREMIERE = Films.Columns.IS_PREMIERE;
                String FILM_DURATION = Films.Columns.FILM_DURATION;
                String POSTERS = Films.Columns.POSTERS;
                String _ID1 = AnnouncementsMetadata.Columns._ID + ":1";
                String FILM_ID1 = AnnouncementsMetadata.Columns.FILM_ID + ":1";
                String PREMIERE_DATE = AnnouncementsMetadata.Columns.PREMIERE_DATE;
            }

            String PREMIERE_DATE_ORDER_DESC = AnnouncementFilmsView.Columns.PREMIERE_DATE + " DESC";
            String PREMIERE_DATE_ORDER_ASC = AnnouncementFilmsView.Columns.PREMIERE_DATE + " ASC";
        }

        interface Places {
            String TABLE_NAME = "places";
            interface Columns extends BaseColumns {
                String PLACE_ID = "place_id";
                String PLACE_TYPE = "place_type";
                String NAME = "name";
                String ADDRESS = "address";
                String PHONES = "phones";
                String TRANSPORT = "transport";
                String DESCRIPTION = "description";
                String COMMENTS_COUNT = "comments_count";
                String WORK_TIME = "worktime";
                String IMAGE = "image";
                String LON = "lon";
                String LAT = "lat";
                String RATING = "rating";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.PLACE_ID + " INTEGER,"
                    + Columns.PLACE_TYPE + " INTEGER,"
                    + Columns.NAME + " TEXT,"
                    + Columns.ADDRESS + " TEXT,"
                    + Columns.PHONES + " TEXT,"
                    + Columns.TRANSPORT + " TEXT,"
                    + Columns.DESCRIPTION + " TEXT,"
                    + Columns.WORK_TIME + " TEXT,"
                    + Columns.IMAGE + " TEXT,"
                    + Columns.COMMENTS_COUNT + " INTEGER DEFAULT 0, "
                    + Columns.LON + " REAL,"
                    + Columns.LAT + " REAL,"
                    + Columns.RATING + " REAL,"
                    + "UNIQUE(" + Columns.PLACE_ID + ", " + Columns.PLACE_TYPE + ") ON CONFLICT REPLACE);";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        interface Events {
            String TABLE_NAME = "events";

            interface Columns extends BaseColumns {
                String EVENT_ID = "event_id";
                String EVENT_TYPE = "event_type";
                String IMAGE = "image";
                String VIDEO = "video";
                String NAME = "name";
                String DIRECTOR = "director";
                String ACTORS = "actors";
                String DESCRIPTION = "description";
                String RATING = "rating";
                String RATED = "rated";
                String COMMENTS_COUNT = "comments_count";
                String POSTERS = "posters";
                String SHARE_TEXT = "share_text";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.EVENT_ID + " INTEGER,"
                    + Columns.EVENT_TYPE + " INTEGER,"
                    + Columns.IMAGE + " TEXT, "
                    + Columns.VIDEO + " TEXT, "
                    + Columns.NAME + " TEXT,"
                    + Columns.DIRECTOR + " TEXT, "
                    + Columns.ACTORS + " TEXT, "
                    + Columns.DESCRIPTION + " TEXT, "
                    + Columns.RATING + " REAL, "
                    + Columns.RATED + " INTEGER DEFAULT 0, "
                    + Columns.COMMENTS_COUNT + " INTEGER DEFAULT 0, "
                    + Columns.POSTERS + " TEXT, "
                    + Columns.SHARE_TEXT + " TEXT DEFAULT \'\', "
                    + "UNIQUE (" + Columns.EVENT_ID + ") ON CONFLICT REPLACE)";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
            String GET_EVENTS_ID_BY_TYPE_SQL = "SELECT DISTINCT " + Columns.EVENT_ID + " FROM " + TABLE_NAME +
                                               " WHERE " + Columns.EVENT_TYPE + "=?";
        }

        interface EventsTimetable {
            String TABLE_NAME = "events_timetable";

            interface Columns extends BaseColumns {
                String TIMETABLE_ID = "timetable_id";
                String EVENT_ID = "event_id";
                String PLACE_ID = "place_id";
                String PLACE_NAME = "place_name";
                String DATE = "date";
                String PRICES = "prices";
                String HAS_TICKETS = "has_tickets";
            }

            String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                    + Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + Columns.TIMETABLE_ID + " INTEGER,"
                    + Columns.EVENT_ID + " INTEGER,"
                    + Columns.PLACE_ID + " INTEGER,"
                    + Columns.PLACE_NAME + " TEXT,"
                    + Columns.DATE + " INTEGER,"
                    + Columns.PRICES + " TEXT,"
                    + Columns.HAS_TICKETS + " INTEGER DEFAULT 0, "
                    + "UNIQUE(" + Columns.TIMETABLE_ID + ") ON CONFLICT REPLACE);";

            String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
            String ORDER_DATE_ASC = Columns.DATE + " ASC";

            String DISTINCT_EVENT_ID = "distinct " + Columns.EVENT_ID;
        }

        interface EventsTimetableView {
            String VIEW_NAME = "events_full_timetable_view";
            String CREATE_VIEW_SQL = "CREATE VIEW IF NOT EXISTS " + VIEW_NAME + " AS SELECT * FROM " +
                                     EventsTimetable.TABLE_NAME + " LEFT OUTER JOIN " + Events.TABLE_NAME + " ON " +
                                     EventsTimetable.TABLE_NAME + "." + EventsTimetable.Columns.EVENT_ID + "=" +
                                     Events.TABLE_NAME + "." + Events.Columns.EVENT_ID;

            interface Columns extends BaseColumns {
                String TIMETABLE_ID = EventsTimetable.Columns.TIMETABLE_ID;
                String EVENT_ID = EventsTimetable.Columns.EVENT_ID;
                String PLACE_ID = EventsTimetable.Columns.PLACE_ID;
                String PLACE_NAME = EventsTimetable.Columns.PLACE_NAME;
                String DATE = EventsTimetable.Columns.DATE;
                String PRICES = EventsTimetable.Columns.PRICES;
                String HAS_TICKETS = EventsTimetable.Columns.HAS_TICKETS;
                String _ID1 = Events.Columns._ID + ":1";
                String EVENT_ID1 = Events.Columns.EVENT_ID + ":1";
                String EVENT_TYPE = Events.Columns.EVENT_TYPE;
                String IMAGE = Events.Columns.IMAGE;
                String NAME = Events.Columns.NAME;
                String DIRECTOR = Events.Columns.DIRECTOR;
                String ACTORS = Events.Columns.ACTORS;
                String DESCRIPTION = Events.Columns.DESCRIPTION;
                String RATING = Events.Columns.RATING;
                String COMMENTS_COUNT = Events.Columns.COMMENTS_COUNT;
            }

            String ORDER_ASC = Columns.DATE + " ASC";
            String ORDER_EVENT_ASC_DATE_ASC = Columns.EVENT_ID + " ASC," + Columns.DATE + " ASC";
        }
    }
}
