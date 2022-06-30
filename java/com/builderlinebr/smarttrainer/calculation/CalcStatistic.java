package com.builderlinebr.smarttrainer.calculation;

import com.builderlinebr.smarttrainer.database.Exercises;
import com.builderlinebr.smarttrainer.database.WorkoutExercises;
import com.builderlinebr.smarttrainer.model.exercises.ExerciseEventStatistic;
import com.builderlinebr.smarttrainer.model.exercises.ExerciseStatistic;
import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseEvent;

import java.util.ArrayList;
import java.util.List;

public class CalcStatistic {

    public List<ExerciseEventStatistic> getExerciseEventStatistic(List<WorkoutExercises> workoutExercises, List<Exercises> exercises, List<WorkoutExerciseEvent> events) {
        List<ExerciseEventStatistic> result = new ArrayList<>();

        for (Exercises exercise : exercises) {

            ExerciseEventStatistic exerciseEventStatistic = new ExerciseEventStatistic();
            exerciseEventStatistic.exercise = exercise;
            exerciseEventStatistic.dateEvents = new ArrayList<>();


            for (WorkoutExerciseEvent event : events) {
                for (WorkoutExercises workoutExercise : workoutExercises) {
                    if (event.getWorkoutExercisePk() == workoutExercise.getPk() && workoutExercise.getExercisesId() == exercise.getId()) {

                        boolean find = false;
                        for (int i = 0; i < exerciseEventStatistic.dateEvents.size(); i++) {
                            if (exerciseEventStatistic.dateEvents.get(i).date.equals(event.dateTimeString)) {
                                find = true;
                                exerciseEventStatistic.dateEvents.get(i).counts.add((float) event.getCount());
                                exerciseEventStatistic.dateEvents.get(i).values.add((float) event.getValue());
                                exerciseEventStatistic.dateEvents.get(i).interval.add(event.getInterval());
                            }
                        }
                        if (!find) {
                            ExerciseEventStatistic.DateEvent dateEvent = new ExerciseEventStatistic.DateEvent(event.dateTimeString);
                            dateEvent.counts.add((float) event.getCount());
                            dateEvent.values.add((float) event.getValue());
                            dateEvent.interval.add(event.getInterval());
                            exerciseEventStatistic.dateEvents.add(dateEvent);
                        }
                    }
                }
            }


            result.add(exerciseEventStatistic);

        }

        int maxSize = 0;
        for (ExerciseEventStatistic exerciseEventStatistic : result) {
            if (exerciseEventStatistic.dateEvents.size()>maxSize) maxSize = exerciseEventStatistic.dateEvents.size();
        }


        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).dateEvents.size() < maxSize) {
                int size = 2- result.get(i).dateEvents.size();
                for (int j=0; j < size; j++) {
                    ExerciseEventStatistic.DateEvent de = new ExerciseEventStatistic.DateEvent("");
                    de.counts = new ArrayList<>();
                    de.interval = new ArrayList<>();
                    de.values = new ArrayList<>();
                    result.get(i).dateEvents.add(de);
                }
            }
        }


        return result;
    }





    public List<ExerciseStatistic> getExercisesStatistic(List<WorkoutExercises> workoutExercises, List<Exercises> exercises, List<WorkoutExerciseEvent> events, String date) {
        List<ExerciseStatistic> exerciseStatistics = new ArrayList<>();

        for (Exercises exercise : exercises) {

            ExerciseStatistic exerciseStatistic = new ExerciseStatistic();
            exerciseStatistic.exercise = exercise;
            exerciseStatistic.countAll = 0;
            exerciseStatistic.valueAll = 0;
            exerciseStatistic.averageValue = 0;
            int i = 0;
            for (WorkoutExerciseEvent event : events) {
                if (event.dateTimeString.equals(date))
                    for (WorkoutExercises workoutExercise : workoutExercises) {
                        if (workoutExercise.getPk() == event.getWorkoutExercisePk()) {
                            if (workoutExercise.getExercisesId() == exercise.getId()) {
                                i++;
                                exerciseStatistic.countAll += event.getCount();
                                exerciseStatistic.valueAll += event.getValue() * event.getCount();
                                exerciseStatistic.averageValue += event.getValue();
                            }
                        }
                    }

            }
            exerciseStatistic.averageValue = exerciseStatistic.averageValue / (i == 0 ? 1 : i);
            exerciseStatistics.add(exerciseStatistic);
        }


        return exerciseStatistics;
    }


}
