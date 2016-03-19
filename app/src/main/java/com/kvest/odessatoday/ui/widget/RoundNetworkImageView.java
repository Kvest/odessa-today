package com.kvest.odessatoday.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.kvest.odessatoday.R;

/**
 * Created by kvest on 30.08.15.
 */
public class RoundNetworkImageView extends NetworkImageView {
    private Paint paint;
    private RectF rect;
    private float radius;

    public RoundNetworkImageView(Context context) {
        super(context);

        init(context, null);
    }

    public RoundNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public RoundNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    public float getCornersRadius() {
        return radius;
    }

    public void setCornersRadius(float radius) {
        this.radius = radius;
    }

    public void setCornersRadiusResource(int resId) {
        this.radius = getResources().getDimension(resId);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);

        rect = new RectF(0.0f, 0.0f, getWidth(), getHeight());

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);

            try {
                radius = a.getDimension(R.styleable.RoundImageView_cornerRadius, 0.0f);
            }finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();

        if (radius > 0 && drawable != null) {
            Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
            if (bm == null) {
                return;
            }

            BitmapShader shader = new BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            paint.setShader(shader);

            int saveCount = -1;
            if (getScaleType() == ScaleType.CENTER) {
                saveCount = canvas.save();
                float dx = Math.max(0.0f, (getWidth() - bm.getWidth()) / 2);
                float dy = Math.max(0.0f, (getHeight() - bm.getHeight()) / 2);
                canvas.translate(dx, dy);
            }

            rect.set(0.0f, 0.0f, Math.min(getWidth(), bm.getWidth()), Math.min(getHeight(), bm.getHeight()));

            // rect contains the bounds of the shape
            // radius is the radius in pixels of the rounded corners
            // paint contains the shader that will texture the shape
            canvas.drawRoundRect(rect, radius, radius, paint);

            if (getScaleType() == ScaleType.CENTER) {
                canvas.restoreToCount(saveCount);
            }
        } else {
            super.onDraw(canvas);
        }
    }
}
