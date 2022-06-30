package com.builderlinebr.smarttrainer.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImagesMDao {
    @Query("SELECT * FROM imagesm WHERE Id = :id")
    List<ImagesM> selectById(int id);

    @Query("SELECT * FROM imagesm")
    List<ImagesM> selectAll();
}
