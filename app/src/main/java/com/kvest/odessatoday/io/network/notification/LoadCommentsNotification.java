package com.kvest.odessatoday.io.network.notification;

import android.content.Intent;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 28.07.14
 * Time: 23:58
 * To change this template use File | Settings | File Templates.
 */
public class LoadCommentsNotification {
    public static final String ACTION = "com.kvest.odessatoday.action.LOAD_COMMENTS_NOTIFICATION";

    private  static final String RESULT_DATA = "com.kvest.odessatoday.extra.RESULT";
    private  static final String ERROR_MESSAGE_DATA = "com.kvest.odessatoday.extra.ERROR_MESSAGE";
    private  static final String TARGET_ID_DATA = "com.kvest.odessatoday.extra.TARGET_ID";
    private  static final String TARGET_TYPE_DATA = "com.kvest.odessatoday.extra.TARGET_TYPE";

    public static Intent createSuccessResult(long targetId, int targetType) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(RESULT_DATA, true);
        intent.putExtra(TARGET_ID_DATA, targetId);
        intent.putExtra(TARGET_TYPE_DATA, targetType);

        return intent;
    }

    public static Intent createErrorsResult(String errorMessage, long targetId, int targetType) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(RESULT_DATA, false);
        intent.putExtra(ERROR_MESSAGE_DATA, errorMessage);
        intent.putExtra(TARGET_ID_DATA, targetId);
        intent.putExtra(TARGET_TYPE_DATA, targetType);

        return intent;
    }

    public static boolean isSuccessful(Intent intent) {
        return intent.getBooleanExtra(RESULT_DATA, false);
    }

    public static String getErrorMessage(Intent intent) {
        if (intent.hasExtra(ERROR_MESSAGE_DATA)) {
            return intent.getStringExtra(ERROR_MESSAGE_DATA);
        } else {
            return "";
        }
    }

    public static long getTargetId(Intent intent) {
        return intent.getLongExtra(TARGET_ID_DATA, -1);

    }

    public static int getTargetType(Intent intent) {
        return intent.getIntExtra(TARGET_TYPE_DATA, -1);
    }
}
