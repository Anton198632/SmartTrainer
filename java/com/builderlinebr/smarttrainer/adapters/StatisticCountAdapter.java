package com.builderlinebr.smarttrainer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.StatisticActivity;
import com.builderlinebr.smarttrainer.model.exercises.ExerciseStatistic;

import java.util.ArrayList;
import java.util.List;

public class StatisticCountAdapter extends RecyclerView.Adapter<StatisticCountAdapter.ViewHolder> {

    public static final int TYPE_COUNT_ALL = 0;
    public static final int TYPE_VALUE_ALL = 1;
    public static final int TYPE_AVERAGE_VALUE = 2;


    Context context;
    List<ExerciseStatistic> exercisesStatistic;
    List<Float> counts;
    List<Float> oldCounts;


    public StatisticCountAdapter(Context context, int type) {
        this.context = context;
        exercisesStatistic = new ArrayList<>();

        for (ExerciseStatistic exerciseStat : ((StatisticActivity) context).exercisesStatistic) {
            exercisesStatistic.add(exerciseStat);
        }


        counts = new ArrayList<>();
        oldCounts = new ArrayList<>();

        switch (type) {
            case TYPE_COUNT_ALL:
                for (int i = 0; i < exercisesStatistic.size(); i++) {
                    counts.add((float) exercisesStatistic.get(i).countAll);
                    if (((StatisticActivity) context).oldExercisesStatistic != null){
                        if (((StatisticActivity) context).oldExercisesStatistic.get(i).countAll != Float.NaN)
                            oldCounts.add((float) ((StatisticActivity) context).oldExercisesStatistic.get(i).countAll);
                        else oldCounts.add(0f);
                    } else oldCounts.add(0f);
                }
                break;
            case TYPE_VALUE_ALL:
                for (int i = 0; i < exercisesStatistic.size(); i++) {
                    counts.add((float) exercisesStatistic.get(i).valueAll);
                    if (((StatisticActivity) context).oldExercisesStatistic != null) {
                        if (((StatisticActivity) context).oldExercisesStatistic.get(i).valueAll != Float.NaN)
                            oldCounts.add((float) ((StatisticActivity) context).oldExercisesStatistic.get(i).valueAll);
                        else oldCounts.add(0f);
                    } else oldCounts.add(0f);
                }

                break;
            case TYPE_AVERAGE_VALUE:
                for (int i = 0; i < exercisesStatistic.size(); i++) {
                    counts.add((float) exercisesStatistic.get(i).averageValue);
                    if (((StatisticActivity) context).oldExercisesStatistic != null) {
                        if (((StatisticActivity) context).oldExercisesStatistic.get(i).averageValue != Float.NaN)
                            oldCounts.add((float) ((StatisticActivity) context).oldExercisesStatistic.get(i).averageValue);
                        else oldCounts.add(0f);
                    } else oldCounts.add(0f);
                }
                break;
        }

        exercisesStatistic.add(0, null);


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_table_count_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (exercisesStatistic.get(position) != null) {


            holder.exerciseNameTV.setText(exercisesStatistic.get(position).exercise.getHeader());
            holder.value1TV.setText(Float.toString(oldCounts.get(position - 1)));
            holder.value2TV.setText(Float.toString(counts.get(position - 1)));

            if (oldCounts.get(position - 1) < counts.get(position - 1)) {
                holder.value2TV.setBackgroundColor(Color.parseColor("#4003ff00")); // зеленый с прозрачностью 40
               // holder.value2TV.setTextColor(context.getResources().getColor(R.color.colorLiteText));
            } else if (oldCounts.get(position - 1) > counts.get(position - 1)) {
                holder.value2TV.setBackgroundColor(Color.parseColor("#40ff0000")); // красный с прозрачностью 40
               // holder.value2TV.setTextColor(context.getResources().getColor(R.color.colorLiteText));


            }
        } else {

            holder.itemView.setBackgroundColor(Color.parseColor("#40D0D0D0"));

            holder.exerciseNameTV.setText(context.getResources().getString(R.string.exercise_name));
            holder.exerciseNameTV.setTextColor(context.getResources().getColor(R.color.colorDarkText));
            holder.exerciseNameTV.setTextSize(10);

            holder.value1TV.setText(context.getResources().getString(R.string.pre_workout));
            holder.value1TV.setTextColor(context.getResources().getColor(R.color.colorDarkText));
            holder.value1TV.setTextSize(10);

            holder.value2TV.setText(context.getResources().getString(R.string.current_workout));
            holder.value2TV.setTextColor(context.getResources().getColor(R.color.colorDarkText));
            holder.value2TV.setTextSize(10);

        }


    }

    @Override
    public int getItemCount() {
        return exercisesStatistic.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseNameTV;
        TextView value1TV;
        TextView value2TV;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            exerciseNameTV = itemView.findViewById(R.id.exercise_name_tv);
            value1TV = itemView.findViewById(R.id.value_1);
            value2TV = itemView.findViewById(R.id.value_2);
        }
    }
}
