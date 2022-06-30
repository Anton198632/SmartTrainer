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
public class ExercisesFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    ImageView imageMan;
    ImageView imageGirl;
    TextView manText;
    TextView womanText;

    Context context;

    public ExercisesFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_navigation_exercises, container, false);

        imageMan = view.findViewById(R.id.imageMen);
        imageGirl = view.findViewById(R.id.imageGirl);
        manText = view.findViewById(R.id.man_text);
        womanText = view.findViewById(R.id.woman_text);



        TranslateAnimation translateAnimation = new TranslateAnimation(-1000, 80, 0, 0);
        translateAnimation.setAnimationListener(new TranslaterAnim(imageMan, 80, 0, 0, 0, 800));
        translateAnimation.setDuration(800);

        TranslateAnimation translateAnimation2 = new TranslateAnimation(1000, -80, 0, 0);
        translateAnimation2.setAnimationListener(new TranslaterAnim(imageGirl, -80, 0, 0, 0, 800));
        translateAnimation2.setDuration(800);

        imageMan.startAnimation(translateAnimation);
        manText.startAnimation(translateAnimation2);
        imageGirl.startAnimation(translateAnimation2);
        womanText.startAnimation(translateAnimation);


        imageMan.setOnClickListener(this);
        imageMan.setOnTouchListener(this);
        manText.setOnClickListener(this);
        imageGirl.setOnClickListener(this);
        imageGirl.setOnTouchListener(this);
        womanText.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        String sex = "";
        if (v.getId() == R.id.imageMen || v.getId() == R.id.man_text) {
            sex = "male";
        } else {
            sex = "female";
        }
        Intent intent = new Intent(context, ExercisesActivity.class);
        intent.putExtra("sex", sex);
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
