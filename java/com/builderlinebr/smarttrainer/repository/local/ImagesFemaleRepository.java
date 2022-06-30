package com.builderlinebr.smarttrainer.repository.local;

import android.app.Application;
import com.builderlinebr.smarttrainer.database.*;

import java.util.List;

public class ImagesFemaleRepository {
    List<ImagesFemale> links;
    ImagesFemaleDao imagesFemaleDao;

    public ImagesFemaleRepository(Application application) {
        Database database = Database.getDatabase(application);
        imagesFemaleDao = database.imagesFemaleDao();
    }

    public List<ImagesFemale> getLinks(int id) {
        links = imagesFemaleDao.selectById(id);
        return links;
    }

    public List<ImagesFemale> getAll() {
        links = imagesFemaleDao.selectAll();
        return links;
    }
}
