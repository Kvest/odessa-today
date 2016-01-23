package com.kvest.odessatoday.io.network.handler;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.network.NetworkContract;
import com.kvest.odessatoday.io.network.request.AddCommentRequest;
import com.kvest.odessatoday.io.network.response.AddCommentResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.CINEMAS_URI;
import static com.kvest.odessatoday.provider.TodayProviderContract.CONTENT_AUTHORITY;
import static com.kvest.odessatoday.provider.TodayProviderContract.EVENTS_URI;
import static com.kvest.odessatoday.provider.TodayProviderContract.FILMS_URI;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;
import static com.kvest.odessatoday.provider.TodayProviderContract.Tables.*;

/**
 * Created by Kvest on 10.01.2015.
 */
public class UploadCommentsHandler extends RequestHandler {
    private static final String EXTRA_COMMENT_RECORD_ID = "com.kvest.odessatoday.EXTRAS.COMMENT_RECORD_ID";
    private static final String[] ADD_COMMENTS_PROJECTION = new String[]{Comments.Columns._ID, Comments.Columns.NAME,
                                                                         Comments.Columns.TEXT, Comments.Columns.TARGET_ID,
                                                                         Comments.Columns.TARGET_TYPE};

    public static void putExtras(Intent intent, long recordId) {
        intent.putExtra(EXTRA_COMMENT_RECORD_ID, recordId);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        long commentRecordId = intent.getLongExtra(EXTRA_COMMENT_RECORD_ID, -1);

        Cursor cursor;
        ContentResolver contentResolver = context.getContentResolver();
        if (commentRecordId == -1) {
            //sync all comments
            String selection = "(" + Comments.Columns.SYNC_STATUS + " & " + Constants.SyncStatus.NEED_UPLOAD + " = " + Constants.SyncStatus.NEED_UPLOAD + ")";
            cursor = contentResolver.query(TodayProviderContract.COMMENTS_URI, ADD_COMMENTS_PROJECTION, selection,
                    null, Comments.DATE_ORDER_ASC);
        } else {
            //sync particular comment
            Uri commentUri = Uri.withAppendedPath(TodayProviderContract.COMMENTS_URI, Long.toString(commentRecordId));
            cursor = contentResolver.query(commentUri, ADD_COMMENTS_PROJECTION, null, null, null);
        }

        if (cursor == null) {
            return;
        }

        try {
            //create comment object
            AddCommentRequest.Comment comment = new AddCommentRequest.Comment();
            long recordId;
            long targetId;
            int targetType;

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                //collect data
                comment.name = cursor.getString(cursor.getColumnIndex(Comments.Columns.NAME));
                comment.text = cursor.getString(cursor.getColumnIndex(Comments.Columns.TEXT));
                recordId = cursor.getLong(cursor.getColumnIndex(Comments.Columns._ID));
                targetId = cursor.getLong(cursor.getColumnIndex(Comments.Columns.TARGET_ID));
                targetType = cursor.getInt(cursor.getColumnIndex(Comments.Columns.TARGET_TYPE));

                //get rating
                RatingMetadata ratingMetadata = retrieveRating(contentResolver, recordId);
                if (ratingMetadata != null) {
                    comment.rating = ratingMetadata.rating;
                }

                //sync
                syncComment(contentResolver, targetId, targetType, comment, ratingMetadata,
                            Uri.withAppendedPath(TodayProviderContract.COMMENTS_URI,
                            Long.toString(recordId)));

                //go to next comment
                cursor.moveToNext();

                //make artificial delay to keep comments order
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                } catch (InterruptedException ie) {
                }
            }
        } finally {
            cursor.close();
        }
    }

    private RatingMetadata retrieveRating(ContentResolver contentResolver, long commentRecordId) {
        RatingMetadata result = null;

        Cursor cursor = contentResolver.query(TodayProviderContract.COMMENTS_RATING_URI, null,
                                              CommentsRating.Columns.COMMENT_ID + "=?",
                                              new String[]{Long.toString(commentRecordId)}, null);
        try {
            if (cursor.moveToFirst()) {
                result = new RatingMetadata();
                result.rating = cursor.getFloat(cursor.getColumnIndex(CommentsRating.Columns.RATING));
                result.itemUri = Uri.withAppendedPath(TodayProviderContract.COMMENTS_RATING_URI,
                                                      Long.toString(cursor.getLong(cursor.getColumnIndex(CommentsRating.Columns._ID))));
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    private void syncComment(ContentResolver contentResolver, long targetId, int targetType,
                             AddCommentRequest.Comment comment, RatingMetadata ratingMetadata,
                             Uri commentUri) {
        RequestFuture<AddCommentResponse> future = RequestFuture.newFuture();
        AddCommentRequest request = new AddCommentRequest(targetId, targetType, comment, future, future);

        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            AddCommentResponse response = future.get();
            if (response.isSuccessful()) {
                //update records in the storage
                ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(ratingMetadata != null ? 3 : 1);

                //update comment
                ContentValues cv = response.data.getContentValues(targetId, targetType);
                cv.put(Comments.Columns.SYNC_STATUS, Constants.SyncStatus.UP_TO_DATE);
                operations.add(ContentProviderOperation.newUpdate(commentUri).withValues(cv).build());

                //delete rating record and mark target item as rated
                if (ratingMetadata != null) {
                    operations.add(ContentProviderOperation.newDelete(ratingMetadata.itemUri).build());

                    ContentProviderOperation updateRatedOperation = createUpdateRatedOperation(targetId, targetType);
                    if (updateRatedOperation != null) {
                        operations.add(updateRatedOperation);
                    }
                }

                //apply
                try {
                    contentResolver.applyBatch(CONTENT_AUTHORITY, operations);
                }catch (RemoteException re) {
                    LOGE(Constants.TAG, re.getMessage());
                }catch (OperationApplicationException oae) {
                    LOGE(Constants.TAG, oae.getMessage());
                }
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

    private ContentProviderOperation createUpdateRatedOperation(long targetId, int targetType) {
        switch (Utils.CommentTargetType2Group(targetType)) {
            case Constants.CommentTargetTypeGroup.FILM :
                return ContentProviderOperation.newUpdate(FILMS_URI)
                        .withSelection(Films.Columns.FILM_ID + "=?", new String[]{Long.toString(targetId)})
                        .withValue(Films.Columns.RATED, 1).build();
            case Constants.CommentTargetTypeGroup.EVENT :
                return ContentProviderOperation.newUpdate(EVENTS_URI)
                        .withSelection(Events.Columns.EVENT_ID + "=?", new String[]{Long.toString(targetId)})
                        .withValue(Events.Columns.RATED, 1).build();
            default:
                return null;
        }
    }

    private static class RatingMetadata {
        private Float rating;
        private Uri itemUri;
    }
}
