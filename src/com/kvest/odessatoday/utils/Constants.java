package com.kvest.odessatoday.utils;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 29.05.14
 * Time: 21:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class Constants {
    public static final String BUGSENS_API_KEY = "ae43aa1c";
    public static final String NETWORK_API_KEY = "key4debug";
    public static final String TAG = "odessa-today";
    public static final String ROBOTO_LIGHT_FONT_NAME = "sans-serif-light";

    public interface Premiere {
        int NOT_PREMIERE = 0;
        int IS_PREMIERE = 1;
    }

    public interface CommentTargetType {
        int UNKNOWN = -1;
        int FILM = 0;
        int CINEMA = 1;
    }

    public interface FilmFormat {
        int UNKNOWN = 0;
        int THIRTY_FIFE_MM = 1;
        int TWO_D = 2;
        int THREE_D = 3;
        int IMAX_THREE_D = 4;
        int IMAX = 5;
        int FIVE_D = 6;
        int FOUR_DX = 7;
    }
}
