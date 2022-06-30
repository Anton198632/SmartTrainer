package com.builderlinebr.smarttrainer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.adapters.StatisticCountAdapter;
import com.builderlinebr.smarttrainer.adapters.StatisticExerciseFullAdapter;
import com.builderlinebr.smarttrainer.calculation.CalcStatistic;
import com.builderlinebr.smarttrainer.model.exercises.ExerciseEventStatistic;

import java.util.List;

@SuppressLint("ValidFragment")
public class FragmentStatisticTables extends Fragment {

    Context context;
    RecyclerView recyclerViewCount;
    RecyclerView recyclerViewValue;
    RecyclerView recyclerViewAverageValue;
    View view;

    RecyclerView exerciseStatisticTable;
    LinearLayout waitLayout;

    public FragmentStatisticTables(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_statistic_tables, container, false);
        waitLayout = view.findViewById(R.id.wait_layout);
        waitLayout.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() { // Для получеиня данных в потоке и построение таблиц
            @Override
            public void run() {
                initTables();
                waitLayout.setVisibility(View.GONE);
            }
        });






        return view;
    }


    private void initTables(){
        recyclerViewCount = view.findViewById(R.id.table_count);
        recyclerViewValue = view.findViewById(R.id.table_value);
        recyclerViewAverageValue = view.findViewById(R.id.table_average_value);


        recyclerViewCount.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewValue.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewAverageValue.setLayoutManager(new LinearLayoutManager(context));

        StatisticCountAdapter adapter = new StatisticCountAdapter(context, StatisticCountAdapter.TYPE_COUNT_ALL);
        recyclerViewCount.setAdapter(adapter);
        StatisticCountAdapter adapter1 = new StatisticCountAdapter(context, StatisticCountAdapter.TYPE_VALUE_ALL);
        recyclerViewValue.setAdapter(adapter1);
        StatisticCountAdapter adapter2 = new StatisticCountAdapter(context, StatisticCountAdapter.TYPE_AVERAGE_VALUE);
        recyclerViewAverageValue.setAdapter(adapter2);

        // ----- Таблицы упражнений
        exerciseStatisticTable = view.findViewById(R.id.exercise_statistic_tables);
        exerciseStatisticTable.setLayoutManager(new LinearLayoutManager(context));

        List<ExerciseEventStatistic> exEventStat = new CalcStatistic().getExerciseEventStatistic(
                ((StatisticActivity)context).workoutExercises,
                ((StatisticActivity)context).exercises,
                ((StatisticActivity)context).events);

        StatisticExerciseFullAdapter fullAdapter = new StatisticExerciseFullAdapter(exEventStat, context);
        exerciseStatisticTable.setAdapter(fullAdapter);



    }


}
