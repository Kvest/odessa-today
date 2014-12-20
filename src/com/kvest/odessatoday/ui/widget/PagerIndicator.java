package com.kvest.odessatoday.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.kvest.odessatoday.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 20.12.14
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public class PagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    private static final int MIN_ITEMS_COUNT = 2;

    private Context context;
    private ViewPager pager;
    private LinearLayout container;
    private List<ImageView> items;

    private Drawable activeCircleDrawable;
    private Drawable inactiveCircleDrawable;
    private float marginBetweenCircles;

    private Callback callback;

    public PagerIndicator(Context context) {
        super(context);
        this.context = context;
        init(context, null);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    public PagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        obtainAttributes(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.view_pager_indicator, this);
            container = (LinearLayout) findViewById(R.id.pager_indicator_container);
            items = new ArrayList<ImageView>();
        }
    }

    private void obtainAttributes(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PagerIndicator);

        if (typedArray.hasValue(R.styleable.PagerIndicator_active_circle_drawable)) {
            activeCircleDrawable = typedArray.getDrawable(R.styleable.PagerIndicator_active_circle_drawable);
        } else {
            activeCircleDrawable = context.getResources().getDrawable(R.drawable.default_circle_indicator_active);
        }
        if (typedArray.hasValue(R.styleable.PagerIndicator_active_circle_drawable)) {
            inactiveCircleDrawable = typedArray.getDrawable(R.styleable.PagerIndicator_inactive_circle_drawable);
        } else {
            inactiveCircleDrawable = context.getResources().getDrawable(R.drawable.default_circle_indicator_inactive);
        }
        marginBetweenCircles = typedArray.getDimension(R.styleable.PagerIndicator_margin_between_circles, 0f);

        typedArray.recycle();
    }

    public void notifyDataSetChanged() {
        if (pager != null && pager.getAdapter() != null) {

            container.removeAllViews();

            int containerVisibility = VISIBLE;
            if (pager.getAdapter().getCount() < MIN_ITEMS_COUNT) {
                containerVisibility = GONE;
            }
            container.setVisibility(containerVisibility);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins((int) marginBetweenCircles, 0, 0, 0);

            items.removeAll(items);

            for (int i = 0; i < pager.getAdapter().getCount(); i++) {

                ImageView item = new ImageView(context);

                Drawable drawable = i == pager.getCurrentItem() ? activeCircleDrawable : inactiveCircleDrawable;

                item.setImageDrawable(drawable);
                item.setLayoutParams(lp);
                item.setTag(i);
                items.add(item);

                container.addView(item);
            }
        }
    }

    private void setCurrentItem(int position) {
        if (pager != null && pager.getAdapter() != null) {
            int numberOfItems = pager.getAdapter().getCount();

            for (int i = 0; i < numberOfItems; i++) {
                ImageView item = items.get(i);
                if (item != null) {
                    Drawable resource = i == position ? activeCircleDrawable : inactiveCircleDrawable;
                    item.setImageDrawable(resource);
                }
            }
        }
    }

    public ViewPager getPager() {
        return pager;
    }

    public void setPager(ViewPager pager) {
        this.pager = pager;
        this.pager.setOnPageChangeListener(this);

        notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        setCurrentItem(i);
        if(callback!=null)
            callback.onPageChanged(i);
    }


    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public void setCircleDrawables(Drawable active, Drawable inactive) {
        this.activeCircleDrawable = active;
        this.inactiveCircleDrawable = inactive;
        notifyDataSetChanged();
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        public void onPageChanged(int i);
    }
}
