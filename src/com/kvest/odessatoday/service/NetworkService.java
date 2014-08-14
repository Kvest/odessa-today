package com.kvest.odessatoday.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.notification.LoadCinemasNotification;
import com.kvest.odessatoday.io.notification.LoadCommentsNotification;
import com.kvest.odessatoday.io.notification.LoadFilmsNotification;
import com.kvest.odessatoday.io.notification.LoadTimetableNotification;
import com.kvest.odessatoday.io.request.*;
import com.kvest.odessatoday.io.response.GetCinemasResponse;
import com.kvest.odessatoday.io.response.GetCommentsResponse;
import com.kvest.odessatoday.io.response.GetFilmsResponse;
import com.kvest.odessatoday.io.response.GetTimetableResponse;
import com.kvest.odessatoday.utils.Constants;

import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 04.06.14
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
public class NetworkService extends IntentService {
    private static final String ACTION_EXTRA = "com.kvest.odessatoday.EXTRAS.ACTION";
    private static final String START_DATE_EXTRA = "com.kvest.odessatoday.EXTRAS.START_DATE";
    private static final String END_DATE_EXTRA = "com.kvest.odessatoday.EXTRAS.END_DATE";
    private static final String FILM_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.FILM_ID";
    private static final String CINEMA_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.CINEMA_ID";
    private static final String COMMENT_RECORD_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.COMMENT_RECORD_ID";
    private static final int ACTION_LOAD_FILMS = 0;
    private static final int ACTION_LOAD_CINEMAS = 1;
    private static final int ACTION_LOAD_TIMETABLE = 2;
    private static final int ACTION_LOAD_FILM_COMMENTS = 3;
    private static final int ACTION_LOAD_CINEMA_COMMENTS = 4;
    private static final int ACTION_UPLOAD_COMMENT = 5;

