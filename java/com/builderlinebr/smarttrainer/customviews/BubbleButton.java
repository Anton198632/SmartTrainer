package com.builderlinebr.smarttrainer.customviews;

import android.animation.Animator;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import androidx.annotation.RequiresApi;

public class BubbleButton {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) // Только для андрой 5 и выше
    public void show(View view, int cx, int cy, int duration) { // метод анимации расширения круга
        int maxRadius = view.getHeight() > view.getWidth() ? view.getHeight() : view.getWidth();
        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, maxRadius);
        animator.setDuration(duration);
        animator.start();
    }

    public View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_UP: // если отжата view
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // Проверка версии андройд >= 5
                        new BubbleButton().show(v, (int) event.getX(), (int) event.getY(), 300);
                    }
            }
            return false;
        }
    };
}
