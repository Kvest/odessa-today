package com.kvest.odessatoday.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

import com.kvest.odessatoday.R;
import com.kvest.odessatoday.ui.animation.HeightResizeAnimation;

/**
 * Created by kvest on 15.08.16.
 */
public class OrderTicketsFragment extends BaseFragment {
    private static final float ANIMATION_ACCELERATION_FRACTION = 1.5f; //imperatively selected value
    private static final long ANIMATION_DURATION = 400;

    public static OrderTicketsFragment newInstance() {
        return new OrderTicketsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_tickets_fragmen, container, false);

        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        View view = getView().findViewById(R.id.order_panel);

        if (enter) {
            //calculate target height
            view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.getLayoutParams().height = 1;
        }

        int statHeight = enter ? 0 : view.getHeight();
        int finishHeight = enter ? view.getMeasuredHeight() : 0;
        Animation animation = new HeightResizeAnimation(view, statHeight, finishHeight);
        animation.setInterpolator(new AccelerateInterpolator(ANIMATION_ACCELERATION_FRACTION));
        animation.setDuration(ANIMATION_DURATION);

        return animation;
    }
}
