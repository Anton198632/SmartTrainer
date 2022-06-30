package com.builderlinebr.smarttrainer.repository.local;

import android.app.Application;
import android.os.AsyncTask;
import com.builderlinebr.smarttrainer.database.Database;
import com.builderlinebr.smarttrainer.database.Events;
import com.builderlinebr.smarttrainer.database.EventsDao;

import java.util.List;

public class EventsRepository {

    private List<Events> eventsList;
    private static EventsDao eventsDao;

    public EventsRepository(Application application) {
        Database database = Database.getDatabase(application);
        eventsDao = database.eventsDao();
    }

    public List<Events> selectByWorkoutId(int workoutId, int dateTime) {
        eventsList = eventsDao.selectByWorkoutId(workoutId, dateTime);
        return eventsList;
    }

    public List<Events> selectByWorkoutId(int workoutId, int dateBegin, int dateEnd) {
        eventsList = eventsDao.selectByWorkoutId(workoutId, dateBegin, dateEnd);
        return eventsList;
    }

    public void addEvent(Events event) {
        new AddEventAsync().execute(event);
    }

    private static class AddEventAsync extends AsyncTask<Events, Void, Void> {

        @Override
        protected Void doInBackground(Events... events) {
            eventsDao.insertEvent(events[0]);
            return null;
        }
    }

    public void delete(int id) {
        eventsDao.delete(id);
    }

    public void updateEvent(int count, int value, int interval, int pk) {
        eventsDao.updateEvent(count, value, interval, pk);
    }

}
