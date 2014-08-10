package com.kvest.odessatoday.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        //store list view
        commentsList = (ListView)rootView.findViewById(R.id.comments_list);

        //create and set an adapter
        adapter = new CommentsAdapter(getActivity());
        commentsList.setAdapter(adapter);
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
