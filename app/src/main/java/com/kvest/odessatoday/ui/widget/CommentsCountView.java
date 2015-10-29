package com.kvest.odessatoday.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by kvest on 29.10.15.
 */
public class CommentsCountView extends FontTextView {

    public CommentsCountView(Context context) {
        super(context);
    }

    public CommentsCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentsCountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public CommentsCountView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCommentsCount(int count) {
        if (count > 100) {
            setText("100+");
        } else {
            setText(Integer.toString(count));
        }
    }
}
