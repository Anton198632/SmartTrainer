package com.builderlinebr.smarttrainer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.builderlinebr.smarttrainer.animation.TranslaterAnim;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;

@SuppressLint("ValidFragment")
public class WorkoutFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    ImageView imageWorkoutList;
    ImageView imageWorkoutMy;
    TextView workoutListText;
    TextView workoutMyText;

    Context context;

    public WorkoutFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_navigation_workout, container, false);
        this.imageWorkoutList = view.findViewById(R.id.imageWorkoutList);
        this.imageWorkoutMy = view.findViewById(R.id.imageMyWorkout);
        this.workoutListText = view.findViewById(R.id.workout_list_text);
        this.workoutMyText = view.findViewById(R.id.workout_my_text);


        TranslateAnimation translateAnimation = new TranslateAnimation(-1000, 80, 0, 0);
        translateAnimation.setAnimationListener(new TranslaterAnim(imageWorkoutList, 80, 0, 0, 0, 800));
        translateAnimation.setDuration(800);

        TranslateAnimation translateAnimation2 = new TranslateAnimation(1000, -80, 0, 0);
        translateAnimation2.setAnimationListener(new TranslaterAnim(imageWorkoutMy, -80, 0, 0, 0, 800));
        translateAnimation2.setDuration(800);

        imageWorkoutList.startAnimation(translateAnimation);
        workoutListText.startAnimation(translateAnimation2);
        imageWorkoutMy.startAnimation(translateAnimation2);
        workoutMyText.startAnimation(translateAnimation);


        imageWorkoutList.setOnClickListener(this);
        imageWorkoutList.setOnTouchListener(this);
        workoutListText.setOnClickListener(this);
        imageWorkoutMy.setOnClickListener(this);
        imageWorkoutMy.setOnTouchListener(this);
        workoutMyText.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int isMy = 0;
        if (v.getId()==R.id.imageMyWorkout || v.getId()== R.id.workout_my_text){
            isMy = 1;
        }
        Intent intent = new Intent(context, MyWorkoutsActivity.class);
        intent.putExtra("isMy", isMy);
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: // если нажата на view
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // Проверка версии андройд >= 5
                    new BubbleButton().show(v, (int) event.getX(), (int) event.getY(), 100);
                }
                break;
            case MotionEvent.ACTION_UP: // если отжата view
                v.setVisibility(View.VISIBLE);
        }


        return false;
    }
}