package com.builderlinebr.smarttrainer.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.builderlinebr.smarttrainer.database.GeolocationEventsTable;
import com.builderlinebr.smarttrainer.model.exercises.GeolocationEvents;
import com.builderlinebr.smarttrainer.repository.local.GeolocationEventsRepository;

import java.util.ArrayList;
import java.util.List;

public class GeolocationEventsViewModel extends AndroidViewModel {

    List<GeolocationEvents> geolocationEvents;
    GeolocationEventsRepository geolocationEventsRepository;


    public GeolocationEventsViewModel(@NonNull Application application) {
        super(application);
        geolocationEventsRepository = new GeolocationEventsRepository(application);
    }

    public List<GeolocationEvents> getEventsByDistanceId(int workoutId, int limit) {
        List<GeolocationEventsTable> geolocationEventsTables =
                limit <= 0 ?
                        geolocationEventsRepository.selectByDistanceId(workoutId) :
                        geolocationEventsRepository.selectByDistanceIdLimit(workoutId, limit);
        geolocationEvents = new ArrayList<>();

        for (GeolocationEventsTable geolocationEventsTable : geolocationEventsTables) {
            String[] coordinates = geolocationEventsTable.getCoordinates().split("\n");
            List<double[]> coordinatesList = new ArrayList<>();
            for (String coordinate : coordinates) {
                if (!coordinate.equals("")) {
                    String[] c = coordinate.split(";");
                    coordinatesList.add(new double[]{Double.parseDouble(c[0]), Double.parseDouble(c[1]), Double.parseDouble(c[2])});
                }
            }
            GeolocationEvents event = new GeolocationEvents(
                    geolocationEventsTable.getDate(),
                    geolocationEventsTable.getDistance(),
                    geolocationEventsTable.getInterval(),
                    coordinatesList,
                    geolocationEventsTable.getWorkoutId());
            geolocationEvents.add(event);
        }

        return geolocationEvents;
    }

    public void addEvent(GeolocationEvents event) {
        GeolocationEventsTable geolocationEventsTable = new GeolocationEventsTable();
        geolocationEventsTable.setDate(event.getDate());
        geolocationEventsTable.setDistance(event.getDistance());
        geolocationEventsTable.setInterval(event.getInterval());
        geolocationEventsTable.setWorkoutId(event.getWorkoutId());
        StringBuilder stringBuilder = new StringBuilder();
        for (double[] coordinate : event.getCoordinatesList()) {
            stringBuilder.append(coordinate[0]);
            stringBuilder.append(";");
            stringBuilder.append(coordinate[1]);
            stringBuilder.append(";");
            stringBuilder.append(coordinate[2]);
            stringBuilder.append("\n");
        }
        geolocationEventsTable.setCoordinates(stringBuilder.toString());
        geolocationEventsRepository.addEvent(geolocationEventsTable);

    }

    public void delete(int id) {
        geolocationEventsRepository.delete(id);
    }

    public void updateEvent(int distance, List<double[]> coordinatesList, int interval, int workoutId, int pk) {
        StringBuilder stringBuilder = new StringBuilder();
        for (double[] coordinate : coordinatesList) {
            stringBuilder.append(coordinate[0]);
            stringBuilder.append(";");
            stringBuilder.append(coordinate[1]);
            stringBuilder.append(";");
            stringBuilder.append(coordinate[2]);
            stringBuilder.append("\n");
        }
        geolocationEventsRepository.updateEvent(distance, stringBuilder.toString(), interval, workoutId, pk);
    }
}