    public static void loadFilms(Context context, long startDate, long endDate) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_LOAD_FILMS);
        intent.putExtra(START_DATE_EXTRA, startDate);
        intent.putExtra(END_DATE_EXTRA, endDate);

        context.startService(intent);
    }

    public static void loadCinemas(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_LOAD_CINEMAS);

        context.startService(intent);
    }

    public static void loadTimetable(Context context, long filmId) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_LOAD_TIMETABLE);
        intent.putExtra(FILM_ID_EXTRA, filmId);

        context.startService(intent);
    }

    public static void loadFilmComments(Context context, long filmId) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_LOAD_FILM_COMMENTS);
        intent.putExtra(FILM_ID_EXTRA, filmId);

        context.startService(intent);
    }

    public static void loadCinemaComments(Context context, long cinemaId) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_LOAD_CINEMA_COMMENTS);
        intent.putExtra(CINEMA_ID_EXTRA, cinemaId);

        context.startService(intent);
    }

    public static void uploadComment(Context context, long recordId) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_UPLOAD_COMMENT);
        intent.putExtra(COMMENT_RECORD_ID_EXTRA, recordId);

        context.startService(intent);
    }

    public NetworkService() {
        super("NetworkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getIntExtra(ACTION_EXTRA, -1)) {
            case ACTION_LOAD_FILMS :
                doLoadFilms(intent);
                break;
            case ACTION_LOAD_CINEMAS :
                doLoadCinemas(intent);
                break;
            case ACTION_LOAD_TIMETABLE :
                doLoadTimetable(intent);
                break;
            case ACTION_LOAD_FILM_COMMENTS :
                doLoadFilmComments(intent);
                break;
            case ACTION_LOAD_CINEMA_COMMENTS :
                doLoadCinemaComments(intent);
                break;
            case ACTION_UPLOAD_COMMENT :
                doUploadComment(intent);
                break;
        }
    }

    private void doUploadComment(Intent intent) {
        //get extra data
        long recordId = intent.getLongExtra(COMMENT_RECORD_ID_EXTRA, -1);

        Log.d("KVEST_TAG", "recordId=" + recordId);
    }

    private void doLoadCinemaComments(Intent intent) {
        //get extra data
        long cinemaId = intent.getLongExtra(CINEMA_ID_EXTRA, -1);

        RequestFuture<GetCommentsResponse> future = RequestFuture.newFuture();
        GetCinemaCommentsRequest request = new GetCinemaCommentsRequest(cinemaId, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetCommentsResponse response = future.get();
            if (response.isSuccessful()) {
                //notify listeners about successful loading comments
                sendLocalBroadcast(LoadCommentsNotification.createSuccessResult(cinemaId, Constants.CommentTargetType.CINEMA));
            } else {
                Log.e(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading comments
                sendLocalBroadcast(LoadCommentsNotification.createErrorsResult(response.error, cinemaId, Constants.CommentTargetType.CINEMA));
            }
        } catch (InterruptedException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            sendLocalBroadcast(LoadCommentsNotification.createErrorsResult(e.getLocalizedMessage(), cinemaId, Constants.CommentTargetType.CINEMA));
        } catch (ExecutionException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            sendLocalBroadcast(LoadCommentsNotification.createErrorsResult(e.getLocalizedMessage(), cinemaId, Constants.CommentTargetType.CINEMA));
        }

    }

    private void doLoadFilmComments(Intent intent) {
        //get extra data
        long filmId = intent.getLongExtra(FILM_ID_EXTRA, -1);

        RequestFuture<GetCommentsResponse> future = RequestFuture.newFuture();
        GetFilmCommentsRequest request = new GetFilmCommentsRequest(filmId, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetCommentsResponse response = future.get();
            if (response.isSuccessful()) {
                //notify listeners about successful loading comments
                sendLocalBroadcast(LoadCommentsNotification.createSuccessResult(filmId, Constants.CommentTargetType.FILM));
            } else {
                Log.e(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading comments
                sendLocalBroadcast(LoadCommentsNotification.createErrorsResult(response.error, filmId, Constants.CommentTargetType.FILM));
            }
        } catch (InterruptedException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            sendLocalBroadcast(LoadCommentsNotification.createErrorsResult(e.getLocalizedMessage(), filmId, Constants.CommentTargetType.FILM));
        } catch (ExecutionException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            sendLocalBroadcast(LoadCommentsNotification.createErrorsResult(e.getLocalizedMessage(), filmId, Constants.CommentTargetType.FILM));
        }
    }

    private void doLoadTimetable(Intent  intent) {
        //get extra data
        long filmId = intent.getLongExtra(FILM_ID_EXTRA, -1);

        RequestFuture<GetTimetableResponse> future = RequestFuture.newFuture();
        GetTimetableRequest request = new GetTimetableRequest(filmId, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetTimetableResponse response = future.get();
            if (response.isSuccessful()) {
                //notify listeners about successful loading timetable
                sendLocalBroadcast(LoadTimetableNotification.createSuccessResult());
            } else {
                Log.e(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading timetable
                sendLocalBroadcast(LoadTimetableNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading timetable
            sendLocalBroadcast(LoadTimetableNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading timetable
            sendLocalBroadcast(LoadTimetableNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }

    private void doLoadCinemas(Intent  intent) {
        RequestFuture<GetCinemasResponse> future = RequestFuture.newFuture();
        GetCinemasRequest request = new GetCinemasRequest(future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetCinemasResponse response = future.get();
            if (response.isSuccessful()) {
                //notify listeners about successful loading cinemas
                sendLocalBroadcast(LoadCinemasNotification.createSuccessResult());
            } else {
                Log.e(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading cinemas
                sendLocalBroadcast(LoadCinemasNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading cinemas
            sendLocalBroadcast(LoadCinemasNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading cinemas
            sendLocalBroadcast(LoadCinemasNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }

    private void doLoadFilms(Intent intent) {
        //get extra data
        long startDate = intent.getLongExtra(START_DATE_EXTRA, -1);
        long endDate = intent.getLongExtra(END_DATE_EXTRA, -1);

        //send request
        RequestFuture<GetFilmsResponse> future = RequestFuture.newFuture();
        GetFilmsRequest request = new GetFilmsRequest(startDate, endDate, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetFilmsResponse response = future.get();
            if (response.isSuccessful()) {
                //notify listeners about successful loading films
                sendLocalBroadcast(LoadFilmsNotification.createSuccessResult());

                //update cinemas
                NetworkService.loadCinemas(this);
            } else {
                Log.e(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading films
                sendLocalBroadcast(LoadFilmsNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading films
            sendLocalBroadcast(LoadFilmsNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading films
            sendLocalBroadcast(LoadFilmsNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }

    private void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(NetworkService.this).sendBroadcast(intent);
    }
}
