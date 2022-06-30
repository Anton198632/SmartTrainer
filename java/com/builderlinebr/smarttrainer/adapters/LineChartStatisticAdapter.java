package com.builderlinebr.smarttrainer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.R;
import com.builderlinebr.smarttrainer.StatisticActivity;
import com.builderlinebr.smarttrainer.model.exercises.ExerciseEventStatistic;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LineChartStatisticAdapter extends RecyclerView.Adapter<LineChartStatisticAdapter.ViewHolder> {

    List<ExerciseEventStatistic> items;
    Context context;

    public LineChartStatisticAdapter(List<ExerciseEventStatistic> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LineChartStatisticAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.line_chart_statictic_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(items.get(position).exercise.getHeader());
        getImageFromFile(items.get(position).exercise.getImageMale() + ".jpg", holder.exerciseIm);

        if (items.get(position).dateEvents.size()>0) {
            lineChartBuilder(holder.lineChartCount, items.get(position), StatisticActivity.TYPE_COUNT);
            lineChartBuilder(holder.lineChartValue, items.get(position), StatisticActivity.TYPE_VALUE);
            lineChartBuilder(holder.lineChartAll, items.get(position), StatisticActivity.TYPE_ALL);
        } else {
            holder.textView.setVisibility(View.GONE);
            holder.lineChartCount.setVisibility(View.GONE);
            holder.lineChartValue.setVisibility(View.GONE);
            holder.lineChartAll.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        LineChart lineChartCount;
        LineChart lineChartValue;
        LineChart lineChartAll;
        CircleImageView exerciseIm;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.graph_title_exercise_name);
            lineChartCount = itemView.findViewById(R.id.line_chart_count);
            lineChartValue = itemView.findViewById(R.id.line_chart_value);
            lineChartAll = itemView.findViewById(R.id.line_chart_all);
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

    public void lineChartBuilder(LineChart lineChart, ExerciseEventStatistic exerciseEventStatistic, int type) {

        lineChart.setTouchEnabled(false);
        lineChart.getLegend().setWordWrapEnabled(true); // перенос меток по строкам
        lineChart.setPinchZoom(true);
        //  lineChart.getXAxis().setEnabled(false); // Убираем вертикальную разметку на графике
        //lineChart.getDescription().setEnabled(false);


        ArrayList<Entry> values = new ArrayList<>();

        ExerciseEventStatistic.DateEvent dateEvent = exerciseEventStatistic.dateEvents.get(exerciseEventStatistic.dateEvents.size() - 1);
        List<Float> floats = new ArrayList<>();
        List<Float> floats2 = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        String description = "";


        // Предыдущая тренировка

        if (exerciseEventStatistic.dateEvents.size() > 1) {
            ArrayList<Entry> values2 = new ArrayList<>();
            ExerciseEventStatistic.DateEvent dateEvent2 = exerciseEventStatistic.dateEvents.get(exerciseEventStatistic.dateEvents.size() - 2);

            switch (type) {
                case StatisticActivity.TYPE_COUNT:
                    for (Float count : dateEvent2.counts) {
                        floats2.add(count);
                    }
                    break;
                case StatisticActivity.TYPE_VALUE:
                    for (Float v : dateEvent2.values) {
                        floats2.add(v);
                    }
                    break;
                case StatisticActivity.TYPE_ALL:
                    for (int i = 0; i < dateEvent2.counts.size(); i++) {
                        floats2.add(dateEvent2.counts.get(i) * (dateEvent2.values.get(i) > 0 ? dateEvent2.values.get(i) : 1));
                    }
                    break;
            }

            int i = 1;
            for (Float aFloat : floats2) {
                values2.add(new Entry(i, aFloat));
                i++;
            }

            LineDataSet set2 = new LineDataSet(values2, "Предыдущая тренировка");
            set2.setDrawIcons(false);
            set2.enableDashedLine(10f, 5f, 0f); // использовать пунктирную линию (10 - длинна пунктира, 5 - длинна пустоты
            set2.enableDashedHighlightLine(10f, 5f, 0f);// использовать пунктирную линию
            set2.setColor(Color.GRAY);
            set2.setCircleColor(Color.GRAY);
            set2.setLineWidth(1f);
            set2.setCircleRadius(3f);
            set2.setDrawCircleHole(false);
            set2.setValueTextSize(9f);


            dataSets.add(set2);
        }


        // Текущая тренировка
        switch (type) {
            case StatisticActivity.TYPE_COUNT:
                for (Float count : dateEvent.counts) {
                    floats.add(count);
                }
                description = "Количество в подходе";
                break;
            case StatisticActivity.TYPE_VALUE:
                for (Float v : dateEvent.values) {
                    floats.add(v);
                }
                description = "Значение (вес) в подходе";
                break;
            case StatisticActivity.TYPE_ALL:
                for (int i = 0; i < dateEvent.counts.size(); i++) {
                    floats.add(dateEvent.counts.get(i) * (dateEvent.values.get(i) > 0 ? dateEvent.values.get(i) : 1));
                    description = "Значение*количество в подходе";
                }
                break;
        }


        Description d = new Description();
        d.setText(description);
        d.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        lineChart.setDescription(d);

        int i = 1;
        for (Float aFloat : floats) {
            values.add(new Entry(i, aFloat));
            i++;
        }

        LineDataSet set1 = new LineDataSet(values, "Текущая тренировка");
        set1.setDrawIcons(false);
        set1.enableDashedLine(10f, 5f, 0f); // использовать пунктирную линию (10 - длинна пунктира, 5 - длинна пустоты
        set1.enableDashedHighlightLine(10f, 5f, 0f);// использовать пунктирную линию
        set1.setColor(Color.DKGRAY);
        set1.setCircleColor(Color.DKGRAY);
        float diff = 0;
        if (floats2.size() > 0) {
            for (int j = 0; j < floats.size(); j++) {
                if (floats2.size()>j)
                diff += floats.get(j) - floats2.get(j);
                else diff += floats.get(j);
            }
        }

        if (diff > 0) {
            set1.setColor(Color.parseColor("#FF2E9417"));
            set1.setCircleColor(Color.parseColor("#FF2E9417"));
        } else if (diff < 0) {
            set1.setColor(Color.RED);
            set1.setCircleColor(Color.RED);
        }


        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);


        dataSets.add(set1);


        LineData data = new LineData(dataSets);
        lineChart.setData(data);

//        lineChart.getAxisLeft().setAxisMinimum(0f);
//        lineChart.getAxisRight().setAxisMinimum(0f);

        lineChart.getXAxis().setSpaceMin(0.5f);
        lineChart.getXAxis().setSpaceMax(0.5f);
        lineChart.getXAxis().setAvoidFirstLastClipping(true);
        lineChart.getXAxis().setLabelCount(values.size(), true); // Ставим количество вертикальных меток


    }
}
