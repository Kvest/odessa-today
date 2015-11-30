package com.kvest.odessatoday.io.network;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 23:08
 * To change this template use File | Settings | File Templates.
 */
public class NetworkContract {
    private static final String BASE_URL = "http://today.od.ua/api/1.0/";
    private static final String FILMS_PATH = "films";
    private static final String CINEMAS_PATH = "cinemas";
    private static final String TIMETABLE_PATH = "timetable";
    private static final String COMMENTS_PATH = "comments";
    private static final String ANNOUNCEMENTS_PATH = "announcement";
    private static final String PLACES_PATH = "places";
    private static final String EVENTS_PATH = "events";

    public static final String API_KEY_HEADER_NAME = "api-key";

    public interface FilmsRequest {
        Uri url = Uri.parse(BASE_URL + FILMS_PATH);

        public interface Params {
            String START_DATE = "start_date";
            String END_DATE = "end_date";
            String CINEMA_ID = "cinema_id";
        }
    }

    public interface AnnouncementRequest {
        Uri url = Uri.parse(BASE_URL + FILMS_PATH + "/" + ANNOUNCEMENTS_PATH);

        public interface Params {
            String OFFSET = "offset";
            String LIMIT = "limit";
            String ORDER = "order";
        }

        public static final String ORDER_ASC = "asc";
        public static final String ORDER_DESC = "desc";
    }

    public interface CinemasRequest {
        Uri url = Uri.parse(BASE_URL + CINEMAS_PATH);
    }

    public static Uri createTimetableUri(long filmId) {
        return Uri.parse(BASE_URL + FILMS_PATH + "/" + Long.toString(filmId) + "/" + TIMETABLE_PATH);
    }

    public static Uri createFilmCommentsUri(long filmId) {
        return Uri.parse(BASE_URL + FILMS_PATH + "/" + Long.toString(filmId) + "/" + COMMENTS_PATH);
    }

    public interface FilmCommentsRequest {
        public int DEFAULT_OFFSET = 0;
        public int MAX_LIMIT = 100;

        public interface Params {
            String OFFSET = "offset";
            String LIMIT = "limit";
        }
    }

    public static Uri createCinemaCommentsUri(long cinemaId) {
        return Uri.parse(BASE_URL + CINEMAS_PATH + "/" + Long.toString(cinemaId) + "/" + COMMENTS_PATH);
    }

    public interface CinemaCommentsRequest {
        public int DEFAULT_OFFSET = 0;
        public int MAX_LIMIT = 100;

        public interface Params {
            String OFFSET = "offset";
            String LIMIT = "limit";
        }
    }

    public interface PlacesRequest {
        Uri url = Uri.parse(BASE_URL + PLACES_PATH);

        public int DEFAULT_OFFSET = 0;
        public int MAX_LIMIT = 100;

        public interface Params {
            String TYPE = "type";
            String OFFSET = "offset";
            String LIMIT = "limit";
        }
    }

    public interface EventsRequest {
        Uri url = Uri.parse(BASE_URL + EVENTS_PATH);

        interface Params {
            String START_DATE = "start_date";
            String END_DATE = "end_date";
            String PLACE_ID = "place_id";
            String TYPE = "type";
        }
    }
}
