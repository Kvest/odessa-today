package com.kvest.odessatoday.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import com.kvest.odessatoday.R;

/**
 * Created by Kvest on 03.01.2015.
 */
public class CirclePagerIndicator extends View implements ViewPager.OnPageChangeListener {
    private static final int MIN_INDICATORS_COUNT = 2;
    private static final int DEFAULT_RADIUS = 10;
    private static final int DEFAULT_MARGIN_BETWEEN_CIRCLES = 0;
    private static final int DEFAULT_GRAVITY = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

    private int radius;
    private int marginBetweenCircles;
    private int circleSelectedColor;
    private int circleUnselectedColor;
    private int gravity;
    private Paint paint;

    private int position;
    private float positionOffset;
    private int state = ViewPager.SCROLL_STATE_IDLE;

    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener listener;

    public CirclePagerIndicator(Context context) {
        super(context);

        init(context, null);
    }

    public CirclePagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public CirclePagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.listener = listener;
    }

    public void setViewPager(ViewPager view) {
        if (viewPager == view) {
            return;
        }

        if (view == null) {
            throw new IllegalArgumentException("ViewPager can not be null.");
        }

        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        viewPager = view;
        viewPager.setOnPageChangeListener(this);
        invalidate();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    public int getMarginBetweenCircles() {
        return marginBetweenCircles;
    }

    public void setMarginBetweenCircles(int marginBetweenCircles) {
        this.marginBetweenCircles = marginBetweenCircles;
        invalidate();
    }

    public int getCircleSelectedColor() {
        return circleSelectedColor;
    }

    public void setCircleSelectedColor(int circleSelectedColor) {
        this.circleSelectedColor = circleSelectedColor;
        invalidate();
    }

    public int getCircleUnselectedColor() {
        return circleUnselectedColor;
    }

    public void setCircleUnselectedColor(int circleUnselectedColor) {
        this.circleUnselectedColor = circleUnselectedColor;
        invalidate();
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = getPaddingTop() + getPaddingBottom() + radius * 2;

        //calculate width
        if (viewPager != null && viewPager.getAdapter() != null) {
            int count = viewPager.getAdapter().getCount();
            width = getPaddingLeft() + getPaddingRight() + count * radius * 2 + (count - 1) * marginBetweenCircles;
        }

        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (viewPager == null) {
            return;
        }

        int count = viewPager.getAdapter().getCount();
        if (count <= MIN_INDICATORS_COUNT) {
            return;
        }

        //calculate left offset
        int leftOffset = getPaddingLeft();
        int horizontalFreeSpace = getWidth() - count * radius * 2 - (count - 1) * marginBetweenCircles - getPaddingLeft() - getPaddingRight();
        if (horizontalFreeSpace < 0) {
            horizontalFreeSpace = 0;
        }
        if ((gravity & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL) {
            leftOffset += horizontalFreeSpace / 2;
        } else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            leftOffset += horizontalFreeSpace;
        }

        //calculate top offset
        int topOffset = getPaddingTop();
        int verticalFreeSpace = getHeight() - radius * 2 - getPaddingTop() - getPaddingBottom();
        if (verticalFreeSpace < 0) {
            verticalFreeSpace = 0;
        }
        if ((gravity & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL) {
            topOffset += verticalFreeSpace / 2;
        } else if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            topOffset += verticalFreeSpace;
        }

        //draw count indication circles
        paint.setColor(circleUnselectedColor);
        for (int i = 0; i < count; ++i) {
            canvas.drawCircle(leftOffset + i * marginBetweenCircles + i * radius * 2 + radius, topOffset + radius, radius, paint);
        }

        //draw active page indication circles
        paint.setColor(circleSelectedColor);
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            float dx = (2 * radius + marginBetweenCircles) * positionOffset;
            canvas.drawCircle(leftOffset + position * marginBetweenCircles + position * radius * 2 + radius + dx, topOffset + radius, radius, paint);
        } else {
            int currentItem = viewPager.getCurrentItem();
            canvas.drawCircle(leftOffset + currentItem * marginBetweenCircles + currentItem * radius * 2 + radius, topOffset + radius, radius, paint);
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState)state;

        super.onRestoreInstanceState(savedState.getSuperState());

        radius = savedState.radius;
        marginBetweenCircles = savedState.marginBetweenCircles;
        circleSelectedColor = savedState.circleSelectedColor;
        circleUnselectedColor = savedState.circleUnselectedColor;
        gravity = savedState.gravity;

        // requestLayout();
    }
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState savedState = new SavedState(superState);
        savedState.radius = radius;
        savedState.marginBetweenCircles = marginBetweenCircles;
        savedState.circleSelectedColor = circleSelectedColor;
        savedState.circleUnselectedColor = circleUnselectedColor;
        savedState.gravity = gravity;

        return savedState;
    }

    private void init(Context context, AttributeSet attrs) {
        //set default values
        radius = DEFAULT_RADIUS;
        circleSelectedColor = Color.DKGRAY;
        circleUnselectedColor = Color.LTGRAY;
        marginBetweenCircles = DEFAULT_MARGIN_BETWEEN_CIRCLES;
        gravity = DEFAULT_GRAVITY;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        if (attrs != null) {
            obtainAttributes(context, attrs);
        }
    }

    private void obtainAttributes(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CirclePagerIndicator);
        try {
            radius = typedArray.getDimensionPixelSize(R.styleable.CirclePagerIndicator_circle_radius, radius);
            circleSelectedColor = typedArray.getColor(R.styleable.CirclePagerIndicator_selected_circle_color, circleSelectedColor);
            circleUnselectedColor = typedArray.getColor(R.styleable.CirclePagerIndicator_unselected_circle_color, circleUnselectedColor);
            marginBetweenCircles = typedArray.getDimensionPixelSize(R.styleable.CirclePagerIndicator_margin_between_circles, marginBetweenCircles);
            gravity = typedArray.getInt(R.styleable.CirclePagerIndicator_gravity, gravity);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (listener != null) {
            listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        this.position = position;
        this.positionOffset = positionOffset;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        if (listener != null) {
            listener.onPageSelected(position);
        }

        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (listener != null) {
            listener.onPageScrollStateChanged(state);
        }

        this.state = state;
    }

    private static class SavedState extends BaseSavedState {
        private int radius;
        private int marginBetweenCircles;
        private int circleSelectedColor;
        private int circleUnselectedColor;
        private int gravity;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel in) {
            super(in);

            radius = in.readInt();
            marginBetweenCircles = in.readInt();
            circleSelectedColor = in.readInt();
            circleUnselectedColor = in.readInt();
            gravity = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeInt(radius);
            dest.writeInt(marginBetweenCircles);
            dest.writeInt(circleSelectedColor);
            dest.writeInt(circleUnselectedColor);
            dest.writeInt(gravity);
        }

        @SuppressWarnings("UnusedDeclaration")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public static final class Gravity {
        public static final int RIGHT = 1;
        public static final int LEFT = 2;
        public static final int CENTER_HORIZONTAL = 4;
        public static final int TOP = 8;
        public static final int BOTTOM = 16;
        public static final int CENTER_VERTICAL = 32;
    }
}
