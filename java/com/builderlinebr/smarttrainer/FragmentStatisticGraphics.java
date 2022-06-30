package com.builderlinebr.smarttrainer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.adapters.LineChartStatisticAdapter;
import com.builderlinebr.smarttrainer.calculation.CalcStatistic;
import com.builderlinebr.smarttrainer.model.exercises.ExerciseEventStatistic;
import com.builderlinebr.smarttrainer.model.exercises.ExerciseStatistic;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class FragmentStatisticGraphics extends Fragment {

    public static final int TYPE_COUNT = 0;
    public static final int TYPE_VALUE = 1;
    public static final int TYPE_INTERVAL = 2;
    public static final int TYPE_AVERAGE = 3;
    public static final int TYPE_ALL = 4;

    Context context;


    RecyclerView recyclerView;
    public int centralPosition;

    BarChart barChart0;
    TextView graphTitle0;
    BarChart barChart1;
    TextView graphTitle1;
    BarChart barChart2;
    TextView graphTitle2;

    LineChart lineChart;
    public RecyclerView linerChartRecyclerView;

    View view;

    LinearLayout waitLayout;

    public FragmentStatisticGraphics(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_statistic_graphics, container, false);

        waitLayout = view.findViewById(R.id.wait_layout);
        waitLayout.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Построение графиков
                graphicsBuild();
                waitLayout.setVisibility(View.GONE);
            }
        });


        return view;
    }


    private void graphicsBuild() {
        // ----- Построение графиков

        graphTitle0 = view.findViewById(R.id.graph_title0);
        graphTitle0.setText(context.getResources().getString(R.string.count_repeat));
        barChart0 = view.findViewById(R.id.bar_chart0);
        List<Float> counts = new ArrayList<>();
        List<Float> oldCounts = new ArrayList<>();
        for (int i = 0; i < ((StatisticActivity) context).exercisesStatistic.size(); i++) {
            counts.add((float) ((StatisticActivity) context).exercisesStatistic.get(i).countAll);
            if (((StatisticActivity) context).oldExercisesStatistic != null)
                oldCounts.add((float) ((StatisticActivity) context).oldExercisesStatistic.get(i).countAll);
        }
        barChartBuilder(barChart0, counts, oldCounts, ((StatisticActivity) context).exercisesStatistic);

        graphTitle1 = view.findViewById(R.id.graph_title1);
        graphTitle1.setText(context.getResources().getString(R.string.count_value));
        barChart1 = view.findViewById(R.id.bar_chart1);
        List<Float> values = new ArrayList<>();
        List<Float> oldValues = new ArrayList<>();
        for (int i = 0; i < ((StatisticActivity) context).exercisesStatistic.size(); i++) {
            values.add((float) ((StatisticActivity) context).exercisesStatistic.get(i).valueAll);
            if (((StatisticActivity) context).oldExercisesStatistic != null)
                oldValues.add((float) ((StatisticActivity) context).oldExercisesStatistic.get(i).valueAll);
        }
        barChartBuilder(barChart1, values, oldValues, ((StatisticActivity) context).exercisesStatistic);

        graphTitle2 = view.findViewById(R.id.graph_title2);
        graphTitle2.setText(context.getResources().getString(R.string.average_value));
        barChart2 = view.findViewById(R.id.bar_chart2);
        List<Float> averageValues = new ArrayList<>();
        List<Float> oldAverageValues = new ArrayList<>();
        for (int i = 0; i < ((StatisticActivity) context).exercisesStatistic.size(); i++) {
            averageValues.add((float) ((StatisticActivity) context).exercisesStatistic.get(i).averageValue);
            if (((StatisticActivity) context).oldExercisesStatistic != null)
                oldAverageValues.add((float) ((StatisticActivity) context).oldExercisesStatistic.get(i).averageValue);
        }
        barChartBuilder(barChart2, averageValues, oldAverageValues, ((StatisticActivity) context).exercisesStatistic);

        // ----- Построение линейного графика
        List<ExerciseEventStatistic> exEventStat = new CalcStatistic().getExerciseEventStatistic(
                ((StatisticActivity) context).workoutExercises,
                ((StatisticActivity) context).exercises,
                ((StatisticActivity) context).events);
        // lineChart = findViewById(R.id.line_chart);
        // lineChartBuilder(lineChart, exEventStat.get(0), TYPE_COUNT);
        linerChartRecyclerView = view.findViewById(R.id.line_chart_recycler_view);
        linerChartRecyclerView.setHasFixedSize(true); // для постоянных списков для лучшей производительности
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linerChartRecyclerView.setLayoutManager(linearLayoutManager);
        LineChartStatisticAdapter lineChartAdapter = new LineChartStatisticAdapter(exEventStat, context);
        linerChartRecyclerView.setAdapter(lineChartAdapter);

        linerChartRecyclerView.setNestedScrollingEnabled(false);


    }


    public void barChartBuilder(BarChart barChart, List<Float> v, List<Float> oldVs, List<ExerciseStatistic> statisticList) {

        // -----  Построение графика ---------------------------------------------------------


        barChart.getXAxis().setEnabled(false); // Убираем вертикальную разметку на графике
        barChart.getDescription().setEnabled(false);

        // Формируем список значений для графика
        ArrayList noOfEmp = new ArrayList();
        int i = 1;
        for (float value : v) {
            noOfEmp.add(new BarEntry(i, value));
            i++;
        }

        BarDataSet dataSet = new BarDataSet(noOfEmp, null);
        int[] colors = new int[v.size()];
        int step = 16711680 / (v.size());
        for (int c = 0; c < v.size(); c++) {
            int currentColorInt = -16711680 + step * (c);
            colors[c] = currentColorInt;
        }

        dataSet.setColors(colors);

        ArrayList noOfEmp1 = new ArrayList();
        i = 1;
        for (Float oldV : oldVs) {
            noOfEmp1.add(new BarEntry(i, oldV));
            i++;
        }

        BarDataSet dataSet1 = new BarDataSet(noOfEmp1, null);
        int[] colors1 = new int[v.size()];
        for (int c = 0; c < v.size(); c++) {
            // Делаем прозрачность для цвета
            String hex = Integer.toHexString(colors[c]);
            hex = "40" + hex.substring(2);
            int cc = Color.parseColor("#" + hex);
            colors1[c] = cc;
        }
        dataSet1.setColors(colors1);


        BarData data = new BarData(dataSet1, dataSet);
        barChart.setData(data);

        // Формируем подписи внизу графика
        LegendEntry[] legendEntry = barChart.getLegend().getEntries();
        for (int l = 0; l < statisticList.size(); l++) {

            legendEntry[l].label = statisticList.get(l).exercise.getHeader();
            if (legendEntry.length > l + statisticList.size())
                legendEntry[l + statisticList.size()].formSize = 0;
        }
        barChart.getLegend().setCustom(legendEntry);
        barChart.getLegend().setWordWrapEnabled(true); // перенос меток по строкам


        float barSpace = 0.02f;
        float groupSpace = 0.3f;
        int groupCount = statisticList.size();

        barChart.getBarData().setBarWidth(0.46f);
        barChart.getXAxis().setAxisMinValue(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        barChart.groupBars(0, groupSpace, barSpace);

        barChart.setFitBars(true);
        barChart.invalidate();


    }





}