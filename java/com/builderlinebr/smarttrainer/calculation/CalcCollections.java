package com.builderlinebr.smarttrainer.calculation;

import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseEvent;

import java.util.List;

public class CalcCollections {

    public WorkoutExerciseEvent getWorkoutExerciseEvent(List<WorkoutExerciseEvent> events, int workoutExercisePk) {
        WorkoutExerciseEvent result = null;

        for (WorkoutExerciseEvent event : events) {
            if (event.getWorkoutExercisePk() == workoutExercisePk){
               result = event;
            }
        }
        return result;
    }

    public boolean findExerciseId(List<Integer> exercisesList, int exerciseId){
        Boolean result = false;

        for (Integer id : exercisesList) {

            if (id == exerciseId) {
                result = true;
                break;
            }
        }
        return result;
    }

    public WorkoutExerciseEvent getWorkoutExerciseEvent(List<WorkoutExerciseEvent> events, String date) {
        WorkoutExerciseEvent result = null;

        for (WorkoutExerciseEvent event : events) {
            if (event.dateTimeString!=null && event.dateTimeString.equals(date)){
                result = event; break;
            }
        }
        return result;
    }




    public boolean findPosition (List<Integer> positions, int newPosition){
        boolean find = false;
        for (Integer position : positions) {
            if (position == newPosition){
                find = true;
                break;
            }
        }
        return find;
    }

}
