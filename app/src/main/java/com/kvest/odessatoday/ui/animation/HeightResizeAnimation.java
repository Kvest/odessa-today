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
    private int finalHeight;

    public HeightResizeAnimation(View targetView, int startHeight, int finishHeight, int finalHeight) {
        this.targetView = targetView;
        this.startHeight = startHeight;
        this.finishHeight = finishHeight;
        this.finalHeight = finalHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int height = (interpolatedTime == 1) ? finalHeight :
                                               startHeight + (int)((finishHeight - startHeight) * interpolatedTime);
        targetView.getLayoutParams().height = height;
        targetView.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
