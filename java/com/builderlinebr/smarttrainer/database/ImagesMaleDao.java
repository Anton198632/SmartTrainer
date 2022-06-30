package com.builderlinebr.smarttrainer.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImagesMaleDao {
    @Query("SELECT * FROM imagesmale WHERE Id = :id")
    List<ImagesMale> selectById(int id);

    @Query("SELECT * FROM imagesmale")
    List<ImagesMale> selectAll();
}
