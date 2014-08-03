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

    public interface Premiere {
        int NOT_PREMIERE = 0;
        int IS_PREMIERE = 0;
    }

    public interface CommentTargetType {
        int UNKNOWN = -1;
        int FILM = 0;
        int CINEMA = 1;
    }
}
