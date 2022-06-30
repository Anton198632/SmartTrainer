package com.builderlinebr.smarttrainer.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;


@SuppressLint("AppCompatCustomView")
public class CustomImageViewRadius extends ImageView {

    private float radius = 10.0f;
    private Path path;
    private RectF rect;

    public CustomImageViewRadius(Context context) {
        super(context);
        init();
    }

    public CustomImageViewRadius(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomImageViewRadius(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        path = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        path.addRoundRect(rect, radius, radius, Path.Direction.CCW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}