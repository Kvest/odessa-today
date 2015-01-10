package com.kvest.odessatoday.io.network.handler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import com.android.volley.toolbox.RequestFuture;
import com.kvest.odessatoday.TodayApplication;
import com.kvest.odessatoday.io.network.request.AddCommentRequest;
import com.kvest.odessatoday.io.network.response.AddCommentResponse;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.Constants;

import java.util.concurrent.ExecutionException;

import static com.kvest.odessatoday.utils.LogUtils.LOGE;

/**
 * Created by Kvest on 10.01.2015.
 */
public abstract class CommentHandler extends RequestHandler {
    protected static final String[] ADD_COMMENTS_PROJECTION = new String[]{TodayProviderContract.Tables.Comments.Columns._ID, TodayProviderContract.Tables.Comments.Columns.NAME,
                                        TodayProviderContract.Tables.Comments.Columns.TEXT, TodayProviderContract.Tables.Comments.Columns.TARGET_ID,
                                        TodayProviderContract.Tables.Comments.Columns.TARGET_TYPE};

    protected void syncComment(ContentResolver contentResolver, long targetId, int targetType,
                                AddCommentRequest.Comment comment, Uri commentUri) {
        RequestFuture<AddCommentResponse> future = RequestFuture.newFuture();
        AddCommentRequest request = new AddCommentRequest(targetId, targetType, comment, future, future);

        TodayApplication.getApplication().getVolleyHelper().addRequest(request);
        try {
            AddCommentResponse response = future.get();
            if (response.isSuccessful()) {
                //update record
                ContentValues cv = response.data.getContentValues(targetId, targetType);
                cv.put(TodayProviderContract.Tables.Comments.Columns.SYNC_STATUS, Constants.SyncStatus.UP_TO_DATE);
                contentResolver.update(commentUri, cv, null, null);
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
