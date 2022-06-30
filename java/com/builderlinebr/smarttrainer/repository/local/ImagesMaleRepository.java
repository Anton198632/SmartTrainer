package com.builderlinebr.smarttrainer.repository.local;

import android.app.Application;
import com.builderlinebr.smarttrainer.database.Database;
import com.builderlinebr.smarttrainer.database.ImagesMale;
import com.builderlinebr.smarttrainer.database.ImagesMaleDao;

import java.util.List;

public class ImagesMaleRepository {
    List<ImagesMale> links;
    ImagesMaleDao imagesMaleDao;

    public ImagesMaleRepository(Application application) {
        Database database = Database.getDatabase(application);
        imagesMaleDao = database.imagesMaleDao();
    }

    public List<ImagesMale> getLinks(int id) {
        links = imagesMaleDao.selectById(id);
        return links;
    }

    public List<ImagesMale> getAll() {
        links = imagesMaleDao.selectAll();
        return links;
    }
}