package com.builderlinebr.smarttrainer.dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.customviews.BubbleButton;

public class ExercisesInfoDialogFragment extends DialogFragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.exercises_info_dialog_fragment, null);
        ((Button) view.findViewById(R.id.button_site)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.button_site)).setOnTouchListener(new BubbleButton().onTouchListener);

        return view;

    }

    @Override
    public void onClick(View v) {
        Uri page = Uri.parse("https://dailyfit.ru");
        Intent intent = new Intent(Intent.ACTION_VIEW, page); // ACTION_VIEW - для просмотра
        if (intent.resolveActivity((getContext().getPackageManager())) != null) {
            startActivity(intent);
        }
        getDialog().cancel();

    }
}
