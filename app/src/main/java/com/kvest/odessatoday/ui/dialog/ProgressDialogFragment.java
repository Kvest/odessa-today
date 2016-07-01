package com.kvest.odessatoday.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by kvest on 01.07.16.
 */
public class ProgressDialogFragment extends DialogFragment {
    private final static String CANCELABLE_ARG = "cancelable";

    private CustomProgressDialog progressDialog;

    /**
     * Create a fragment dialog with isCancelable flag
     * @param isCancelable Is this dialog cancelable or not
     * @return New fragment instance
     */
    public static ProgressDialogFragment newInstance(boolean isCancelable) {
        Bundle args = new Bundle();
        args.putBoolean(CANCELABLE_ARG, isCancelable);
        ProgressDialogFragment frag = new ProgressDialogFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final boolean cancelable = getArguments().getBoolean(CANCELABLE_ARG);
        progressDialog = CustomProgressDialog.newInstance(getActivity());
        setCancelable(cancelable);
        setRetainInstance(true);
        return progressDialog;

    }
}
