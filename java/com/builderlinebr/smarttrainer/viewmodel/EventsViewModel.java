package com.builderlinebr.smarttrainer.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.builderlinebr.smarttrainer.calculation.CalcTime;
import com.builderlinebr.smarttrainer.database.Events;
import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseEvent;
import com.builderlinebr.smarttrainer.repository.local.EventsRepository;

import java.util.ArrayList;
import java.util.List;

public class EventsViewModel extends AndroidViewModel {

    List<WorkoutExerciseEvent> exerciseEvents;
    EventsRepository eventsRepository;

    public EventsViewModel(@NonNull Application application) {
        super(application);
        eventsRepository = new EventsRepository(application);
    }

    public List<WorkoutExerciseEvent> getWorkoutExerciseEventsByWorkoutId(int workoutId, int dateTime) {
        exerciseEvents = new ArrayList<>();

        List<Events> events = eventsRepository.selectByWorkoutId(workoutId, dateTime);
        for (Events event : events) {
            WorkoutExerciseEvent weEvent = new WorkoutExerciseEvent();
            weEvent.setDateTime(event.getDateTime());
            weEvent.setCount(event.getCount());
            weEvent.setInterval(event.getInterval());
            weEvent.setValue((float) event.getValue() / 100f);
            weEvent.setWorkoutExercisePk(event.getWorkoutExercisePk());
            weEvent.setWorkoutId(event.getWorkoutId());
            weEvent.setPk(event.getPk());
            exerciseEvents.add(weEvent);
        }
        return exerciseEvents;
    }


    public List<WorkoutExerciseEvent> getWorkoutExerciseEventsByWorkoutIdAndDates(int workoutId, String dateBegin, String dateEnd) {


        long dateB = new CalcTime().convertDateTimeToLong(dateBegin);
        long dateE = new CalcTime().convertDateTimeToLong(dateEnd);

        exerciseEvents = new ArrayList<>();
        List<Events> events = eventsRepository.selectByWorkoutId(workoutId, (int) dateB, (int) dateE);
        for (Events event : events) {
            WorkoutExerciseEvent weEvent = new WorkoutExerciseEvent();
            weEvent.setDateTime(event.getDateTime());
            weEvent.setCount(event.getCount());
            weEvent.setInterval(event.getInterval());
            weEvent.setValue((float) event.getValue() / 100f);
            weEvent.setWorkoutExercisePk(event.getWorkoutExercisePk());
            weEvent.setWorkoutId(event.getWorkoutId());
            weEvent.setPk(event.getPk());
            String date = new CalcTime().convertLongToDate(event.getDateTime());
            weEvent.dateTimeString = date;
            exerciseEvents.add(weEvent);
        }
        return exerciseEvents;
    }

    public void insertNewEvent(WorkoutExerciseEvent weEvent) {
        Events event = new Events();
        event.setCount(weEvent.getCount());
        event.setDateTime((int) weEvent.getDateTime());
        event.setInterval((int) weEvent.getInterval());
        event.setValue((int) (weEvent.getValue() * 100));
        event.setWorkoutExercisePk(weEvent.getWorkoutExercisePk());
        event.setWorkoutId(weEvent.getWorkoutId());

        eventsRepository.addEvent(event);
    }

    public void delete(int id) {
        eventsRepository.delete(id);
    }

    public void updateEvent(int count, float value, long interval, int pk) {
        eventsRepository.updateEvent(count, (int) (value * 100), (int) interval, pk);
    }


}
