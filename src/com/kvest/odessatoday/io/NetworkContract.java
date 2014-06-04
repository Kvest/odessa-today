package com.kvest.odessatoday.io;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 03.06.14
 * Time: 23:08
 * To change this template use File | Settings | File Templates.
 */
public class NetworkContract {
    private static final String BASE_URL = "http://todayy.od.ua/api/1.0/";
    private static final String FILMS_PATH = "films";
    private static final String TODAY_PATH = "today";

    public interface FilmsTodayRequest {
        String Url = BASE_URL + "/" + FILMS_PATH + "/" + TODAY_PATH;
    }
}
