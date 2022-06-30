package com.builderlinebr.smarttrainer.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;

@SuppressLint("ValidFragment")
public class WorkoutExerciseInvkeDialogFragment extends DialogFragment implements NumberPicker.OnValueChangeListener, View.OnClickListener {

    NumberPicker cnp0;
    NumberPicker cnp1;
    NumberPicker cnp2;

    NumberPicker vnp0;
    NumberPicker vnp1;
    NumberPicker vnp2;
    NumberPicker vnp3;
    NumberPicker vnp4;


    private String count = "";
    public static int c;
    private String value = "";
    public static float v;

    public static final int DIALOG_RESULT_OK = 1;
    public static final int DIALOG_RESULT_CANCEL = 0;

    public static int dialogResult;

    public WorkoutExerciseInvkeDialogFragment(int count, float value){
        this.c = count;
        this.v = value;

        this.count = String.valueOf(count);
        if (this.count.length() == 2) this.count = "0" + this.count;
        if (this.count.length() == 1) this.count = "00" + this.count;


        String cel = String.valueOf((int)value);
        String dr = String.valueOf((int)((value - (int)value)*100));

        if (cel.length() == 2) cel = "0" + cel;
        if (cel.length() == 1) cel = "00" + cel;
        if (dr.length()==1) dr = "0" + dr;
        this.value = cel + dr;


        dialogResult = DIALOG_RESULT_CANCEL;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.workout_exercise_invoke_dialog_fragment, null);

        cnp0 = view.findViewById(R.id.count_picker_0);
        cnp0.setMinValue(0);
        cnp0.setMaxValue(9);
        cnp0.setValue(Integer.parseInt(count.substring(0, 1)));
        cnp0.setOnValueChangedListener(this);
        cnp1 = view.findViewById(R.id.count_picker_1);
        cnp1.setMinValue(0);
        cnp1.setMaxValue(9);
        cnp1.setValue(Integer.parseInt(count.substring(1, 2)));
        cnp1.setOnValueChangedListener(this);
        cnp2 = view.findViewById(R.id.count_picker_2);
        cnp2.setMinValue(0);
        cnp2.setMaxValue(9);
        cnp2.setValue(Integer.parseInt(count.substring(2)));
        cnp2.setOnValueChangedListener(this);

        vnp0 = view.findViewById(R.id.value_picker_0);
        vnp0.setMinValue(0);
        vnp0.setMaxValue(9);
        vnp0.setValue(Integer.parseInt(value.substring(0, 1)));
        vnp0.setOnValueChangedListener(this);
        vnp1 = view.findViewById(R.id.value_picker_1);
        vnp1.setMinValue(0);
        vnp1.setMaxValue(9);
        vnp1.setValue(Integer.parseInt(value.substring(1, 2)));
        vnp1.setOnValueChangedListener(this);
        vnp2 = view.findViewById(R.id.value_picker_2);
        vnp2.setMinValue(0);
        vnp2.setMaxValue(9);
        vnp2.setValue(Integer.parseInt(value.substring(2, 3)));
        vnp2.setOnValueChangedListener(this);
        vnp3 = view.findViewById(R.id.value_picker_3);
        vnp3.setMinValue(0);
        vnp3.setMaxValue(9);
        vnp3.setValue(Integer.parseInt(value.substring(3, 4)));
        vnp3.setOnValueChangedListener(this);
        vnp4 = view.findViewById(R.id.value_picker_4);
        vnp4.setMinValue(0);
        vnp4.setMaxValue(9);
        vnp4.setValue(Integer.parseInt(value.substring(4)));
        vnp4.setOnValueChangedListener(this);


        ((TextView)view.findViewById(R.id.invoke_ok)).setOnClickListener(this);
        ((TextView)view.findViewById(R.id.invoke_ok)).setOnTouchListener(new BubbleButton().onTouchListener);
        ((TextView)view.findViewById(R.id.invoke_cancel)).setOnClickListener(this);
        ((TextView)view.findViewById(R.id.invoke_cancel)).setOnTouchListener(new BubbleButton().onTouchListener);


        return view;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        c = cnp0.getValue() * 100 + cnp1.getValue() * 10 + cnp2.getValue();
        v = vnp0.getValue() * 100 + vnp1.getValue() * 10 + vnp2.getValue() + vnp3.getValue()*0.1f + vnp4.getValue()*0.01f;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.invoke_ok:
                dialogResult = DIALOG_RESULT_OK;
                break;
            case R.id.invoke_cancel:
                dialogResult = DIALOG_RESULT_CANCEL;
                break;
        }
        getDialog().cancel();
    }
}
