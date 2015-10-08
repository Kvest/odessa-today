package com.kvest.odessatoday.io.network.notification;

import android.content.Intent;

/**
 * Created by kvest on 08.10.15.
 */
public class LoadPlacesNotification {
    public static final String ACTION = "com.kvest.odessatoday.action.LOAD_PLACES_NOTIFICATION";

    private  static final String RESULT_DATA = "com.kvest.odessatoday.extra.RESULT";
    private  static final String ERROR_MESSAGE_DATA = "com.kvest.odessatoday.extra.ERROR_MESSAGE";
    private  static final String PLACE_TYPE_DATA = "com.kvest.odessatoday.extra.PLACE_TYPE";

    public static Intent createSuccessResult(int placeType) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(RESULT_DATA, true);
        intent.putExtra(PLACE_TYPE_DATA, placeType);

        return intent;
    }

    public static Intent createErrorsResult(String errorMessage, int placeType) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(RESULT_DATA, false);
        intent.putExtra(ERROR_MESSAGE_DATA, errorMessage);
        intent.putExtra(PLACE_TYPE_DATA, placeType);

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

    public static int getPlaceType(Intent intent) {
        return intent.getIntExtra(PLACE_TYPE_DATA, -1);
    }
}
