package com.kvest.odessatoday.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by kvest on 01.07.16.
 */
public class ProgressDialogFragment extends DialogFragment {
    private final static String ARG_CANCELABLE = "com.kvest.odessatoday.argument.CANCELABLE";
    private final static String ARG_IS_RETAIN_INSTANCE = "com.kvest.odessatoday.argument.IS_RETAIN_INSTANCE";

    private CustomProgressDialog progressDialog;

    /**
     * Create a fragment dialog with isCancelable flag
     * @param isCancelable Is this dialog cancelable or not
     * @return New fragment instance
     */
    public static ProgressDialogFragment newInstance(boolean isCancelable, boolean isRetainInstance) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_CANCELABLE, isCancelable);
        args.putBoolean(ARG_IS_RETAIN_INSTANCE, isRetainInstance);
        ProgressDialogFragment frag = new ProgressDialogFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final boolean cancelable = getArguments().getBoolean(ARG_CANCELABLE);
        final boolean isRetainInstance = getArguments().getBoolean(ARG_IS_RETAIN_INSTANCE);
        progressDialog = CustomProgressDialog.newInstance(getContext());
        setCancelable(cancelable);
        setRetainInstance(isRetainInstance);
        return progressDialog;

    }
}
