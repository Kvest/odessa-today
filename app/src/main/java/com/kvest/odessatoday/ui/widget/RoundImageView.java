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
import android.widget.ImageView;

import com.kvest.odessatoday.R;

/**
 * Created by roman on 11/4/15.
 */
public class RoundImageView extends ImageView {
    private Paint paint;
    private RectF rect;
    private float radius;

    public RoundImageView(Context context) {
        super(context);

        init(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
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

            BitmapShader shader = new BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            paint.setShader(shader);

            rect.set(0.0f, 0.0f, getWidth(), getHeight());

            // rect contains the bounds of the shape
            // radius is the radius in pixels of the rounded corners
            // paint contains the shader that will texture the shape
            canvas.drawRoundRect(rect, radius, radius, paint);
        } else {
            super.onDraw(canvas);
        }
    }
}
