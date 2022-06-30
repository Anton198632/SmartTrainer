package com.builderlinebr.smarttrainer.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.builderlinebr.smarttrainer.database.UserTable;
import com.builderlinebr.smarttrainer.repository.local.UserTableRepository;

import java.util.List;

public class UserTableViewModel extends AndroidViewModel {

    List<UserTable> userTables;
    UserTableRepository repository;

    public UserTableViewModel(@NonNull Application application) {
        super(application);

        repository = new UserTableRepository(application);
    }


    public List<UserTable> selectAllUsers(){
        userTables = repository.selectAllUsers();
        return userTables;
    }

    public void deleteUser(UserTable user){
        repository.deleteUser(user);
    }

    public void insertUser(UserTable user){
        repository.insertUser(user);
    }

    public void updateUser(UserTable user){
        repository.updateUser(user);
    }
}
