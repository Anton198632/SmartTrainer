package com.builderlinebr.smarttrainer.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface GeolocationEventsDao {

    @Query("SELECT * FROM GeolocationEventsTable WHERE workoutId = :workoutId")
    List<GeolocationEventsTable> selectByWorkoutId(int workoutId);

    @Query("SELECT * FROM GeolocationEventsTable WHERE workoutId = :workoutId LIMIT :limit")
    List<GeolocationEventsTable> selectByWorkoutIdLimit(int workoutId, int limit);

    @Insert(onConflict = REPLACE)
    void insertEvent(GeolocationEventsTable event);

    @Query("DELETE FROM GeolocationEventsTable WHERE pk = :pk")
    void delete(int pk);

    @Query("UPDATE GeolocationEventsTable SET distance = :distance, coordinates = :coordinates, interval = :interval, workoutId = :workoutId WHERE pk = :pk")
    void updateEvent (int distance, String coordinates, int interval, int workoutId, int pk);
}
