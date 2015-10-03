package com.kvest.odessatoday.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
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

        //bg
        //TODO
    }

    public void setFormat(int format) {
        switch (format) {
            case Constants.FilmFormat.THIRTY_FIFE_MM:
                setText(R.string.format_thirty_fife_mm);
                setBackgroundResource(R.color.format_thirty_fife_mm);
                break;
            case Constants.FilmFormat.TWO_D:
                setText(R.string.format_two_d);
                setBackgroundResource(R.color.format_two_d);
                break;
            case Constants.FilmFormat.THREE_D:
                setText(R.string.format_three_d);
                setBackgroundResource(R.color.format_three_d);
                break;
            case Constants.FilmFormat.IMAX_THREE_D:
                setText(R.string.format_imax_three_d);
                setBackgroundResource(R.color.format_imax_three_d);
                break;
            case Constants.FilmFormat.IMAX:
                setText(R.string.format_imax);
                setBackgroundResource(R.color.format_imax);
                break;
            case Constants.FilmFormat.FIVE_D:
                setText(R.string.format_five_d);
                setBackgroundResource(R.color.format_five_d);
                break;
            case Constants.FilmFormat.FOUR_DX:
                setText(R.string.format_four_dx);
                setBackgroundResource(R.color.format_four_dx);
                break;
            default:
                setText(R.string.format_unknown);
                setBackgroundResource(R.color.format_unknown);
        }
    }
}
