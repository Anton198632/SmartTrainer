package com.builderlinebr.smarttrainer.customviews;

import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;

public class ShadowButtonProvider extends ViewOutlineProvider {

    @Override
    public void getOutline(View view, Outline outline) {
        Rect rect = new Rect(0, 0, view.getWidth(), view.getHeight());
        outline.setRoundRect(rect, rect.width()/2f);
    }


}