package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.FontUtils;
import com.kvest.odessatoday.utils.SettingsSPStorage;

import java.util.concurrent.TimeUnit;

/**
 * Created by kvest on 08.01.16.
 */
public class AddCommentFragment extends BaseFragment {
    private static final String ARGUMENT_TARGET_ID = "com.kvest.odessatoday.argument.TARGET_ID";
    private static final String ARGUMENT_TARGET_TYPE = "com.kvest.odessatoday.argument.TARGET_TYPE";

    private EditText commentAuthor;
    private EditText commentText;
    private TextView rateLabel;
    private RadioGroup rate;

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
        //store widgets
        commentAuthor = (EditText)rootView.findViewById(R.id.comment_author);
        commentText = (EditText)rootView.findViewById(R.id.comment_text);
        rateLabel = (TextView)rootView.findViewById(R.id.rate_label);
        rate = (RadioGroup)rootView.findViewById(R.id.rate);
        View sendCommentButton = rootView.findViewById(R.id.send_comment);

        Typeface helveticaneuecyrRoman = FontUtils.getFont(getActivity().getAssets(), FontUtils.HELVETICANEUECYR_ROMAN_FONT);
        commentAuthor.setTypeface(helveticaneuecyrRoman);
        commentText.setTypeface(helveticaneuecyrRoman);

        commentAuthor.setText(SettingsSPStorage.getCommentAuthorName(getActivity()));

        if (!canRate()) {
            //hide rate components
            rateLabel.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
        }

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendComment()) {
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.finish();
                    }
                }
            }
        });
    }

    private float getRating() {
        if (canRate()) {
            switch (rate.getCheckedRadioButtonId()) {
                case R.id.rating1 : return 1f;
                case R.id.rating2 : return 2f;
                case R.id.rating3 : return 3f;
                case R.id.rating4 : return 4f;
                case R.id.rating5 : return 5f;
            }
        }

        return -1;
    }

    private boolean sendComment() {
        Context context = getActivity();
        if (isNewCommentDataValid() && context != null) {
            //send comment
            DataProviderHelper.addComment(context, getTargetType(), getTargetId(), getCommentAuthor(),
                                          getCommentText(), getRating(),
                                          TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

            //remember name
            SettingsSPStorage.setCommentAuthorName(context, getCommentAuthor());

            return true;
        }

        return false;
    }

    private boolean isNewCommentDataValid() {
        boolean isDataValid = true;

        if (TextUtils.isEmpty(getCommentAuthor())) {
            commentAuthor.setError(getString(R.string.fill_field));
            isDataValid = false;
        }

        if (TextUtils.isEmpty(getCommentText())) {
            commentText.setError(getString(R.string.fill_field));
            isDataValid = false;
        }

        return isDataValid;
    }

    private String getCommentText() {
        return commentText.getText().toString().trim();
    }

    private String getCommentAuthor() {
        return commentAuthor.getText().toString().trim();
    }

    private long getTargetId() {
        Bundle arguments = getArguments();
        return arguments != null ? arguments.getLong(ARGUMENT_TARGET_ID, -1) : -1;
    }

    private int getTargetType() {
        Bundle arguments = getArguments();
        return arguments != null ? arguments.getInt(ARGUMENT_TARGET_TYPE, -1) : -1;
    }

    private boolean canRate() {
        int targetType = getTargetType();

        return (targetType == Constants.CommentTargetType.FILM || targetType == Constants.CommentTargetType.CONCERT
                || targetType == Constants.CommentTargetType.PARTY || targetType == Constants.CommentTargetType.SPECTACLE
                || targetType == Constants.CommentTargetType.EXHIBITION || targetType == Constants.CommentTargetType.SPORT
                || targetType == Constants.CommentTargetType.WORKSHOP);
    }
}
