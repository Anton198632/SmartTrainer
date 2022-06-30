package com.builderlinebr.smarttrainer.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WorkoutsDao {

    @Query("SELECT * FROM Workouts")
    List<Workouts> selectAll();

    @Query("SELECT * FROM Workouts where isMy = :isMy")
    List<Workouts> selectOnly(int isMy);

    @Query("SELECT * FROM Workouts where id = :id")
    List<Workouts> selectById(int id);

    @Insert(onConflict = REPLACE)
    void insertWorkout(Workouts workout);

    @Delete
    void delete(Workouts workout);


}
