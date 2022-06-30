package com.builderlinebr.smarttrainer.database;


import androidx.room.*;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WorkoutExercisesDao {

    @Query("SELECT * FROM WorkoutExercises WHERE workoutId = :workoutId")
    List<WorkoutExercises> selectAllExercises(int workoutId);

    @Query("DELETE FROM WorkoutExercises WHERE workoutId = :workoutId")
    void deleteAllExercises(int workoutId);

    @Delete
    void deleteExercises(WorkoutExercises workoutExercise);

    @Insert(onConflict = REPLACE)
    void insertExercise(WorkoutExercises newWorkoutExercise);

    @Update(onConflict = REPLACE)
    void updateNote(WorkoutExercises workoutExercises);

}
