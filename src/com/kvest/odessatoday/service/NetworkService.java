package com.kvest.odessatoday.service;

import android.app.IntentService;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.*;
import com.kvest.odessatoday.io.notification.*;
import com.kvest.odessatoday.io.request.*;
import com.kvest.odessatoday.io.response.*;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;
import static com.kvest.odessatoday.utils.LogUtils.*;
/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 04.06.14
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
public class NetworkService extends IntentService {
    private static final String[] ADD_COMMENTS_PROJECTION = new String[]{Tables.Comments.Columns._ID, Tables.Comments.Columns.NAME,
                                                                         Tables.Comments.Columns.TEXT, Tables.Comments.Columns.TARGET_ID,
                                                                         Tables.Comments.Columns.TARGET_TYPE};
    private static final int DEFAULT_ANNOUNCEMENTS_LIMIT = 30;
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
    private static final int ACTION_SYNC = 6;
    private static final int ACTION_LOAD_ANNOUNCEMENTS = 7;

    public static void loadFilms(Context context, long startDate, long endDate) {
        loadFilms(context, startDate, endDate, -1);
    }

    public static void loadFilms(Context context, long startDate, long endDate, long cinemaId) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_LOAD_FILMS);
        intent.putExtra(START_DATE_EXTRA, startDate);
        intent.putExtra(END_DATE_EXTRA, endDate);
        intent.putExtra(CINEMA_ID_EXTRA, cinemaId);

        context.startService(intent);
    }

    public static void loadAnnouncements(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_LOAD_ANNOUNCEMENTS);

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

    public static void sync(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(ACTION_EXTRA, ACTION_SYNC);

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
            case ACTION_SYNC :
                doSync(intent);
                break;
            case ACTION_LOAD_ANNOUNCEMENTS :
                doLoadAnnouncements(intent);
                break;
        }
    }

    private void doLoadAnnouncements(Intent intent) {
        int offset = 0;
        int totalCount = 0;
        boolean deletePreviousAnnouncement = true;
        boolean loadedWithoutErrors = true;
        do {
            RequestFuture<GetAnnouncementsResponse> future = RequestFuture.newFuture();
            GetAnnouncementsRequest request = new GetAnnouncementsRequest(offset, DEFAULT_ANNOUNCEMENTS_LIMIT, future, future);
            TodayApplication.getApplication().getVolleyHelper().addRequest(request);
            try {
                GetAnnouncementsResponse response = future.get();
                if (response.isSuccessful()) {
                    //TODO test this loop
//                    LOGD("KVEST_TAG", "count=" + response.data.films.size());
//                    for (Film film : response.data.films) {
//                        LOGD("KVEST_TAG", film.filmname + "[" + film.id + "]");
//                    }

                    saveAnnouncementFilms(this, response.data.films, deletePreviousAnnouncement);

                    //recalculate values
                    deletePreviousAnnouncement = false;
                    offset += response.data.films.size();
                    totalCount = response.data.total_count;
                } else {
                    LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                    loadedWithoutErrors = false;
                    //notify listeners about unsuccessful loading cinemas
                    sendLocalBroadcast(LoadAnnouncementFilmsNotification.createErrorsResult(response.error));

                    break;
                }
            } catch (InterruptedException e) {
                LOGE(Constants.TAG, e.getLocalizedMessage());

                loadedWithoutErrors = false;
                //notify listeners about unsuccessful loading cinemas
                sendLocalBroadcast(LoadAnnouncementFilmsNotification.createErrorsResult(e.getLocalizedMessage()));

                break;
            } catch (ExecutionException e) {
                LOGE(Constants.TAG, e.getLocalizedMessage());

                loadedWithoutErrors = false;
                //notify listeners about unsuccessful loading cinemas
                sendLocalBroadcast(LoadAnnouncementFilmsNotification.createErrorsResult(e.getLocalizedMessage()));

                break;
            }
        } while (offset < totalCount);

        if (loadedWithoutErrors) {
            //notify listeners about successful loading cinemas
            sendLocalBroadcast(LoadAnnouncementFilmsNotification.createSuccessResult());
        }
    }

    private void doSync(Intent intent) {
        if (Utils.isNetworkAvailable(this)) {
            syncComments();
        }
    }

    private void doUploadComment(Intent intent) {
        //get extra data
        long recordId = intent.getLongExtra(COMMENT_RECORD_ID_EXTRA, -1);
        Uri commentUri = Uri.withAppendedPath(TodayProviderContract.COMMENTS_URI, Long.toString(recordId));

        //get comment and target id and type
        long targetId = -1;
        int targetType = -1;
        AddCommentRequest.Comment comment = new AddCommentRequest.Comment();
        comment.device_id = Utils.getDeviceId(getApplicationContext());

        Cursor cursor = getContentResolver().query(commentUri, ADD_COMMENTS_PROJECTION, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                comment.name = cursor.getString(cursor.getColumnIndex(Tables.Comments.Columns.NAME));
                comment.text = cursor.getString(cursor.getColumnIndex(Tables.Comments.Columns.TEXT));
                targetId = cursor.getLong(cursor.getColumnIndex(Tables.Comments.Columns.TARGET_ID));
                targetType = cursor.getInt(cursor.getColumnIndex(Tables.Comments.Columns.TARGET_TYPE));
            }
        } finally {
            cursor.close();
        }

        syncComment(targetId, targetType, comment, commentUri);
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
                //save comments
                saveComments(this, response.data.comments, request.getTargetId(), request.getTargetType());

                //notify listeners about successful loading comments
                sendLocalBroadcast(LoadCommentsNotification.createSuccessResult(cinemaId, Constants.CommentTargetType.CINEMA));
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading comments
                sendLocalBroadcast(LoadCommentsNotification.createErrorsResult(response.error, cinemaId, Constants.CommentTargetType.CINEMA));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            sendLocalBroadcast(LoadCommentsNotification.createErrorsResult(e.getLocalizedMessage(), cinemaId, Constants.CommentTargetType.CINEMA));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

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
                //save comments
                saveComments(this, response.data.comments, request.getTargetId(), request.getTargetType());

                //notify listeners about successful loading comments
                sendLocalBroadcast(LoadCommentsNotification.createSuccessResult(filmId, Constants.CommentTargetType.FILM));
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading comments
                sendLocalBroadcast(LoadCommentsNotification.createErrorsResult(response.error, filmId, Constants.CommentTargetType.FILM));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            sendLocalBroadcast(LoadCommentsNotification.createErrorsResult(e.getLocalizedMessage(), filmId, Constants.CommentTargetType.FILM));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

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
                //save timetable
                saveTimetable(this, response.data.timetable, request.getFilmId());

                //notify listeners about successful loading timetable
                sendLocalBroadcast(LoadTimetableNotification.createSuccessResult());
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading timetable
                sendLocalBroadcast(LoadTimetableNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading timetable
            sendLocalBroadcast(LoadTimetableNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

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
                //save cinemas
                saveCinemas(this, response.data.cinemas);

                //notify listeners about successful loading cinemas
                sendLocalBroadcast(LoadCinemasNotification.createSuccessResult());
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading cinemas
                sendLocalBroadcast(LoadCinemasNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading cinemas
            sendLocalBroadcast(LoadCinemasNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading cinemas
            sendLocalBroadcast(LoadCinemasNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }

    private void doLoadFilms(Intent intent) {
        //get extra data
        long startDate = intent.getLongExtra(START_DATE_EXTRA, -1);
        long endDate = intent.getLongExtra(END_DATE_EXTRA, -1);
        long cinemaId = intent.getLongExtra(CINEMA_ID_EXTRA, -1);

        //send request
        RequestFuture<GetFilmsResponse> future = RequestFuture.newFuture();
        GetFilmsRequest request = new GetFilmsRequest(startDate, endDate, cinemaId, future, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetFilmsResponse response = future.get();
            if (response.isSuccessful()) {
                //save films
                saveFilms(this, response.data.films, request.getStartDate(), request.getEndDate());

                //notify listeners about successful loading films
                sendLocalBroadcast(LoadFilmsNotification.createSuccessResult());

                //update cinemas
                NetworkService.loadCinemas(this);
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading films
                sendLocalBroadcast(LoadFilmsNotification.createErrorsResult(response.error));
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading films
            sendLocalBroadcast(LoadFilmsNotification.createErrorsResult(e.getLocalizedMessage()));
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading films
            sendLocalBroadcast(LoadFilmsNotification.createErrorsResult(e.getLocalizedMessage()));
        }
    }

    private void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(NetworkService.this).sendBroadcast(intent);
    }

    private void saveCinemas(Context context, List<Cinema> cinemas) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(cinemas.size() + 1);

        //delete cinemas
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(CINEMAS_URI).build();
        operations.add(deleteOperation);

        for (Cinema cinema : cinemas) {
            //insert film
            operations.add(ContentProviderOperation.newInsert(CINEMAS_URI).withValues(cinema.getContentValues()).build());
        }

        //apply
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
            re.printStackTrace();
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
            oae.printStackTrace();
        }
    }

    private void saveFilms(Context context, List<FilmWithTimetable> films, long startDate, long endDate) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        //delete timetable from startDate to endDate
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(TIMETABLE_URI)
                .withSelection(Tables.FilmsTimetable.Columns.DATE + ">=? AND " + Tables.FilmsTimetable.Columns.DATE + "<=?",
                        new String[]{Long.toString(startDate), Long.toString(endDate)})
                .build();
        operations.add(deleteOperation);

        for (FilmWithTimetable film : films) {
            //insert film
            operations.add(ContentProviderOperation.newInsert(FILMS_URI).withValues(film.getContentValues()).build());

            //insert timetable
            for (TimetableItem timetableItem : film.timetable) {
                operations.add(ContentProviderOperation.newInsert(TIMETABLE_URI).withValues(timetableItem.getContentValues(film.id)).build());
            }
        }

        //apply
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
            re.printStackTrace();
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
            oae.printStackTrace();
        }
    }

    private void saveAnnouncementFilms(Context context, List<Film> films, boolean deletePreviousAnnouncement) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(films.size() * 2 + (deletePreviousAnnouncement ? 1 : 0));

        if (deletePreviousAnnouncement) {
            ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(ANNOUNCEMENT_FILMS_URI).build();
            operations.add(deleteOperation);
        }

        for (Film film : films) {
            //insert film
            operations.add(ContentProviderOperation.newInsert(FILMS_URI).withValues(film.getContentValues()).build());

            //insert film id to metadata
            ContentValues cv = new ContentValues(1);
            cv.put(Tables.AnnouncementsMetadata.Columns.FILM_ID, film.id);
            operations.add(ContentProviderOperation.newInsert(ANNOUNCEMENT_FILMS_URI).withValues(cv).build());
        }

        //apply
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
            re.printStackTrace();
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
            oae.printStackTrace();
        }
    }

    private void saveTimetable(Context context, List<TimetableItem> timetable, long filmId) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        //delete timetable for film with filmId
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(TIMETABLE_URI)
                .withSelection(Tables.FilmsTimetable.Columns.FILM_ID + "=?", new String[]{Long.toString(filmId)})
                .build();
        operations.add(deleteOperation);

        //insert timetable items
        for (TimetableItem timetableItem : timetable) {
            operations.add(ContentProviderOperation.newInsert(TIMETABLE_URI).withValues(timetableItem.getContentValues(filmId)).build());
        }

        //apply
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
            re.printStackTrace();
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
            oae.printStackTrace();
        }
    }

    private void saveComments(Context context, List<Comment> comments, long targetId, int targetType) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(comments.size() + 1);

        //delete old comments
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(COMMENTS_URI)
                .withSelection(Tables.Comments.Columns.TARGET_ID + "=? AND " + Tables.Comments.Columns.TARGET_TYPE + "=? AND " +
                               Tables.Comments.Columns.SYNC_STATUS + "=?",
                               new String[]{Long.toString(targetId), Integer.toString(targetType),
                                            Integer.toString(Constants.SyncStatus.UP_TO_DATE)})
                .build();
        operations.add(deleteOperation);

        //insert comments
        for (Comment comment : comments) {
            operations.add(ContentProviderOperation.newInsert(COMMENTS_URI)
                    .withValues(comment.getContentValues(targetId, targetType)).build());
        }

        //apply
        try {
            context.getContentResolver().applyBatch(CONTENT_AUTHORITY, operations);
        }catch (RemoteException re) {
            LOGE(Constants.TAG, re.getMessage());
            re.printStackTrace();
        }catch (OperationApplicationException oae) {
            LOGE(Constants.TAG, oae.getMessage());
            oae.printStackTrace();
        }
    }

    private void syncComments() {
        String selection = "(" + Tables.Comments.Columns.SYNC_STATUS + " & " + Constants.SyncStatus.NEED_UPLOAD + " = " + Constants.SyncStatus.NEED_UPLOAD + ")";
        Cursor cursor = getContentResolver().query(TodayProviderContract.COMMENTS_URI, ADD_COMMENTS_PROJECTION, selection,
                                                   null, Tables.Comments.DATE_ORDER_ASC);
        try {
            //create comment object
            AddCommentRequest.Comment comment = new AddCommentRequest.Comment();
            comment.device_id = Utils.getDeviceId(this);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                //collect data
                comment.name = cursor.getString(cursor.getColumnIndex(Tables.Comments.Columns.NAME));
                comment.text = cursor.getString(cursor.getColumnIndex(Tables.Comments.Columns.TEXT));
                long recordId = cursor.getLong(cursor.getColumnIndex(Tables.Comments.Columns._ID));
                long targetId = cursor.getLong(cursor.getColumnIndex(Tables.Comments.Columns.TARGET_ID));
                int targetType = cursor.getInt(cursor.getColumnIndex(Tables.Comments.Columns.TARGET_TYPE));

                //sync
                syncComment(targetId, targetType, comment, Uri.withAppendedPath(TodayProviderContract.COMMENTS_URI, Long.toString(recordId)));

                //go to next comment
                cursor.moveToNext();

                //make artificial delay to keep comments order
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                } catch (InterruptedException ie) {}
            }
        } finally {
            cursor.close();
        }
    }

    private void syncComment(long targetId, int targetType, AddCommentRequest.Comment comment, Uri commentUri) {
        RequestFuture<AddCommentResponse> future = RequestFuture.newFuture();
        AddCommentRequest request = new AddCommentRequest(targetId, targetType, comment, future, future);

        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            AddCommentResponse response = future.get();
            if (response.isSuccessful()) {
                //update record
                ContentValues cv = response.data.getContentValues(targetId, targetType);
                cv.put(Tables.Comments.Columns.SYNC_STATUS, Constants.SyncStatus.UP_TO_DATE);
                getContentResolver().update(commentUri, cv, null, null);
            } else {
                //TODO
                //react right to the code
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());
        }
    }
}
