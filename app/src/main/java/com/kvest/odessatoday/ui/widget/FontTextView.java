package com.kvest.odessatoday.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.utils.FontUtils;

/**
 * Created by roman on 10/22/15.
 */
public class FontTextView extends TextView {

    public FontTextView(Context context) {
        super(context);

        init(context, null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @SuppressLint("NewApi")
    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FontTextView, 0, 0);
            try {
                //set font
                String fontAsset = a.getString(R.styleable.FontTextView_fontAsset);
                if (!TextUtils.isEmpty(fontAsset)) {
                    Typeface typeface = FontUtils.getFont(context.getAssets(), fontAsset);
                    setTypeface(typeface);
                }
            } finally {
                a.recycle();
            }
        }
    }
}
