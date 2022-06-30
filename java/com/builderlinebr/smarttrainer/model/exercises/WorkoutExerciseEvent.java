package com.builderlinebr.smarttrainer.model.exercises;

import com.builderlinebr.smarttrainer.database.Exercises;

public class WorkoutExerciseEvent {

    long dateTime;
    public String dateTimeString;
    public Exercises exercise;
    long interval;
    int workoutId;
    int workoutExercisePk;
    int count;
    float value;

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getPk() {
        return pk;
    }

    int pk;


    public WorkoutExerciseEvent() {
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }

    public void setDateTime(long dateTime) {
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

    public void setValue(float value) {
        this.value = value;
    }

    public long getDateTime() {
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

    public float getValue() {
        return value;
    }

    public WorkoutExerciseEvent(long dateTime, long interval, int workoutId, int workoutExercisePk, int count, float value) {
        this.dateTime = dateTime;
        this.interval = interval;
        this.workoutId = workoutId;
        this.workoutExercisePk = workoutExercisePk;
        this.count = count;
        this.value = value;
    }
}
