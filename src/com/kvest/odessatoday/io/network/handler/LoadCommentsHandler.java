package com.kvest.odessatoday.io.network.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import com.kvest.odessatoday.datamodel.Comment;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.kvest.odessatoday.provider.TodayProviderContract.COMMENTS_URI;
import static com.kvest.odessatoday.provider.TodayProviderContract.CONTENT_AUTHORITY;
import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 10.01.2015.
 */
public abstract class LoadCommentsHandler extends RequestHandler {
    protected void saveComments(Context context, List<Comment> comments, long targetId, int targetType) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(comments.size() + 1);

        //delete old comments
        ContentProviderOperation deleteOperation = ContentProviderOperation.newDelete(COMMENTS_URI)
                .withSelection(TodayProviderContract.Tables.Comments.Columns.TARGET_ID + "=? AND " + TodayProviderContract.Tables.Comments.Columns.TARGET_TYPE + "=? AND " +
                                TodayProviderContract.Tables.Comments.Columns.SYNC_STATUS + "=?",
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
}
