package com.builderlinebr.smarttrainer.animation;

import android.view.View;
import android.view.animation.Animation;



public class AlphaAnim implements Animation.AnimationListener {

    View view;


    public AlphaAnim(View view) {
        this.view = view;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        view.animate().alpha(1f).setDuration(100).start();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
