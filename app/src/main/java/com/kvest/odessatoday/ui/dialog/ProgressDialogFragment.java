package com.kvest.odessatoday.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by kvest on 01.07.16.
 */
public class ProgressDialogFragment extends DialogFragment {
    private final static String CANCELABLE_ARG = "cancelable";
    private final static String IS_RETAIN_INSTANCE_ARG = "is_retain_instance";

    private CustomProgressDialog progressDialog;

    /**
     * Create a fragment dialog with isCancelable flag
     * @param isCancelable Is this dialog cancelable or not
     * @return New fragment instance
     */
    public static ProgressDialogFragment newInstance(boolean isCancelable, boolean isRetainInstance) {
        Bundle args = new Bundle();
        args.putBoolean(CANCELABLE_ARG, isCancelable);
        args.putBoolean(IS_RETAIN_INSTANCE_ARG, isRetainInstance);
        ProgressDialogFragment frag = new ProgressDialogFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final boolean cancelable = getArguments().getBoolean(CANCELABLE_ARG);
        final boolean isRetainInstance = getArguments().getBoolean(IS_RETAIN_INSTANCE_ARG);
        progressDialog = CustomProgressDialog.newInstance(getActivity());
        setCancelable(cancelable);
        setRetainInstance(isRetainInstance);
        return progressDialog;

    }
}
