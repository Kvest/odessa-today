package com.kvest.odessatoday.ui.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by kvest on 18.08.16.
 */
public class HeightResizeAnimation extends Animation {
    private View targetView;
    private int startHeight;
    private int finishHeight;

    public HeightResizeAnimation(View targetView, int startHeight, int finishHeight) {
        this.targetView = targetView;
        this.startHeight = startHeight;
        this.finishHeight = finishHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        targetView.getLayoutParams().height = startHeight + (int)((finishHeight - startHeight) * interpolatedTime);
        targetView.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
