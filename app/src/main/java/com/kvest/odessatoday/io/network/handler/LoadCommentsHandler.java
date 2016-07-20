package com.kvest.odessatoday.io.network.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.Comment;
import com.kvest.odessatoday.io.network.event.CommentsLoadedEvent;
import com.kvest.odessatoday.io.network.request.GetCommentsRequest;
import com.kvest.odessatoday.io.network.response.GetCommentsResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.provider.TodayProviderContract.COMMENTS_URI;
import static com.kvest.odessatoday.provider.TodayProviderContract.CONTENT_AUTHORITY;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 10.01.2015.
 */
public class LoadCommentsHandler extends RequestHandler {
    private static final String TARGET_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.TARGET_ID";
    private static final String TARGET_TYPE_EXTRA = "com.kvest.odessatoday.EXTRAS.TARGET_TYPE";
    private static final String LIMIT_EXTRA = "com.kvest.odessatoday.EXTRAS.LIMIT";
    private static final String OFFSET_EXTRA = "com.kvest.odessatoday.EXTRAS.OFFSET";
    private static final String DELETE_COMMENTS_EXTRA = "com.kvest.odessatoday.EXTRAS.DELETE_COMMENTS";

    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 50;

    public static void putExtras(Intent intent, long targetId, int targetType,
                                 int offset, int limit, boolean deleteComments) {
        intent.putExtra(TARGET_ID_EXTRA, targetId);
        intent.putExtra(TARGET_TYPE_EXTRA, targetType);
        intent.putExtra(OFFSET_EXTRA, offset);
        intent.putExtra(LIMIT_EXTRA, limit);
        intent.putExtra(DELETE_COMMENTS_EXTRA, deleteComments);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        boolean deleteOldComments = intent.getBooleanExtra(DELETE_COMMENTS_EXTRA, false);

        RequestFuture<GetCommentsResponse> future = RequestFuture.newFuture();
        GetCommentsRequest request = createRequest(intent, future);
        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            GetCommentsResponse response = future.get();
            if (response.isSuccessful()) {
                //save comments
                saveComments(context, response.data.comments, request.getTargetId(), request.getTargetType(), deleteOldComments);

                boolean hasMoreComments = response.data.comments_remained > 0;
                notifySuccess(context, intent, hasMoreComments);
            } else {
                LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                //notify listeners about unsuccessful loading comments
                notifyError(context, response.error, intent);
            }
        } catch (InterruptedException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            notifyError(context, e.getLocalizedMessage(), intent);
        } catch (ExecutionException e) {
            LOGE(Constants.TAG, e.getLocalizedMessage());

            //notify listeners about unsuccessful loading comments
            notifyError(context, e.getLocalizedMessage(), intent);
        }
    }

    protected void saveComments(Context context, List<Comment> comments, long targetId, int targetType, boolean deleteOldComments) {
        int count = comments != null ? comments.size() : 0;
        ArrayList<ContentProviderOperation> operations = new ArrayList<>(count + (deleteOldComments ? 1 : 0));

        if (deleteOldComments) {
            //delete old comments
            ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(COMMENTS_URI)
                    .withSelection(TodayProviderContract.Tables.Comments.Columns.TARGET_ID + "=? AND " + TodayProviderContract.Tables.Comments.Columns.TARGET_TYPE + "=? AND " +
                                    TodayProviderContract.Tables.Comments.Columns.SYNC_STATUS + "=?",
                            new String[]{Long.toString(targetId), Integer.toString(targetType), Integer.toString(Constants.SyncStatus.UP_TO_DATE)})
                    .build();
            operations.add(deleteOperation);
        }

        //insert comments
        if (comments != null) {
            for (Comment comment : comments) {
                operations.add(ContentProviderOperation.newInsert(COMMENTS_URI)
                        .withValues(comment.getContentValues(targetId, targetType)).build());
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

    private GetCommentsRequest createRequest(Intent intent, RequestFuture<GetCommentsResponse> future) {
        //get extra data
        long targetId = intent.getLongExtra(TARGET_ID_EXTRA, -1);
        int targetType = intent.getIntExtra(TARGET_TYPE_EXTRA, Constants.CommentTargetType.UNKNOWN);
        int offset = intent.getIntExtra(OFFSET_EXTRA, DEFAULT_OFFSET);
        int limit = intent.getIntExtra(LIMIT_EXTRA, DEFAULT_LIMIT);

        GetCommentsRequest request = new GetCommentsRequest(targetId, targetType, offset, limit, future, future);
        return request;
    }

    protected void notifyError(Context context, String message, Intent intent) {
        //get extra data
        long targetId = intent.getLongExtra(TARGET_ID_EXTRA, -1);
        int targetType = intent.getIntExtra(TARGET_TYPE_EXTRA, Constants.CommentTargetType.UNKNOWN);

        BusProvider.getInstance().post(new CommentsLoadedEvent(targetId, targetType, message));
    }

    protected void notifySuccess(Context context, Intent intent, boolean hasMoreComments) {
        //get extra data
        long targetId = intent.getLongExtra(TARGET_ID_EXTRA, -1);
        int targetType = intent.getIntExtra(TARGET_TYPE_EXTRA, Constants.CommentTargetType.UNKNOWN);

        BusProvider.getInstance().post(new CommentsLoadedEvent(targetId, targetType, hasMoreComments));
    }
}
