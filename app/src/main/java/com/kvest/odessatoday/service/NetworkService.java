package com.kvest.odessatoday.service;

import android.app.IntentService;
import android.content.*;
import com.kvest.odessatoday.io.network.handler.*;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;
import static com.kvest.odessatoday.utils.LogUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 04.06.14
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
public class NetworkService extends IntentService {
    private static final String ACTION_EXTRA = "com.kvest.odessatoday.EXTRAS.ACTION";
    private static final int ACTION_LOAD_FILMS = 0;
    private static final int ACTION_LOAD_CINEMAS = 1;
    private static final int ACTION_LOAD_TIMETABLE = 2;
    private static final int ACTION_LOAD_COMMENTS = 3;
    private static final int ACTION_UPLOAD_COMMENT = 4;
    private static final int ACTION_UPLOAD_ALL_COMMENTS = 5;
    private static final int ACTION_LOAD_ANNOUNCEMENTS = 6;
    private static final int ACTION_LOAD_PLACES = 7;
    private static final int ACTION_LOAD_EVENTS = 8;

    public static void loadFilms(Context context, long startDate, long endDate) {
        loadFilms(context, startDate, endDate, -1);
    }

    public static void loadFilms(Context context, long startDate, long endDate, long cinemaId) {
        Intent intent = createIntent(context, ACTION_LOAD_FILMS);
        LoadFilmsHandler.putExtras(intent, startDate, endDate, cinemaId);

        context.startService(intent);
    }

    public static void loadCinemas(Context context) {
        Intent intent = createIntent(context, ACTION_LOAD_CINEMAS);

        context.startService(intent);
    }

    public static void loadTimetable(Context context, long filmId) {
        Intent intent = createIntent(context, ACTION_LOAD_TIMETABLE);
        LoadTimetableHandler.putExtras(intent, filmId);

        context.startService(intent);
    }

    public static void loadComments(Context context, long targetId, int targetType) {
        Intent intent = createIntent(context, ACTION_LOAD_COMMENTS);
        LoadCommentsHandler.putExtras(intent, targetId, targetType);

        context.startService(intent);
    }

    public static void uploadComment(Context context, long recordId) {
        Intent intent = createIntent(context, ACTION_UPLOAD_COMMENT);
        UploadCommentHandler.putExtras(intent, recordId);

        context.startService(intent);
    }

    public static void uploadAllComments(Context context) {
        Intent intent = createIntent(context, ACTION_UPLOAD_ALL_COMMENTS);

        context.startService(intent);
    }

    public static void loadAnnouncements(Context context) {
        Intent intent = createIntent(context, ACTION_LOAD_ANNOUNCEMENTS);

        context.startService(intent);
    }

    public static void loadPlaces(Context context, int placesType) {
        Intent intent = createIntent(context, ACTION_LOAD_PLACES);
        LoadPlacesHandler.putExtras(intent, placesType);

        context.startService(intent);
    }

    public static void sync(Context context) {
        if (Utils.isNetworkAvailable(context)) {
            uploadAllComments(context);
        }
    }

    public static void loadEvents(Context context, long startDate, long endDate, long placeId) {
        Intent intent = createIntent(context, ACTION_LOAD_EVENTS);
        LoadEventsHandler.putExtras(intent, startDate, endDate, placeId);

        context.startService(intent);
    }

    public static void loadEvents(Context context, long startDate, long endDate, int type) {
        Intent intent = createIntent(context, ACTION_LOAD_EVENTS);
        LoadEventsHandler.putExtras(intent, startDate, endDate, type);

        context.startService(intent);
    }

    public NetworkService() {
        super("NetworkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //get handler
        int actionId = intent.getIntExtra(ACTION_EXTRA, -1);
        RequestHandler handler = getHandler(actionId);

        if (handler != null) {
            handler.processIntent(this, intent);
        } else {
            LOGE(Constants.TAG, "RequestHandler did not find for action " + actionId);
        }
    }

    /**
     * Method returns handler by action id
     *
     * @param action ID of the action
     * @return Handler or or <code>null</code> if action is unknown
     */
    private RequestHandler getHandler(int action) {
        switch (action) {
            case ACTION_LOAD_FILMS :
                return new LoadFilmsHandler();
            case ACTION_LOAD_CINEMAS :
                return new LoadCinemasHandler();
            case ACTION_LOAD_TIMETABLE :
                return new LoadTimetableHandler();
            case ACTION_LOAD_COMMENTS :
                return new LoadCommentsHandler();
            case ACTION_UPLOAD_COMMENT :
                return new UploadCommentHandler();
            case ACTION_UPLOAD_ALL_COMMENTS :
                return new UploadAllCommentsHandler();
            case ACTION_LOAD_ANNOUNCEMENTS:
                return new LoadAnnouncementsHandler();
            case ACTION_LOAD_PLACES :
                return new LoadPlacesHandler();
            case ACTION_LOAD_EVENTS :
                return new LoadEventsHandler();
        }

        return null;
    }

    private static Intent createIntent(Context context, int action) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, action);

        return intent;
    }
}
