package com.builderlinebr.smarttrainer.database;

import androidx.room.*;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserTableDao {

    @Query("SELECT * FROM UserTable")
    List<UserTable> selectAllUsers();

    @Delete
    void deleteUser(UserTable user);

    @Insert(onConflict = REPLACE)
    void insertUser(UserTable user);

    @Update(onConflict = REPLACE)
    void updateUser(UserTable user);

}
