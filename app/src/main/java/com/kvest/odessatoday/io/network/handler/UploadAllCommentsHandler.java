package com.kvest.odessatoday.io.network.handler;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import com.kvest.odessatoday.io.network.request.AddCommentRequest;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.Utils;

import java.util.concurrent.TimeUnit;

/**
 * Created by Kvest on 10.01.2015.
 */
public class UploadAllCommentsHandler extends CommentHandler {
    @Override
    public void processIntent(Context context, Intent intent) {
        String selection = "(" + TodayProviderContract.Tables.Comments.Columns.SYNC_STATUS + " & " + Constants.SyncStatus.NEED_UPLOAD + " = " + Constants.SyncStatus.NEED_UPLOAD + ")";
        Cursor cursor = context.getContentResolver().query(TodayProviderContract.COMMENTS_URI, ADD_COMMENTS_PROJECTION, selection,
                                                           null, TodayProviderContract.Tables.Comments.DATE_ORDER_ASC);
        try {
            //create comment object
            AddCommentRequest.Comment comment = new AddCommentRequest.Comment();

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                //collect data
                comment.name = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Comments.Columns.NAME));
                comment.text = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Comments.Columns.TEXT));
                long recordId = cursor.getLong(cursor.getColumnIndex(TodayProviderContract.Tables.Comments.Columns._ID));
                long targetId = cursor.getLong(cursor.getColumnIndex(TodayProviderContract.Tables.Comments.Columns.TARGET_ID));
                int targetType = cursor.getInt(cursor.getColumnIndex(TodayProviderContract.Tables.Comments.Columns.TARGET_TYPE));

                //sync
                syncComment(context.getContentResolver(), targetId, targetType, comment, Uri.withAppendedPath(TodayProviderContract.COMMENTS_URI, Long.toString(recordId)));

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
}
