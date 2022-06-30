package com.builderlinebr.smarttrainer.repository.local;

import android.app.Application;
import com.builderlinebr.smarttrainer.database.Database;
import com.builderlinebr.smarttrainer.database.UserTable;
import com.builderlinebr.smarttrainer.database.UserTableDao;

import java.util.List;

public class UserTableRepository {

    List<UserTable> userTables;
    UserTableDao userTableDao;

    public UserTableRepository(Application application) {
        Database database = Database.getDatabase(application);
        userTableDao = database.userTableDao();
    }


    public List<UserTable> selectAllUsers(){
        userTables = userTableDao.selectAllUsers();
        return userTables;
    }

    public void deleteUser(UserTable user){
        userTableDao.deleteUser(user);
    }

    public void insertUser(UserTable user){
        userTableDao.insertUser(user);
    }

    public void updateUser(UserTable user){
        userTableDao.updateUser(user);
    }
}
