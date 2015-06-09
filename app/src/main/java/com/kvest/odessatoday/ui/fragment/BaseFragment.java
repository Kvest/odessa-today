package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

/**
 * Created by kvest on 09.06.15.
 */
public class BaseFragment extends Fragment {
    protected void showErrorSnackbar(Activity activity, int errorMessageId) {
        SnackbarManager.show(
                Snackbar.with(activity.getApplicationContext())
                        .text(errorMessageId)
                        .position(Snackbar.SnackbarPosition.BOTTOM)
                        .color(Color.RED)
                        .type(SnackbarType.MULTI_LINE)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                        .animation(true)
                , activity);
    }
}
