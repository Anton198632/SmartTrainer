package com.builderlinebr.smarttrainer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.appyvet.materialrangebar.RangeBar;
import com.builderlinebr.smarttrainer.adapters.NumbersAdapter;
import com.builderlinebr.smarttrainer.calculation.CalcMap;
import com.builderlinebr.smarttrainer.model.exercises.GeolocationEvents;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.geometry.*;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class FragmentGeoStatisticMap extends Fragment {

    View view;

    MapView mapView;
    RangeBar rangeBar;
    RecyclerView geoWorkoutNumberRecyclerView;

    TextView averageTextView;
    TextView distanceTextView;

    List<PolylineMapObject> polylineMapObjectList;


    Context context;
    public List<double[]> coordinates;
    List<Integer> distance;

    List<Integer> numbers;

    String currentDate;


    public FragmentGeoStatisticMap(Context context, String date) {

        this.currentDate = date;
        this.context = context;
        numbers = new ArrayList<>();
        int i = 1;
        for (GeolocationEvents event : ((GeoStatisticActivity) context).geoEvents) {
            if (event.getDateString().equals(date)) {
                coordinates = event.getCoordinatesList();
                numbers.add(i);
                i++;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_geo_statistic_map, container, false);


        geoWorkoutNumberRecyclerView = view.findViewById(R.id.geo_workout_number);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        geoWorkoutNumberRecyclerView.setLayoutManager(layoutManager);

        NumbersAdapter adapter = new NumbersAdapter(numbers, context, this);
        geoWorkoutNumberRecyclerView.setAdapter(adapter);

        if (numbers != null && numbers.size() < 2) {
            geoWorkoutNumberRecyclerView.setVisibility(View.GONE);
        }

        initComponents();

        return view;
    }

    public void updateCoordinate(int i) {
        int j = 0;
        for (GeolocationEvents event : ((GeoStatisticActivity) context).geoEvents) {
            if (event.getDateString().equals(currentDate)) {
                if (j == i) {
                    coordinates = event.getCoordinatesList();
                }
                j++;
            }
        }
        initComponents();
    }

    public void initComponents() {

        averageTextView = view.findViewById(R.id.average_speed_text_view);
        distanceTextView = view.findViewById(R.id.distance_text_view);

        distance = getDistance();

        rangeBar = view.findViewById(R.id.range_bar);
        rangeBar.setTickInterval(1);
        rangeBar.setTickStart(0);

        String[] ch = new String[coordinates.size() - 1];
        for (int i = 0; i < ch.length; i++) {
            ch[i] = "_" + Integer.toString(i);
        }
        rangeBar.setPinTextFormatter(new RangeBar.PinTextFormatter() {
            @Override
            public String getText(String s) {
                int i = Integer.parseInt(s);
                if (i >= distance.size()) i = distance.size() - 1;
                return Integer.toString(distance.get(i));
            }
        });

        rangeBar.setTickEnd(coordinates.size() - 2);
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int i, int i1, String s, String s1) {
                initPath(i, i1);
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {

            }
        });


        mapView = view.findViewById(R.id.mapView);
        Point point = new Point(55.751574, 37.573856);
        if (coordinates != null && coordinates.size() > 0) {
            point = new Point(
                    coordinates.get(0)[0],
                    coordinates.get(0)[1]
            );
        }

        mapView.getMap().move(
                new CameraPosition(point, 15f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);


        initPath(1, coordinates.size());
    }


    List<Integer> getDistance() {
        List<Integer> result = new ArrayList<>();
        int dist = 0;
        for (int i = 1; i < coordinates.size(); i++) {
            dist += (int) new CalcMap().getDistanceInM(coordinates.get(i - 1)[0], coordinates.get(i - 1)[1],
                    coordinates.get(i)[0], coordinates.get(i)[1]);
            result.add(dist);
        }

        return result;
    }

    private void initPath(int start, int stop) {

        if (start < 1) start = 1;

        if (polylineMapObjectList != null) {
            for (PolylineMapObject polylineMapObject : polylineMapObjectList) {
                mapView.getMap().getMapObjects().remove(polylineMapObject);
            }
        }

        polylineMapObjectList = new ArrayList<>();

        double averageSpeed = 0;
        double min = 10000;
        double max = 0;
        String cl = "";
        if (coordinates != null && coordinates.size() > 1) {
            averageSpeed = coordinates.get(0)[2];
            for (int i = start; i < stop; i++) {
                averageSpeed += coordinates.get(i)[2];
                if (coordinates.get(i)[2] < min) min = coordinates.get(i)[2];
                if (coordinates.get(i)[2] > max) max = coordinates.get(i)[2];
            }
            averageSpeed = averageSpeed / (stop - start);

            int distanceCurrent = 0;
            distanceCurrent = distance.get(stop < distance.size() ? stop : distance.size() - 1) - distance.get(start);

            for (int i = start; i < stop; i++) { //coordinates.size()
                int r = 255;
                int g = 255;
                int b = 0;
                if (coordinates.get(i)[2] < averageSpeed) { // Если текущ. скорость < средней уменьшаем зеленый цвет
                    g = (int) (255 * coordinates.get(i)[2] / averageSpeed);
                } else { // Если текущая скорость дольше средней уменьшаем красный цвет
                    r = ((int) (coordinates.get(i)[2] * 255 / averageSpeed) / 255) * 255;
                    r = (int) (coordinates.get(i)[2] * 255 / averageSpeed) - r;
                }

                String rS = Integer.toHexString(r).toUpperCase();
                rS = rS.length() == 1 ? "0" + rS : rS;
                String gS = Integer.toHexString(g).toUpperCase();
                gS = gS.length() == 1 ? "0" + gS : gS;
                String color = "#FF" + rS + gS + "00"; // формируем цвет
                cl += color + "\r\n";
                drawPath(coordinates.get(i - 1)[0], coordinates.get(i - 1)[1], coordinates.get(i)[0], coordinates.get(i)[1], Color.parseColor(color)); // рисуем ломаную

            }


            averageTextView.setText(String.format("%.2f", averageSpeed) + " m/s");
            distanceTextView.setText(Integer.toString(distanceCurrent) + " m");
        }
    }


    //region Прорисовка пути
    private void drawPath(double lat1, double long1, double lat2, double long2, int color) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(lat1, long1));
        points.add(new Point(lat2, long2));
        Polyline polyline = new Polyline(points);
        PolylineMapObject polylineMapObject = mapView.getMap().getMapObjects().addPolyline(polyline); // рисуем линию
        polylineMapObject.setStrokeColor(color);

        polylineMapObjectList.add(polylineMapObject);
    }
    //endregion
}
