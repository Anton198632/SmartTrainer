package com.builderlinebr.smarttrainer.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Events")
public class Events {

    public Events(int pk, int dateTime, int workoutId, int workoutExercisePk, int count, int value, int interval) {
        this.pk = pk;
        this.dateTime = dateTime;
        this.workoutId = workoutId;
        this.workoutExercisePk = workoutExercisePk;
        this.count = count;
        this.value = value;
        this.interval = interval;
    }

    public Events(){

    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    private int pk;

    @ColumnInfo(name = "dateTime")
    private int dateTime;

    @ColumnInfo(name = "workoutId")
    private int workoutId;

    @ColumnInfo(name = "workoutExercisePk")
    private int workoutExercisePk;

    @ColumnInfo(name = "count")
    private int count;

    @ColumnInfo(name = "value")
    private int value;

    @ColumnInfo(name = "interval")
    private int interval;


    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setDateTime(int dateTime) {
        this.dateTime = dateTime;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public void setWorkoutExercisePk(int workoutExercisePk) {
        this.workoutExercisePk = workoutExercisePk;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getPk() {
        return pk;
    }

    public int getDateTime() {
        return dateTime;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public int getWorkoutExercisePk() {
        return workoutExercisePk;
    }

    public int getCount() {
        return count;
    }

    public int getValue() {
        return value;
    }

    public int getInterval() {
        return interval;
    }


}
