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
    public static final String TAG = "odessa-today";

    public interface ThemeType {
        int DAY = 0;
        int NIGHT = 1;
    }

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

    public interface SyncStatus {
        int UP_TO_DATE = 0;
        int NEED_UPLOAD = 1;
        int NEED_UPDATE = 2;
        int NEED_DELETE = 4;
    }

    public interface PlaceType {
        int THEATRE= 1;
        int CONCERT_HALL = 2;
        int CLUB = 3;
        int MUSEUM = 4;
        int GALLERY = 5;
        int ZOO = 6;
        int QUEST = 7;
        int RESTAURANT = 8;
        int CAFE = 9;
        int PIZZA = 10;
        int SUSHI = 11;
        int KARAOKE = 12;
        int SKATING_RINK = 13;
        int BOWLING = 14;
        int BILLIARD = 15;
        int SAUNA = 16;
        int BATH = 17;
    }

    public interface EventType {
        int CONCERT = 1;
        int PARTY = 2;
        int SPECTACLE = 3;
        int EXHIBITION = 4;
        int SPORT = 5;
        int WORKSHOP = 6;
    }
}
