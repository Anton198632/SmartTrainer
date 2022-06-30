package com.builderlinebr.smarttrainer.repository.local;

import android.app.Application;
import android.os.AsyncTask;
import com.builderlinebr.smarttrainer.database.Database;
import com.builderlinebr.smarttrainer.database.Exercises;
import com.builderlinebr.smarttrainer.database.ExercisesDao;

import java.util.List;

public class ExercisesRepository {

    private List<Exercises> allExercises;
    private static ExercisesDao exercisesDao;

    public ExercisesRepository(Application application) {
        Database database = Database.getDatabase(application);
        exercisesDao = database.exercisesDao();
    }

    public List<Exercises> getExercisesByIds(int minIds){
        allExercises = exercisesDao.selectByIds(minIds);
        return  allExercises;
    }

    public List<Exercises> getByLikeNameAndProperties(String search, int limit){
        allExercises = exercisesDao.selectByLikeNameAndProperties(search, limit);
        return  allExercises;
    }

    public List<Exercises> getFavorites(){
        allExercises = exercisesDao.selectFavorites();
        return  allExercises;
    }

    public Exercises getExerciseById(int id){
        Exercises result = null;
        allExercises  = exercisesDao.selectExerciseById(id);
        if (allExercises.size()>0) result = allExercises.get(0);
        return result;
    }

    // --- Обновление данных в БД ---------
    public void updateItem(Exercises exercises) {
        new UpdateNoteAsync().execute(exercises);
    }

    private static class UpdateNoteAsync extends AsyncTask<Exercises,Void,Void> {
        @Override
        protected Void doInBackground(Exercises... exercises) {
            exercisesDao.updateExercises(exercises[0]);
            return null;
        }
    }

}
