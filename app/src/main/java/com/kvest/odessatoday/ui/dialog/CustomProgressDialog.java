package com.kvest.odessatoday.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.kvest.odessatoday.R;

/**
 * Created by kvest on 01.07.16.
 */
public class CustomProgressDialog extends ProgressDialog {
    /**
     * Get new instance of CustomProgressDialog
     * @param context Activity context
     * @return CustomProgressDialog object
     */
    public static CustomProgressDialog newInstance(Context context) {
        return newInstance(context, null, false);
    }

    /**
     * Get new instance of CustomProgressDialog
     * @param context Activity context
     * @param message Dialog message
     * @param isCancelable Can be canceled
     * @return CustomProgressDialog object
     */
    public static CustomProgressDialog newInstance(Context context, String message, boolean isCancelable) {
        return new CustomProgressDialog(context, message, isCancelable);
    }

    /**
     * Constructor of CustomProgressDialog
     * @param context Activity context
     * @param message Dialog message
     * @param isCancelable Can be canceled
     */
    private CustomProgressDialog(Context context, String message, boolean isCancelable) {
        super(context, R.style.ProgressDialogTheme);
        setCancelable(isCancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_layout);
    }
}