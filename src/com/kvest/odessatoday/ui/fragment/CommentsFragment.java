package com.kvest.odessatoday.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.CursorLoaderBuilder;
import com.kvest.odessatoday.ui.adapter.CommentsAdapter;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 10.08.14
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public class CommentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARGUMENT_TARGET_ID = "com.kvest.odessatoday.argument.TARGET_ID";
    private static final String ARGUMENT_TARGET_TYPE = "com.kvest.odessatoday.argument.TARGET_TYPE";
    private static final int COMMENTS_LOADER_ID = 1;

    private ListView commentsList;
    private CommentsAdapter adapter;

    private View addCommentPanel;
    private Animation showCommentPanelAnimation;
    private Animation hideCommentPanelAnimation;

    private EditText commentName;
    private EditText commentText;
    private Button hideCommentPanelButton;
    private Button sendCommentButton;

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
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.comments_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_comment:
                showAddCommentPanel();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(COMMENTS_LOADER_ID, null, this);
    }

    private void init(View rootView) {
        //store widgets
        addCommentPanel = rootView.findViewById(R.id.add_comment_panel);
        commentName = (EditText)rootView.findViewById(R.id.comment_name);
        commentText = (EditText)rootView.findViewById(R.id.comment_text);
        hideCommentPanelButton = (Button)rootView.findViewById(R.id.hide);
        sendCommentButton = (Button)rootView.findViewById(R.id.send);

        //store list view
        commentsList = (ListView)rootView.findViewById(R.id.comments_list);

        //create and set an adapter
        adapter = new CommentsAdapter(getActivity());
        commentsList.setAdapter(adapter);

        //create animations
        showCommentPanelAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.add_comment_panel_up);
        hideCommentPanelAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.add_comment_panel_down);
        hideCommentPanelAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                addCommentPanel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        sendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendComment()) {
                    hideAddCommentPanel();
                }
            }
        });

        hideCommentPanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAddCommentPanel();
            }
        });
    }

    private boolean sendComment() {
        if (isNewCommentDataValid()) {
            //send comment
            //TODO

            //clean comment text
            commentText.setText("");

            //remember name
            //TODO

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

    private void showAddCommentPanel() {
        if (!isAddCommentPanelShown()) {
            //set visible
            addCommentPanel.setVisibility(View.VISIBLE);

            //animate
            addCommentPanel.clearAnimation();
            addCommentPanel.startAnimation(showCommentPanelAnimation);
        }
    }

    private void hideAddCommentPanel() {
        if (isAddCommentPanelShown()) {
            //animate
            addCommentPanel.clearAnimation();
            addCommentPanel.startAnimation(hideCommentPanelAnimation);
        }
    }

    private boolean isAddCommentPanelShown() {
        return addCommentPanel.getVisibility() == View.VISIBLE;
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
            return CursorLoaderBuilder.getComments(getActivity(), getTargetId(), getTargetType(),
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
