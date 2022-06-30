package com.builderlinebr.smarttrainer.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FoodDao {

    @Query("SELECT * FROM food WHERE pk > :minPk  LIMIT :limit")
    List<Food> select(int minPk, int limit);

    @Query("SELECT * FROM food WHERE UPPER(foodName) LIKE '%'||:search||'%' LIMIT :limit")
    List<Food> selectSearchFoods(String search, int limit);


}
