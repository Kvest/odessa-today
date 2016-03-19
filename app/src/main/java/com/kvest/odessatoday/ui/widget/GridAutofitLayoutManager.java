package com.kvest.odessatoday.ui.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by kvest on 19.03.16.
 */
public class GridAutofitLayoutManager extends GridLayoutManager {
    private int mColumnWidth;
    private boolean mColumnWidthChanged = true;

    public GridAutofitLayoutManager(Context context, int columnWidth)
    {
        super(context, 1);

        if (columnWidth <= 0) {
            throw new IllegalArgumentException("Column width should be > 0");
        }
        setColumnWidth(columnWidth);
    }

    public GridAutofitLayoutManager(Context context, int columnWidth, int orientation, boolean reverseLayout)
    {
        super(context, 1, orientation, reverseLayout);

        if (columnWidth <= 0) {
            throw new IllegalArgumentException("Column width should be > 0");
        }
        setColumnWidth(columnWidth);
    }


    public void setColumnWidth(int newColumnWidth)
    {
        if (newColumnWidth > 0 && newColumnWidth != mColumnWidth)
        {
            mColumnWidth = newColumnWidth;
            mColumnWidthChanged = true;
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        if (mColumnWidthChanged && mColumnWidth > 0)
        {
            int totalSpace;
            if (getOrientation() == VERTICAL){
                totalSpace = getWidth() - getPaddingRight() - getPaddingLeft();
            } else {
                totalSpace = getHeight() - getPaddingTop() - getPaddingBottom();
            }
            int spanCount = Math.max(1, totalSpace / mColumnWidth);
            setSpanCount(spanCount);
            mColumnWidthChanged = false;
        }
        super.onLayoutChildren(recycler, state);
    }
}
