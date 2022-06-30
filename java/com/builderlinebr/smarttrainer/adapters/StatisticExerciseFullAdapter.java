package com.builderlinebr.smarttrainer.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.model.exercises.ExerciseEventStatistic;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StatisticExerciseFullAdapter extends RecyclerView.Adapter<StatisticExerciseFullAdapter.ViewHolder> {


    List<ExerciseEventStatistic> items;
    Context context;


    public StatisticExerciseFullAdapter(List<ExerciseEventStatistic> items, Context context) {
        this.items = new ArrayList<>();
        for (ExerciseEventStatistic item : items) {
            this.items.add(item);
        }


        this.context = context;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_table_exercis_full_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        if (items.get(position) != null) {

            holder.textView.setText(items.get(position).exercise.getHeader());
            getImageFromFile(items.get(position).exercise.getImageMale() + ".jpg", holder.exerciseIm);


            List<Float> counts = new ArrayList<>();
            List<Float> countsOld = new ArrayList<>();
            List<Float> value = new ArrayList<>();
            List<Float> valueOld = new ArrayList<>();
            List<Float> valueCount = new ArrayList<>();
            List<Float> valueCountOld = new ArrayList<>();
            // Предыдущие значения
            if (items.get(position).dateEvents.size() > 1) {
                ExerciseEventStatistic.DateEvent dateEvent2 = items.get(position).dateEvents.get(items.get(position).dateEvents.size() - 2);
                for (Float count : dateEvent2.counts) {
                    countsOld.add(count);
                }
                for (Float v : dateEvent2.values) {
                    valueOld.add(v);
                }
                for (int i = 0; i < dateEvent2.counts.size(); i++) {
                    valueCountOld.add(dateEvent2.counts.get(i) * (dateEvent2.values.get(i) > 0 ? dateEvent2.values.get(i) : 1));
                }
            }
            // текущие значения
            if (items.get(position).dateEvents.size() > 0) {
                ExerciseEventStatistic.DateEvent dateEvent = items.get(position).dateEvents.get(items.get(position).dateEvents.size() - 1);
                for (Float count : dateEvent.counts) {
                    counts.add(count);
                }
                for (Float v : dateEvent.values) {
                    value.add(v);
                }
                for (int i = 0; i < dateEvent.counts.size(); i++) {
                    valueCount.add(dateEvent.counts.get(i) * (dateEvent.values.get(i) > 0 ? dateEvent.values.get(i) : 1));
                }
            }
            holder.recViewCount.setAdapter(
                    new StatisticExerciseAdapter(counts, countsOld, context)
            );
            holder.recViewValue.setAdapter(
                    new StatisticExerciseAdapter(value, valueOld, context)
            );
            holder.recViewCountAndValue.setAdapter(
                    new StatisticExerciseAdapter(valueCount, valueCountOld, context)
            );

        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recViewCount;
        RecyclerView recViewValue;
        RecyclerView recViewCountAndValue;
        TextView textView;
        CircleImageView exerciseIm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recViewCount = itemView.findViewById(R.id.rec_view_exercise_count);
            recViewValue = itemView.findViewById(R.id.rec_view_exercise_value);
            recViewCountAndValue = itemView.findViewById(R.id.rec_view_exercise_count_and_value);
            recViewCount.setLayoutManager(new LinearLayoutManager(context));
            recViewValue.setLayoutManager(new LinearLayoutManager(context));
            recViewCountAndValue.setLayoutManager(new LinearLayoutManager(context));

            textView = itemView.findViewById(R.id.graph_title_exercise_name);
            exerciseIm = itemView.findViewById(R.id.exercise_im);
        }
    }

    private void getImageFromFile(String imageName, ImageView imageView) {
        final String path = context.getExternalFilesDir("").getPath() + "/images/";
        if (!new File(path).exists()) {
            new File(path).mkdir();
        }
        File imgFile = new File(path + imageName);
        if (imgFile.exists()) {

            Uri uri = Uri.fromFile(imgFile);
            Picasso.get().load(uri).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
    }
}
