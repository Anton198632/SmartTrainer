package com.builderlinebr.smarttrainer.model.exercises;

import com.builderlinebr.smarttrainer.calculation.CalcTime;

import java.util.List;

public class GeolocationEvents {

    int date;
    int distance;
    int interval;
    List<double[]> coordinatesList;
    int workoutId;

    String dateString;


    public String getDateString() {
        return dateString;
    }

    public GeolocationEvents(int date, int distance, int interval, List<double[]> coordinatesList, int workoutId) {
        this.date = date;
        this.distance = distance;
        this.interval = interval;
        this.coordinatesList = coordinatesList;
        this.workoutId = workoutId;

        this.dateString = new CalcTime().convertLongToDate(date);
    }

    public int getDate() {
        return date;
    }

    public int getDistance() {
        return distance;
    }

    public int getInterval() {
        return interval;
    }

    public List<double[]> getCoordinatesList() {
        return coordinatesList;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setCoordinatesList(List<double[]> coordinatesList) {
        this.coordinatesList = coordinatesList;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public int getWorkoutId() {
        return workoutId;
    }
}
