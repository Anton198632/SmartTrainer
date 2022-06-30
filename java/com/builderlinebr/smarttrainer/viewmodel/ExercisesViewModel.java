package com.builderlinebr.smarttrainer.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.builderlinebr.smarttrainer.database.Exercises;
import com.builderlinebr.smarttrainer.repository.local.ExercisesRepository;

import java.util.List;

public class ExercisesViewModel extends AndroidViewModel {

    private List<Exercises> exercisesList;
    private ExercisesRepository exercisesRepository;


    public ExercisesViewModel(@NonNull Application application) {
        super(application);
        exercisesRepository = new ExercisesRepository(application);
    }

    public List<Exercises> getSearchList(int search) {
        exercisesList = exercisesRepository.getExercisesByIds(search);
        return exercisesList;
    }

    public List<Exercises> getByLikeNameAndProperties(String search, int limit) {
        exercisesList = exercisesRepository.getByLikeNameAndProperties(search, limit);
        return exercisesList;
    }

    public List<Exercises> getFavorites() {
        exercisesList = exercisesRepository.getFavorites();
        return exercisesList;
    }

    public Exercises getExerciseById(int id){
       return exercisesRepository.getExerciseById(id);
    }

    public void updateExercise(Exercises exercises) {
        exercisesRepository.updateItem(exercises);
    }



}
