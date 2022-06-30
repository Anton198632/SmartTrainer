package com.builderlinebr.smarttrainer.repository.local;

import android.app.Application;
import com.builderlinebr.smarttrainer.database.*;

import java.util.List;

public class ImagesMRepository {
    List<ImagesM> links;
    ImagesMDao imagesMDao;

    public ImagesMRepository(Application application) {
        Database database = Database.getDatabase(application);
        imagesMDao = database.imagesMDao();
    }

    public List<ImagesM> getLinks(int id) {
        links = imagesMDao.selectById(id);
        return links;
    }

    public List<ImagesM> getAll() {
        links = imagesMDao.selectAll();
        return links;
    }
}
