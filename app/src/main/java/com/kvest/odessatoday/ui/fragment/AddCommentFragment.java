package com.kvest.odessatoday.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kvest.odessatoday.R;

/**
 * Created by kvest on 08.01.16.
 */
public class AddCommentFragment extends BaseFragment {
    private static final String ARGUMENT_TARGET_ID = "com.kvest.odessatoday.argument.TARGET_ID";
    private static final String ARGUMENT_TARGET_TYPE = "com.kvest.odessatoday.argument.TARGET_TYPE";

    public static AddCommentFragment newInstance(long targetId, int targetType) {
        Bundle arguments = new Bundle(2);
        arguments.putInt(ARGUMENT_TARGET_TYPE, targetType);
        arguments.putLong(ARGUMENT_TARGET_ID, targetId);

        AddCommentFragment result = new AddCommentFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_comment_fragment, container, false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {

    }
}
