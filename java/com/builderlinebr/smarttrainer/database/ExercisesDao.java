package com.builderlinebr.smarttrainer.database;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;
@Dao
public interface ExercisesDao {

    @Query("SELECT * FROM exercises WHERE id > :minId")
    List<Exercises> selectByIds(int minId);

    @Query("SELECT * FROM exercises WHERE UPPER(Header) LIKE '%'||:search||'%' OR UPPER(Properties) LIKE '%'||:search||'%' LIMIT :limit")
    List<Exercises> selectByLikeNameAndProperties(String search, int limit);

    @Query("SELECT * FROM exercises WHERE favorites > 0")
    List<Exercises> selectFavorites();

    @Query("SELECT * FROM exercises WHERE id = :id")
    List<Exercises> selectExerciseById(int id);

    @Update(onConflict = REPLACE)
    void updateExercises(Exercises exercises);
}
