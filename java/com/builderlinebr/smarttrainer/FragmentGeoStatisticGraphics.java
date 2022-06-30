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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.builderlinebr.smarttrainer.calculation.CalcMap;
import com.builderlinebr.smarttrainer.customviews.MarkerInGeoGraph;
import com.builderlinebr.smarttrainer.model.exercises.GeolocationEvents;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class FragmentGeoStatisticGraphics extends Fragment {

    Context context;
    LineChart lineChartGeoSpeed;
    LineChart lineChartGeoTime;


    List<double[]> coordinates;
    List<double[]> preCoordinates;
    String date;

    LinearLayout waitLayout;
    View view;

    public FragmentGeoStatisticGraphics(Context context, String date, String preDate) {
        this.context = context;


        for (GeolocationEvents event : ((GeoStatisticActivity) context).geoEvents) {
            if (event.getDateString().equals(date)) {
                coordinates = event.getCoordinatesList();
            }
            if (event.getDateString().equals(preDate)) {
                preCoordinates = event.getCoordinatesList();
            }
        }


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_geo_statistic_graph, container, false);



        waitLayout = view.findViewById(R.id.wait_layout);
        waitLayout.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                initComponents();
                waitLayout.setVisibility(View.GONE);
                lineChartGeoSpeed.invalidate();
                lineChartGeoTime.invalidate();
            }
        });

        return view;
    }


    void initComponents() {


        // пересчитывеме расстояние - скорость

        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<Entry> valuesTime = new ArrayList<>();
        float distance = 0;
        float time = 0;

        if (coordinates != null)
            for (int i = 1; i < coordinates.size(); i++) {
                float currantDistance = new CalcMap().getDistanceInM(
                        coordinates.get(i - 1)[0], coordinates.get(i - 1)[1],
                        coordinates.get(i)[0], coordinates.get(i)[1]
                );
                if (currantDistance < 20) {
                    distance += currantDistance;
                    if (coordinates.get(i)[2]>0)
                    time += currantDistance/coordinates.get(i)[2];
                    values.add(new Entry(distance, (float) coordinates.get(i)[2], ""));
                    valuesTime.add(new Entry(distance, time));
                }
            }

        lineChartGeoSpeed = view.findViewById(R.id.line_chart_geo_speed);
        lineChartGeoSpeed.setTouchEnabled(true);
        lineChartGeoSpeed.getLegend().setWordWrapEnabled(true); // перенос меток по строкам
        lineChartGeoSpeed.setPinchZoom(false); // можно увеличиват ьили уменьшать zoom


        lineChartGeoSpeed.setMarker(new MarkerInGeoGraph(context, R.layout.marker_graphic, false)); // маркер к графику

        LineDataSet set1 = drawLineData(values, Color.BLACK, getString(R.string.current_geo_workout));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        // предыдущая тренировка
        ArrayList<Entry> values2 = new ArrayList<>();
        ArrayList<Entry> valuesTime2 = new ArrayList<>();
        if (preCoordinates != null) {
            distance = 0;
            time = 0;
            for (int i = 1; i < preCoordinates.size(); i++) {
                float currantDistance = new CalcMap().getDistanceInM(
                        preCoordinates.get(i - 1)[0], preCoordinates.get(i - 1)[1],
                        preCoordinates.get(i)[0], preCoordinates.get(i)[1]
                );
                if (currantDistance < 20) {
                    distance += currantDistance;
                    if (preCoordinates.get(i)[2]>0)
                        time += currantDistance/preCoordinates.get(i)[2];
                    values2.add(new Entry(distance, (float) preCoordinates.get(i)[2], ""));
                    valuesTime2.add(new Entry(distance, time));
                }
            }
            LineDataSet set = drawLineData(values2, Color.GRAY, getString(R.string.pre_geo_workout));
            dataSets.add(set);
        }


        LineData data = new LineData(dataSets);
        lineChartGeoSpeed.setData(data);

        Description d = new Description();
        d.setText(getString(R.string.description_geo_speed));
        d.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        lineChartGeoSpeed.setDescription(d);


        // График путь-время
        lineChartGeoTime = view.findViewById(R.id.line_chart_geo_time);
        lineChartGeoTime.setTouchEnabled(true);
        lineChartGeoTime.getLegend().setWordWrapEnabled(true); // перенос меток по строкам
        lineChartGeoTime.setPinchZoom(false); // можно увеличиват ьили уменьшать zoom
        lineChartGeoTime.setMarker(new MarkerInGeoGraph(context, R.layout.marker_graphic, true)); // маркер к графику
        LineDataSet setTime = drawLineData(valuesTime, Color.BLACK, getString(R.string.current_geo_workout));
        LineDataSet setTimePre = drawLineData(valuesTime2, Color.GRAY, getString(R.string.pre_geo_workout));
        ArrayList<ILineDataSet> dataSetsTime = new ArrayList<>();
        dataSetsTime.add(setTime);
        dataSetsTime.add(setTimePre);
        LineData dataTime = new LineData(dataSetsTime);
        lineChartGeoTime.setData(dataTime);

        d = new Description();
        d.setText(getString(R.string.description_geo_time));
        d.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        lineChartGeoTime.setDescription(d);

    }


    LineDataSet drawLineData(ArrayList<Entry> values, int color, String label){
        LineDataSet set1 = new LineDataSet(values, label);
        set1.setDrawIcons(false);
        set1.setColor(color);

        set1.setLineWidth(1f);
        set1.setDrawCircles(false);

        set1.setValueTextSize(9f);
        set1.setDrawValues(false); // убираем значения отчетов с графика

        return set1;
    }
}
