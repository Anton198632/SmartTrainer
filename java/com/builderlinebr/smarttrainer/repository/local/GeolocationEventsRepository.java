package com.builderlinebr.smarttrainer.repository.local;

import android.app.Application;
import android.os.AsyncTask;
import com.builderlinebr.smarttrainer.database.Database;
import com.builderlinebr.smarttrainer.database.GeolocationEventsDao;
import com.builderlinebr.smarttrainer.database.GeolocationEventsTable;

import java.util.List;

public class GeolocationEventsRepository {

    List<GeolocationEventsTable> geolocationEventsTables;
     private static GeolocationEventsDao geolocationEventsDao;

    public GeolocationEventsRepository(Application application) {
        Database database = Database.getDatabase(application);
        geolocationEventsDao = database.geolocationEventsDao();
    }

    public List<GeolocationEventsTable> selectByDistanceId(int workoutId) {
        geolocationEventsTables = geolocationEventsDao.selectByWorkoutId(workoutId);
        return geolocationEventsTables;
    }

    public List<GeolocationEventsTable> selectByDistanceIdLimit(int workoutId, int limit) {
        geolocationEventsTables = geolocationEventsDao.selectByWorkoutIdLimit(workoutId, limit);
        return geolocationEventsTables;
    }

    public void addEvent(GeolocationEventsTable event) {
        new AddEventAsync().execute(event);
    }

    private static class AddEventAsync extends AsyncTask<GeolocationEventsTable, Void, Void> {

        @Override
        protected Void doInBackground(GeolocationEventsTable... events) {
            geolocationEventsDao.insertEvent(events[0]);
            return null;
        }
    }

    public void delete(int id) {
        geolocationEventsDao.delete(id);
    }

    public void updateEvent(int distance, String coordinates, int interval, int workoutId, int pk) {
        geolocationEventsDao.updateEvent(distance, coordinates, interval, workoutId, pk);
    }


}
