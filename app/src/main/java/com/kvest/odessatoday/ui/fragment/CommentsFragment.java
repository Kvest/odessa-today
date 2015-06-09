package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.ui.adapter.CommentsAdapter;
import com.kvest.odessatoday.utils.KeyboardUtils;
import com.kvest.odessatoday.utils.SettingsSPStorage;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.concurrent.TimeUnit;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 10.08.14
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public class CommentsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
                                                          SlidingUpPanelLayout.PanelSlideListener {
    private static final String ARGUMENT_TARGET_ID = "com.kvest.odessatoday.argument.TARGET_ID";
    private static final String ARGUMENT_TARGET_TYPE = "com.kvest.odessatoday.argument.TARGET_TYPE";
    private static final int COMMENTS_LOADER_ID = 1;

    private ListView commentsList;
    private CommentsAdapter adapter;

    private Animation openPanelIconUp;
    private Animation openPanelIconDown;

    private SlidingUpPanelLayout slidingUpPanelLayout;

    private EditText commentName;
    private EditText commentText;
    private ImageView openPanelIcon;
    private ImageButton sendCommentButton;

    public static CommentsFragment getInstance(int targetType, long targetId) {
        Bundle arguments = new Bundle(2);
        arguments.putInt(ARGUMENT_TARGET_TYPE, targetType);
        arguments.putLong(ARGUMENT_TARGET_ID, targetId);

        CommentsFragment result = new CommentsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comments_fragment, container, false);

        init(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(COMMENTS_LOADER_ID, null, this);
    }

    private void init(View rootView) {
        //store widgets
        slidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelSlideListener(this);
        commentName = (EditText)rootView.findViewById(R.id.comment_name);
        commentText = (EditText)rootView.findViewById(R.id.comment_text);
        openPanelIcon = (ImageView)rootView.findViewById(R.id.open_panel_icon);
        sendCommentButton = (ImageButton)rootView.findViewById(R.id.send);

        commentName.setText(SettingsSPStorage.getCommentAuthorName(getActivity()));

        //store list view
        commentsList = (ListView)rootView.findViewById(R.id.comments_list);

        //create and set an adapter
        adapter = new CommentsAdapter(getActivity());
        commentsList.setAdapter(adapter);

        //load animations
        openPanelIconUp = AnimationUtils.loadAnimation(getActivity(), R.anim.open_panel_icon_up);
        openPanelIconDown = AnimationUtils.loadAnimation(getActivity(), R.anim.open_panel_icon_down);

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendComment()) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }
        });
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {}

    @Override
    public void onPanelCollapsed(View panel) {
        sendCommentButton.setVisibility(View.INVISIBLE);

        Activity activity = getActivity();
        if (activity != null) {
            KeyboardUtils.hideKeyboard(activity, activity.getCurrentFocus());
        }

        //workaround - update the size of the comments list. Otherwise it will be shown only on the part of the screen
        commentsList.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        commentsList.requestLayout();

        //animate arrow
        openPanelIcon.clearAnimation();
        openPanelIcon.startAnimation(openPanelIconDown);
    }

    @Override
    public void onPanelExpanded(View panel) {
        sendCommentButton.setVisibility(View.VISIBLE);

        //open keyboard
        Context context = getActivity();
        if (context != null) {
            if (TextUtils.isEmpty(commentName.getText())) {
                commentName.requestFocus();
                KeyboardUtils.showKeyboard(context, commentName);
            } else {
                commentText.requestFocus();
                KeyboardUtils.showKeyboard(context, commentText);
            }
        }

        //animate arrow
        openPanelIcon.clearAnimation();
        openPanelIcon.startAnimation(openPanelIconUp);
    }

    @Override
    public void onPanelAnchored(View panel) {}

    @Override
    public void onPanelHidden(View panel) {}

    private boolean sendComment() {
        Context context = getActivity();
        if (isNewCommentDataValid() && context != null) {
            //send comment
            DataProviderHelper.addComment(context, getTargetType(), getTargetId(), getCommentName(),
                                          getCommentText(), TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

            //clean comment text
            commentText.setText("");

            //remember name
            SettingsSPStorage.setCommentAuthorName(context, getCommentName());

            return true;
        }

        return false;
    }

    private boolean isNewCommentDataValid() {
        boolean isDataValid = true;

        if (TextUtils.isEmpty(getCommentName())) {
            commentName.setError(getString(R.string.fill_field));
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

    private String getCommentName() {
        return commentName.getText().toString().trim();
    }

    private long getTargetId() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getLong(ARGUMENT_TARGET_ID, -1);
        } else {
            return -1;
        }
    }

    private int getTargetType() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            return arguments.getInt(ARGUMENT_TARGET_TYPE, -1);
        } else {
            return -1;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == COMMENTS_LOADER_ID) {
            return DataProviderHelper.getCommentsLoader(getActivity(), getTargetId(), getTargetType(),
                    CommentsAdapter.PROJECTION, Tables.Comments.DATE_ORDER_DESC);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case COMMENTS_LOADER_ID :
                adapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case COMMENTS_LOADER_ID :
                adapter.swapCursor(null);
                break;
        }
    }
}
