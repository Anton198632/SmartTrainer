package com.builderlinebr.smarttrainer.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "GeolocationEventsTable")
public class GeolocationEventsTable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    private int pk;

    @ColumnInfo(name = "workoutId")
    private int workoutId;

    @ColumnInfo(name = "distance")
    private int distance;
    @ColumnInfo(name = "interval")
    private int interval;
    @ColumnInfo(name = "coordinates")
    private String coordinates;
    @ColumnInfo(name = "date")
    private int date;

    public GeolocationEventsTable() {
    }

    public GeolocationEventsTable(int pk, int distance, int interval, String coordinates, int date, int workoutId) {
        this.pk = pk;
        this.distance = distance;
        this.interval = interval;
        this.coordinates = coordinates;
        this.date = date;
        this.workoutId = workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public int getPk() {
        return pk;
    }

    public int getDistance() {
        return distance;
    }

    public int getInterval() {
        return interval;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public int getDate() {
        return date;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
