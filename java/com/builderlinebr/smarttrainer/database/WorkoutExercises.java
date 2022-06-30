package com.builderlinebr.smarttrainer.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "WorkoutExercises")
public class WorkoutExercises {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    private int pk;

    @ColumnInfo(name = "workoutId")
    private int workoutId;

    @ColumnInfo(name = "exercisesId")
    private int exercisesId;

    @ColumnInfo(name = "order")
    private int order;

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public WorkoutExercises(int pk, int workoutId, int exercisesId, int order) {
        this.pk = pk;
        this.workoutId = workoutId;
        this.exercisesId = exercisesId;
        this.order = order;
    }

    public WorkoutExercises() {
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public void setExercisesId(int exercisesId) {
        this.exercisesId = exercisesId;
    }

    public int getPk() {
        return pk;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public int getExercisesId() {
        return exercisesId;
    }
}
