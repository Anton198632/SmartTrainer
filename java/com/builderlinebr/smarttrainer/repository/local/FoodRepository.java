package com.builderlinebr.smarttrainer.repository.local;

import android.app.Application;
import com.builderlinebr.smarttrainer.database.Database;
import com.builderlinebr.smarttrainer.database.Food;
import com.builderlinebr.smarttrainer.database.FoodDao;

import java.util.List;

public class FoodRepository {
    private List<Food> foodList;
    private FoodDao foodDao;

    public FoodRepository(Application application) {
        Database database = Database.getDatabase(application);
        foodDao = database.foodDao();
    }

    public List<Food> select(int pkMin, int limit){
        foodList = foodDao.select(pkMin, limit);
        return foodList;
    }

    public List<Food> selectSearchFoods (String search, int limit){
        foodList = foodDao.selectSearchFoods(search, limit);
        return foodList;
    }

}
