package com.builderlinebr.smarttrainer.repository.local;

import android.app.Application;
import android.os.AsyncTask;
import com.builderlinebr.smarttrainer.database.Database;
import com.builderlinebr.smarttrainer.database.Workouts;
import com.builderlinebr.smarttrainer.database.WorkoutsDao;

import java.util.List;

public class WorkoutsRepository {

    private List<Workouts> workouts;
    private static WorkoutsDao workoutsDao;

    public WorkoutsRepository(Application application) {
        Database database = Database.getDatabase(application);
        workoutsDao = database.workoutsDao();
    }

    public List<Workouts> selectAll() {
        workouts = workoutsDao.selectAll();
        return workouts;
    }

    public List<Workouts> selectIsMy() {
        workouts = workoutsDao.selectOnly(1);
        return workouts;
    }

    public List<Workouts> selectOther() {
        workouts = workoutsDao.selectOnly(0);
        return workouts;
    }

    public List<Workouts> selectGeo(){
        workouts = workoutsDao.selectOnly(2);
        return  workouts;
    }

    public List<Workouts> selectById(int id) {
        workouts = workoutsDao.selectById(id);
        return workouts;
    }

    // -----------------------------------------------------
    public void addWorkout(Workouts workout){
       // workout.setIsMy(1);
        workoutsDao.insertWorkout(workout);
    }

    public void addWorkoutAsync(Workouts workout){
      //  workout.setIsMy(1);
        new AddWorkoutAsync().execute(workout);
    }


    private static class AddWorkoutAsync extends AsyncTask<Workouts, Void, Void>{
        @Override
        protected Void doInBackground(Workouts... workouts) {
            workoutsDao.insertWorkout(workouts[0]);
            return null;
        }
    }
    //------------------------------------------------------

    public void delete(Workouts workout){
        workoutsDao.delete(workout);
    }
}
