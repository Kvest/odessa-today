package com.kvest.odessatoday.io.network.handler;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import com.kvest.odessatoday.io.network.request.AddCommentRequest;
import com.kvest.odessatoday.provider.TodayProviderContract;
import com.kvest.odessatoday.utils.Utils;

/**
 * Created by Kvest on 10.01.2015.
 */
public class UploadCommentHandler extends CommentHandler {
    private static final String COMMENT_RECORD_ID_EXTRA = "com.kvest.odessatoday.EXTRAS.COMMENT_RECORD_ID";

    public static void putExtras(Intent intent, long recordId) {
        intent.putExtra(COMMENT_RECORD_ID_EXTRA, recordId);
    }

    @Override
    public void processIntent(Context context, Intent intent) {
        //get extra data
        long recordId = intent.getLongExtra(COMMENT_RECORD_ID_EXTRA, -1);
        Uri commentUri = Uri.withAppendedPath(TodayProviderContract.COMMENTS_URI, Long.toString(recordId));

        //get comment and target id and type
        long targetId = -1;
        int targetType = -1;
        AddCommentRequest.Comment comment = new AddCommentRequest.Comment();
        comment.device_id = Utils.getDeviceId(context);

        Cursor cursor = context.getContentResolver().query(commentUri, ADD_COMMENTS_PROJECTION, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                comment.name = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Comments.Columns.NAME));
                comment.text = cursor.getString(cursor.getColumnIndex(TodayProviderContract.Tables.Comments.Columns.TEXT));
                targetId = cursor.getLong(cursor.getColumnIndex(TodayProviderContract.Tables.Comments.Columns.TARGET_ID));
                targetType = cursor.getInt(cursor.getColumnIndex(TodayProviderContract.Tables.Comments.Columns.TARGET_TYPE));
            }
        } finally {
            cursor.close();
        }

        syncComment(context.getContentResolver(), targetId, targetType, comment, commentUri);
    }
}
