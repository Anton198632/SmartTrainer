package com.builderlinebr.smarttrainer.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.builderlinebr.smarttrainer.database.Workouts;
import com.builderlinebr.smarttrainer.repository.local.WorkoutsRepository;

import java.util.List;

public class WorkoutsViewModel extends AndroidViewModel {

    List<Workouts> workouts;
    WorkoutsRepository workoutsRepository;


    public WorkoutsViewModel(@NonNull Application application) {
        super(application);
        workoutsRepository = new WorkoutsRepository(application);
    }

    public List<Workouts> getAll() {
        workouts = workoutsRepository.selectAll();
        return workouts;
    }

    public List<Workouts> getIsMyWorkouts() {
        workouts = workoutsRepository.selectIsMy();
        return workouts;
    }

    public List<Workouts> getOtherWorkouts() {
        workouts = workoutsRepository.selectOther();
        return workouts;
    }

    public List<Workouts> getGeoWorkouts(){
        workouts = workoutsRepository.selectGeo();
        return  workouts;
    }

    public List<Workouts> getById(int id) {
        workouts = workoutsRepository.selectById(id);
        return workouts;
    }

    public void addNewWorkout(Workouts workout){
        workoutsRepository.addWorkout(workout);
    }

    public void delete(Workouts workout){
        workoutsRepository.delete(workout);
    }
}
