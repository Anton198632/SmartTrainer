package com.builderlinebr.smarttrainer.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.builderlinebr.smarttrainer.database.WorkoutExercises;
import com.builderlinebr.smarttrainer.repository.local.WorkoutExercisesRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorkoutExercisesViewModel extends AndroidViewModel {

    List<WorkoutExercises> workoutExercisesList;
    WorkoutExercisesRepository workoutExercisesRepository;


    public WorkoutExercisesViewModel(@NonNull Application application) {
        super(application);
        workoutExercisesRepository = new WorkoutExercisesRepository(application);
    }

    public List<WorkoutExercises> selectAllExercises (int workoutId){
        workoutExercisesList = workoutExercisesRepository.selectAllExercises(workoutId);
        // Сортировка по возрастанию поля "order"
        Collections.sort(workoutExercisesList, new Comparator<WorkoutExercises>() {
            @Override
            public int compare(WorkoutExercises o1, WorkoutExercises o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });
        return workoutExercisesList;
    }

    public  void deleteExercise(WorkoutExercises workoutExercise){
        workoutExercisesRepository.deleteExercise(workoutExercise);
    }

    public void insertExercise(WorkoutExercises newWorkoutExercise){
        workoutExercisesRepository.insertExercise(newWorkoutExercise);
    }

    public void updateWorkoutExercise(WorkoutExercises workoutExercise){
        workoutExercisesRepository.updateItem(workoutExercise);
    }
}
