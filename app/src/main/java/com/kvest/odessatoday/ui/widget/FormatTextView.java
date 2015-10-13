package com.kvest.odessatoday.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.utils.Constants;
import com.kvest.odessatoday.utils.FontUtils;

/**
 * Created by kvest on 03.10.15.
 */
public class FormatTextView extends TextView {
    private Drawable background;

    public FormatTextView(Context context) {
        super(context);

        init(context);
    }

    public FormatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public FormatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    @SuppressLint("NewApi")
    public FormatTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);

        //text
        setTypeface(FontUtils.getFont(context.getAssets(), FontUtils.HELVETICANEUECYR_BOLD_FONT));
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.format_text_size));
        setTextColor(context.getResources().getColor(R.color.format_text_color));

        //set background
        background = getResources().getDrawable(R.drawable.film_format_bg);
        setBackgroundDrawable(background);
    }

    public void setFormat(int format) {
        switch (format) {
            case Constants.FilmFormat.THIRTY_FIFE_MM:
                setText(R.string.format_thirty_fife_mm);
                setBgColor(R.color.format_thirty_fife_mm);
                break;
            case Constants.FilmFormat.TWO_D:
                setText(R.string.format_two_d);
                setBgColor(R.color.format_two_d);
                break;
            case Constants.FilmFormat.THREE_D:
                setText(R.string.format_three_d);
                setBgColor(R.color.format_three_d);
                break;
            case Constants.FilmFormat.IMAX_THREE_D:
                setText(R.string.format_imax_three_d);
                setBgColor(R.color.format_imax_three_d);
                break;
            case Constants.FilmFormat.IMAX:
                setText(R.string.format_imax);
                setBgColor(R.color.format_imax);
                break;
            case Constants.FilmFormat.FIVE_D:
                setText(R.string.format_five_d);
                setBgColor(R.color.format_five_d);
                break;
            case Constants.FilmFormat.FOUR_DX:
                setText(R.string.format_four_dx);
                setBgColor(R.color.format_four_dx);
                break;
            default:
                setText(R.string.format_unknown);
                setBgColor(R.color.format_unknown);
        }
    }

    private void setBgColor(int colorResId) {
        background.setColorFilter(getResources().getColor(colorResId), PorterDuff.Mode.SRC_IN);
    }

}
