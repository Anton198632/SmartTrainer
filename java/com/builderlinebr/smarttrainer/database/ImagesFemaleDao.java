package com.builderlinebr.smarttrainer.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImagesFemaleDao {
    @Query("SELECT * FROM imagesfemale WHERE Id = :id")
    List<ImagesFemale> selectById(int id);

    @Query("SELECT * FROM imagesfemale")
    List<ImagesFemale> selectAll();
}
