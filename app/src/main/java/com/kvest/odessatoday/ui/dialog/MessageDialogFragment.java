package com.kvest.odessatoday.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.kvest.odessatoday.R;

/**
 * Created by kvest on 9/7/16.
 */
public class MessageDialogFragment extends DialogFragment {
    private final static String ARGUMENT_MESSAGE = "com.kvest.odessatoday.argument.MESSAGE";
    private DialogInterface.OnDismissListener onDismissListener;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MessageDialogStyle);

        TextView view = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.message_dialog_view, null);
        view.setMovementMethod(new ScrollingMovementMethod());
        view.setText(message);
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        onDismissListener = null;
    }
}
