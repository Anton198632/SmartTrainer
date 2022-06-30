package com.builderlinebr.smarttrainer.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.builderlinebr.smarttrainer.database.Food;
import com.builderlinebr.smarttrainer.repository.local.FoodRepository;

import java.util.List;

public class FoodViewModel extends AndroidViewModel {
    List<Food> foodList;
    FoodRepository foodRepository;


    public FoodViewModel(@NonNull Application application) {
        super(application);
        foodRepository = new FoodRepository(application);
    }

    public List<Food> select(int pk, int limit) {
        foodList = foodRepository.select(pk, limit);
        return foodList;
    }

    public List<Food> selectSearchFoods(String search, int limit) {
        foodList = foodRepository.selectSearchFoods(search, limit);
        return foodList;
    }
}
