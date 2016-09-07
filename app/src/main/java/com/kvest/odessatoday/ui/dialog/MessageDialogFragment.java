package com.kvest.odessatoday.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by kvest on 9/7/16.
 */
public class MessageDialogFragment extends DialogFragment {
    private final static String ARGUMENT_MESSAGE = "com.kvest.odessatoday.argument.MESSAGE";

    public static MessageDialogFragment newInstance(String message) {
        MessageDialogFragment result = new MessageDialogFragment();

        Bundle arguments = new Bundle(1);
        arguments.putString(ARGUMENT_MESSAGE, message);
        result.setArguments(arguments);

        return result;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(ARGUMENT_MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);

        return builder.create();
    }
}
