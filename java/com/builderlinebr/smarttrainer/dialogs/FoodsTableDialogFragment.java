package com.builderlinebr.smarttrainer.dialogs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.NumberPicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.builderlinebr.smarttrainer.R;

@SuppressLint("ValidFragment")
public class FoodsTableDialogFragment extends DialogFragment implements NumberPicker.OnValueChangeListener {

    NumberPicker np0;
    NumberPicker np1;
    NumberPicker np2;

    public int value = 100;
    private String count = "";

    public FoodsTableDialogFragment(String count) {
        this.count = count.substring(0, count.indexOf(" "));
        switch (count.length()) {
            case 1:
                this.count = "00" + this.count;
                break;
            case 2:
                this.count = "0" + this.count;
                break;
        }
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.food_table_dialog_fragment, null);

        np0 = view.findViewById(R.id.food_count_picker_0);
        np0.setMinValue(0);
        np0.setMaxValue(9);
        np0.setValue(Integer.parseInt(count.substring(0, 1)));
        np0.setOnValueChangedListener(this);

        np1 = view.findViewById(R.id.food_count_picker_1);
        np1.setMinValue(0);
        np1.setMaxValue(9);
        np1.setValue(Integer.parseInt(count.substring(1, 2)));
        np1.setOnValueChangedListener(this);

        np2 = view.findViewById(R.id.food_count_picker_2);
        np2.setMinValue(0);
        np2.setMaxValue(9);
        np2.setValue(Integer.parseInt(count.substring(2)));
        np2.setOnValueChangedListener(this);

        return view;

    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        value = np0.getValue() * 100 + np1.getValue() * 10 + np2.getValue();
    }
}
