package com.builderlinebr.smarttrainer.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Workouts")
public class Workouts {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "workoutName")
    private String workoutName;

    @ColumnInfo(name = "workoutDescription")
    private String workoutDescription;

    @ColumnInfo(name = "workoutImage")
    private String workoutImage;

    @ColumnInfo(name = "isMy")
    private int isMy;

    @ColumnInfo(name = "forGirl")
    private int forGirl;

    public Workouts(int id, String workoutName, String workoutDescription, int isMy, int forGirl) {
        this.id = id;
        this.workoutName = workoutName;
        this.workoutDescription = workoutDescription;
        this.isMy = isMy;
        this.forGirl = forGirl;
    }

    public Workouts() {
    }

    public Workouts(int id, String workoutName, String workoutDescription, String workoutImage, int isMy, int forGirl) {
        this.id = id;
        this.workoutName = workoutName;
        this.workoutDescription = workoutDescription;
        this.workoutImage = workoutImage;
        this.isMy = isMy;
        this.forGirl = forGirl;
    }

    public void setWorkoutImage(String workoutImage) {
        this.workoutImage = workoutImage;
    }

    public String getWorkoutImage() {
        return workoutImage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public void setWorkoutDescription(String workoutDescription) {
        this.workoutDescription = workoutDescription;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
    }

    public int getId() {
        return id;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public String getWorkoutDescription() {
        return workoutDescription;
    }

    public int getIsMy() {
        return isMy;
    }

    public int getForGirl() {
        return forGirl;
    }

    public void setForGirl(int forGirl) {
        this.forGirl = forGirl;
    }
}
