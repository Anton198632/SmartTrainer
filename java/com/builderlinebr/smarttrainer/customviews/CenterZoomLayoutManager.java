package com.builderlinebr.smarttrainer.customviews;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CenterZoomLayoutManager extends LinearLayoutManager {

    private final float mShrinkAmount = 0.25f;//0.15
    private final float mShrinkDistance = 0.9f;//0.9

    public CenterZoomLayoutManager(Context context) {
        super(context);
    }

    private Context context;

    public CenterZoomLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.context = context;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {

        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);

            float midpoint = getWidth() / 2.f;//300; // getWidth() / 2.f; // центр
            float d0 = 1.f;
            float d1 = mShrinkDistance * midpoint;
            float s0 = 1f;// 1
            float s1 = 1.f - mShrinkAmount;

            float maxScale = 0;
            View maxChaild = new View(context);
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                float childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.f;//2

                float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                child.setScaleX(scale);
                child.setScaleY(scale);

                child.setAlpha(scale > 0.91 ? scale : scale / (scale + 2));

                if (scale > maxScale) {
                    maxScale = scale;
                    maxChaild = child;
                }


            }

//            TextView n = (TextView) maxChaild.findViewById(R.id.workout_exercise_name);
//            if (n!=null)
//            ((WorkoutExercisesActivity) context).exerciseHeaderTextView.setText(n.getText());

            return scrolled;
        } else {
            return 0;
        }
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);

        float midpoint = getWidth() / 2.f;//300; // getWidth() / 2.f; // центр
        float d0 = 0.f;
        float d1 = mShrinkDistance * midpoint;
        float s0 = 1.f;
        float s1 = 1.f - mShrinkAmount;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float childMidpoint =
                    (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.f;

            float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
            float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
            child.setScaleX(scale);
            child.setScaleY(scale);

        }

    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        scrollHorizontallyBy(0, recycler, state);
    }


}
