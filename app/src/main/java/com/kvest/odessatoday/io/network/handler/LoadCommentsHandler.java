package com.kvest.odessatoday.io.network.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.datamodel.Comment;
import com.kvest.odessatoday.io.network.request.GetCommentsRequest;
import com.kvest.odessatoday.io.network.response.GetCommentsResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
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
public abstract class LoadCommentsHandler extends RequestHandler {
    private static final int DEFAULT_OFFSET = 0;
    private static final int DEFAULT_LIMIT = 50;

    @Override
    public void processIntent(Context context, Intent intent) {
        boolean hasMoreComments = true;
        boolean deleteOldComments = true;
        int offset = DEFAULT_OFFSET;
        int limit = DEFAULT_LIMIT;

        while (hasMoreComments) {
            hasMoreComments = false;

            RequestFuture<GetCommentsResponse> future = RequestFuture.newFuture();
            GetCommentsRequest request = createRequest(intent, offset, limit, future);
            TodayApplication.getApplication().getVolleyHelper().addRequest(request);
            try {
                GetCommentsResponse response = future.get();
                if (response.isSuccessful()) {
                    //save comments
                    saveComments(context, response.data.comments, request.getTargetId(), request.getTargetType(), deleteOldComments);
                    deleteOldComments = false;

                    if (response.data.comments_remained > 0) {
                        hasMoreComments = true;
                        offset += response.data.comments.size();
                    } else {
                        notifySuccess(context, intent);
                    }
                } else {
                    LOGE(Constants.TAG, "ERROR " + response.code + " = " + response.error);

                    //notify listeners about unsuccessful loading comments
                    notifyError(context, response.error, intent);

                    break;
                }
            } catch (InterruptedException e) {
                LOGE(Constants.TAG, e.getLocalizedMessage());

                //notify listeners about unsuccessful loading comments
                notifyError(context, e.getLocalizedMessage(), intent);

                break;
            } catch (ExecutionException e) {
                LOGE(Constants.TAG, e.getLocalizedMessage());

                //notify listeners about unsuccessful loading comments
                notifyError(context, e.getLocalizedMessage(), intent);

                break;
            }
        }
    }

    protected void saveComments(Context context, List<Comment> comments, long targetId, int targetType, boolean deleteOldComments) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(comments.size() + (deleteOldComments ? 1 : 0));

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

    protected abstract GetCommentsRequest createRequest(Intent intent, int offset, int limit, RequestFuture<GetCommentsResponse> future);
    protected abstract void notifyError(Context context, String message, Intent intent);
    protected abstract void notifySuccess(Context context, Intent intent);
}
