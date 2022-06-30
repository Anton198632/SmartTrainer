package com.builderlinebr.smarttrainer.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface EventsDao {

    @Query("SELECT * FROM events WHERE workoutId = :workoutId and dateTime >= :dateTime")
    List<Events> selectByWorkoutId(int workoutId, int dateTime);

    @Query("SELECT * FROM events WHERE workoutId = :workoutId and dateTime between :dateTimeBegin and :dateTimeEnd")
    List<Events> selectByWorkoutId(int workoutId, int dateTimeBegin, int dateTimeEnd);

    @Insert(onConflict = REPLACE)
    void insertEvent(Events event);

    @Query("DELETE FROM events WHERE pk = :pk")
    void delete(int pk);

    @Query("UPDATE events SET count = :count, value = :value, interval = :interval WHERE pk = :pk")
    void updateEvent (int count, int value, int interval, int pk);
}
