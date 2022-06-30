package com.builderlinebr.smarttrainer.repository.local;

import android.app.Application;
import android.os.AsyncTask;
import com.builderlinebr.smarttrainer.database.*;

import java.util.List;

public class WorkoutExercisesRepository  {
    private List<WorkoutExercises> workoutExercisesList;
    private static WorkoutExercisesDao workoutExercisesDao;

    public WorkoutExercisesRepository(Application application) {
        Database database = Database.getDatabase(application);
        workoutExercisesDao = database.workoutExercisesDao();
    }

    public List<WorkoutExercises> selectAllExercises(int workoutId){
        workoutExercisesList  = workoutExercisesDao.selectAllExercises(workoutId);
        return  workoutExercisesList;
    }

    public void deleteExercise(WorkoutExercises workoutExercises){
        workoutExercisesDao.deleteExercises(workoutExercises);
    }

    public void insertExercise(WorkoutExercises newWorkoutExercise){
        workoutExercisesDao.insertExercise(newWorkoutExercise);
    }

    // ---------- Асинхронное обновление данных в БД
    public void updateItem(WorkoutExercises workoutExercises){
        new UpdateNoteAsync().execute(workoutExercises);
    }

    private static class UpdateNoteAsync extends AsyncTask<WorkoutExercises, Void, Void>{

        @Override
        protected Void doInBackground(WorkoutExercises... workoutExercises) {
            workoutExercisesDao.updateNote(workoutExercises[0]);
            return null;
        }
    }



}
