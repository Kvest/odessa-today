package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.adapter.CommentsAdapter;
import com.kvest.odessatoday.ui.widget.CommentsCountView;
import com.kvest.odessatoday.utils.FontUtils;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 10.08.14
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public class CommentsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARGUMENT_TARGET_ID = "com.kvest.odessatoday.argument.TARGET_ID";
    private static final String ARGUMENT_TARGET_TYPE = "com.kvest.odessatoday.argument.TARGET_TYPE";
    private static final String ARGUMENT_COMMENTS_COUNT = "com.kvest.odessatoday.argument.COMMENTS_COUNT";
    private static final String ARGUMENT_RATING = "com.kvest.odessatoday.argument.RATING";
    private static final String ARGUMENT_TARGET_NAME = "com.kvest.odessatoday.argument.TARGET_NAME";
    private static final String ARGUMENT_TARGET_TYPE_NAME = "com.kvest.odessatoday.argument.TARGET_TYPE_NAME";
    public static final float EMPTY_RATING = -1;
    private static final int COMMENTS_LOADER_ID = 1;

    private CommentsAdapter adapter;

    public static CommentsFragment newInstance(long targetId, int targetType,
                                               String targetName, String targetTypeName,
                                               int commentsCount, float rating) {
        Bundle arguments = new Bundle(6);
        arguments.putInt(ARGUMENT_TARGET_TYPE, targetType);
        arguments.putLong(ARGUMENT_TARGET_ID, targetId);
        arguments.putInt(ARGUMENT_COMMENTS_COUNT, commentsCount);
        arguments.putFloat(ARGUMENT_RATING, rating);
        arguments.putString(ARGUMENT_TARGET_NAME, targetName);
        arguments.putString(ARGUMENT_TARGET_TYPE_NAME, targetTypeName);

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        NetworkService.loadComments(activity, getTargetId(), getTargetType());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(COMMENTS_LOADER_ID, null, this);
    }

    private void init(View rootView) {
        //store widgets
        View addComment = rootView.findViewById(R.id.add_comment);
        TextView addCommentLabel = (TextView)rootView.findViewById(R.id.add_comment_label);
        CommentsCountView commentsCount = (CommentsCountView)rootView.findViewById(R.id.comments_count);
        RatingBar rating = (RatingBar)rootView.findViewById(R.id.rating);
        TextView targetName = (TextView)rootView.findViewById(R.id.target_name);
        TextView targetTypeName = (TextView)rootView.findViewById(R.id.target_type);

        //store list view
        ListView commentsList = (ListView)rootView.findViewById(R.id.comments_list);

        //create and set an adapter
        adapter = new CommentsAdapter(getActivity());
        commentsList.setAdapter(adapter);

        //setup header
        commentsCount.setCommentsCount(getCommentsCount());
        float ratingValue = getRating();
        if (ratingValue >= 0) {
            rating.setRating(getRating());
        } else {
            rating.setVisibility(View.GONE);
        }
        targetName.setText(getTargetName());
        targetTypeName.setText(getTargetTypeName());

        //setup footer
        addCommentLabel.setTypeface(FontUtils.getFont(getActivity().getAssets(), FontUtils.HELVETICANEUECYR_BOLD_FONT));
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCommentFragment();
            }
        });
    }

    private void showAddCommentFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        try {
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_up, R.anim.slide_down, R.anim.slide_down);
            transaction.add(R.id.add_comment_fragment_container, AddCommentFragment.newInstance(getTargetId(), getTargetType()));
            transaction.addToBackStack(null);
        } finally {
            transaction.commit();
        }
    }

    private long getTargetId() {
        Bundle arguments = getArguments();
        return arguments != null ? arguments.getLong(ARGUMENT_TARGET_ID, -1) : -1;
    }

    private int getTargetType() {
        Bundle arguments = getArguments();
        return arguments != null ? arguments.getInt(ARGUMENT_TARGET_TYPE, -1) : -1;
    }

    private int getCommentsCount() {
        Bundle arguments = getArguments();
        return arguments != null ? arguments.getInt(ARGUMENT_COMMENTS_COUNT, 0) : -1;
    }

    private float getRating() {
        Bundle arguments = getArguments();
        return arguments != null ? arguments.getFloat(ARGUMENT_RATING, EMPTY_RATING) : EMPTY_RATING;
    }

    private String getTargetName() {
        Bundle arguments = getArguments();
        return arguments != null ? arguments.getString(ARGUMENT_TARGET_NAME, "") : "";
    }

    private String getTargetTypeName() {
        Bundle arguments = getArguments();
        return arguments != null ? arguments.getString(ARGUMENT_TARGET_TYPE_NAME, "") : "";
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
