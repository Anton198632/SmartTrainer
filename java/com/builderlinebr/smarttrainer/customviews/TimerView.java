package com.builderlinebr.smarttrainer.customviews;

import android.content.Context;
import android.graphics.*;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.builderlinebr.smarttrainer.R;

public class TimerView extends View {

    private int height, width = 0;
    private int padding = 0;
    private int fontSize = 0;
    private int numeralSpacing = 0;
    private int handTruncation, hourHandTruncation = 0;
    private int radius = 0;
    private Paint paint;
    private boolean isInit;
    private Rect rect = new Rect();

    private double seconds = 0;
    private double mins = 0;


    // https://www.ssaurel.com/blog/learn-to-draw-an-analog-clock-on-android-with-the-canvas-2d-api/

    public TimerView(Context context) {
        super(context);
    }

    public TimerView(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimerView(Context context,AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initClock() {
        height = getHeight();
        width = getWidth();
        padding = numeralSpacing + 50;
        fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13,
                getResources().getDisplayMetrics());
        int min = Math.min(height, width);
        radius = min / 2 - padding;
        handTruncation = min / 20;
        hourHandTruncation = min / 7;
        paint = new Paint();
        isInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initClock();
        }
        canvas.drawColor(Color.TRANSPARENT);
        paint.reset();
        paint.setAntiAlias(true);
        drawAll(canvas);

        invalidate();
    }

    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }

    public void setMins(double mins) {
        this.mins = mins;
    }

    private void drawAll(Canvas canvas) {

        // Рисуем серые черные риски
        serifDrawer(canvas, Color.GRAY, 300, 10, 1.15f);
        serifDrawer(canvas, Color.GRAY, 60, 15, 1.15f);
        serifDrawer(canvas, Color.BLACK, 12, 20, 1.15f);

        serifDrawer(canvas, Color.GRAY, 150, 10, 0.55f);
        serifDrawer(canvas, Color.GRAY, 30, -4, 0.55f);
        serifDrawer(canvas, Color.BLACK, 6, 10, 0.55f);
        serifDrawer(canvas, Color.BLACK, 6, -8, 0.55f);

        // Рисуем цифры
        int[] numbers1 = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 0};
        numbersDrawer(canvas, Color.BLACK, numbers1, fontSize, 1f);

        int[] numbers2 = {5, 10, 15, 20, 25, 30};
        numbersDrawer(canvas, Color.GRAY, numbers2, fontSize - 4, 0.68f);

        // Рисуем стрелки

        drawHand(canvas, seconds, false);
        drawCenter(canvas, 15);
        drawHand(canvas, mins, true);
        drawCenter(canvas, 12);

//        for (int number : numbers) {
//            String tmp = String.valueOf(number);
//            paint.getTextBounds(tmp, 0, tmp.length(), rect);
//            double angle = Math.PI / (numbers.length / 2) * (number - (numbers.length / 4));
//            int x = (int) (width / 2 + Math.cos(angle) * radius - rect.width() / 2);
//            int y = (int) (height / 2 + Math.sin(angle) * radius + rect.height() / 2);
//            canvas.drawText(tmp, x, y, paint);
//        }

    }


    private void serifDrawer(Canvas canvas, int color, int count, int size, float podgon) {// рисовальщик засечек
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);

        for (int i = 1; i <= count; i++) {

            double angle = Math.PI / (count / 2f) * (i - (count / 4f));
            double x = (width / 2 - Math.cos(angle) * radius * podgon);// - rect.width() / 2
            double y = (height / 2 - Math.sin(angle) * radius * podgon); // + rect.height() / 2

            double b = size * Math.cos(angle);
            double a = size * Math.sin(angle);

            Path path = new Path();
            path.moveTo((float) x, (float) y);
            path.lineTo((float) (x + b), (float) (y + a));
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private void numbersDrawer(Canvas canvas, int color, int[] numbers, int fontSize, float podgon) {// рисовальщик номеров
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(fontSize);
        paint.setColor(color);

        int i = 0;
        for (int number : numbers) {
            i++;
            String tmp = String.valueOf(number);
            paint.getTextBounds(tmp, 0, tmp.length(), rect);
            double angle = Math.PI / (numbers.length / 2f) * (i - (numbers.length / 4f));
            int r = rect.width();
            int x = (int) (width / 2 + (Math.cos(angle) * radius - rect.width() / 2f) * podgon - (numbers.length < 10 ? 3 : 0));
            int y = (int) (height / 2 + (Math.sin(angle) * radius + rect.height() / 2f) * podgon);
            canvas.drawText(tmp, x, y, paint);


        }
    }

    private void drawHand(Canvas canvas, double loc, boolean isMin) {


        paint.setStrokeWidth(!isMin ? 3 : 5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(!isMin ? getResources().getColor(R.color.colorPrimary) : Color.RED);

        loc = loc * (!isMin ? 1 : 2);

        double angle = Math.PI * loc / 30 - Math.PI / 2;
        int handRadius = !isMin ? radius + padding : (radius + padding) / 2;  //radius - handTruncation - hourHandTruncation : radius - handTruncation;

        int centrRadus = 0;

        canvas.drawLine((float) (width / 2 + Math.cos(angle) * centrRadus), (float) (height / 2 + Math.cos(angle) * centrRadus),
                (float) (width / 2 + Math.cos(angle) * handRadius),
                (float) (height / 2 + Math.sin(angle) * handRadius),
                paint);
    }

    private void drawCenter(Canvas canvas, int radius) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, radius, paint);
    }
}
