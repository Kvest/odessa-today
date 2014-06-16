package com.kvest.odessatoday.io;

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

    public static final String API_KEY_HEADER_NAME = "api-key";

    public interface FilmsRequest {
        Uri url = Uri.parse(BASE_URL + FILMS_PATH);

        public interface Params {
            String START_DATE = "start_date";
            String END_DATE = "end_date";
        }
    }

    public interface CinemasRequest {
        Uri url = Uri.parse(BASE_URL + CINEMAS_PATH);
    }
}
