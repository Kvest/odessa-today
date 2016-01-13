package com.kvest.odessatoday.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.io.network.event.CommentsLoadedEvent;
import com.kvest.odessatoday.provider.DataProviderHelper;
import com.kvest.odessatoday.service.NetworkService;
import com.kvest.odessatoday.ui.activity.AddCommentActivity;
import com.kvest.odessatoday.ui.adapter.CommentsAdapter;
import com.kvest.odessatoday.ui.widget.CommentsCountView;
import com.kvest.odessatoday.utils.BusProvider;
import com.kvest.odessatoday.utils.FontUtils;
import com.squareup.otto.Subscribe;

import static com.kvest.odessatoday.provider.TodayProviderContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 10.08.14
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public class CommentsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
                                                              AbsListView.OnScrollListener,
                                                              SwipeRefreshLayout.OnRefreshListener {
    private static final String ARGUMENT_TARGET_ID = "com.kvest.odessatoday.argument.TARGET_ID";
    private static final String ARGUMENT_TARGET_TYPE = "com.kvest.odessatoday.argument.TARGET_TYPE";
    private static final String ARGUMENT_COMMENTS_COUNT = "com.kvest.odessatoday.argument.COMMENTS_COUNT";
    private static final String ARGUMENT_RATING = "com.kvest.odessatoday.argument.RATING";
    private static final String ARGUMENT_TARGET_NAME = "com.kvest.odessatoday.argument.TARGET_NAME";
    private static final String ARGUMENT_TARGET_TYPE_NAME = "com.kvest.odessatoday.argument.TARGET_TYPE_NAME";
    private static final int MIN_ITEMS_FOR_MORE_LOAD = 2;
    private static final int LOAD_LIMIT = 20;
    public static final float EMPTY_RATING = -1;
    private static final int COMMENTS_LOADER_ID = 1;

    private CommentsAdapter adapter;

    private ImageView progress;
    private SwipeRefreshLayout refreshLayout;

    private boolean hasMoreComments = false;

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
        View footerView = inflater.inflate(R.layout.comments_fragment_footer, null);

        init(rootView, footerView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        loadComments(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(COMMENTS_LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop all progresses
        stopProgress();

        BusProvider.getInstance().unregister(this);
    }

    private void init(View rootView, View footer) {
        //store widgets
        View addComment = rootView.findViewById(R.id.add_comment);
        TextView addCommentLabel = (TextView)rootView.findViewById(R.id.add_comment_label);
        CommentsCountView commentsCount = (CommentsCountView)rootView.findViewById(R.id.comments_count);
        RatingBar rating = (RatingBar)rootView.findViewById(R.id.rating);
        TextView targetName = (TextView)rootView.findViewById(R.id.target_name);
        TextView targetTypeName = (TextView)rootView.findViewById(R.id.target_type);

        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.application_green);

        //store list view
        ListView commentsList = (ListView)rootView.findViewById(R.id.comments_list);
        commentsList.addFooterView(footer);

        //create and set an adapter
        adapter = new CommentsAdapter(getActivity());
        commentsList.setAdapter(adapter);

        commentsList.setOnScrollListener(this);

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

        //setup footer
        progress = (ImageView)footer.findViewById(R.id.progress);
    }

    private void showAddCommentFragment() {
        Context context = getActivity();
        if (context != null) {
            AddCommentActivity.start(context, getTargetId(), getTargetType());
        }
    }

    //method loads first set of the comments
    private void loadComments(Context context) {
        NetworkService.loadComments(context, getTargetId(), getTargetType(), 0, LOAD_LIMIT, true);
    }

    //method loads next set of the comments
    private void loadNextComments(Context context, int offset) {
        NetworkService.loadComments(context, getTargetId(), getTargetType(), offset, LOAD_LIMIT, false);
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

    private void startFooterProgress() {
        AnimationDrawable frameAnimation = (AnimationDrawable) progress.getBackground();
        frameAnimation.start();

        progress.setVisibility(View.VISIBLE);
    }

    private void stopFooterProgress() {
        AnimationDrawable frameAnimation = (AnimationDrawable) progress.getBackground();
        frameAnimation.stop();

        progress.setVisibility(View.GONE);
    }

    private void stopProgress() {
        refreshLayout.setRefreshing(false);

        stopFooterProgress();
    }

    @Override
    public void onRefresh() {
        //reload comments
        Activity activity = getActivity();
        if (activity != null) {
            stopFooterProgress();

            hasMoreComments = false;

            loadComments(activity);
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

    @Subscribe
    public void onCommentsLoaded(CommentsLoadedEvent event) {
        hasMoreComments = event.hasMoreComments();

        //event dispatched not in the UI thread
        progress.post(new Runnable() {
            @Override
            public void run() {
                stopProgress();
            }
        });

        Activity activity = getActivity();
        if (!event.isSuccessful() && activity != null) {
            showErrorSnackbar(activity, R.string.error_loading_comments);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int count = adapter.getCount();
        if (count > 0 && (count - firstVisibleItem - visibleItemCount) < MIN_ITEMS_FOR_MORE_LOAD) {
            Context context = getActivity();
            if (hasMoreComments && context != null) {
                hasMoreComments = false;

                //show progress
                startFooterProgress();

                loadNextComments(context, count);
            }
        }
    }
}
