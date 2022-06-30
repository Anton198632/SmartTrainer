package com.builderlinebr.smarttrainer.model.exercises;

import com.builderlinebr.smarttrainer.database.Exercises;

import java.util.ArrayList;
import java.util.List;

public class ExerciseEventStatistic {

    public Exercises exercise;
    public List<DateEvent> dateEvents;


    public static class DateEvent {
        public DateEvent(String date) {
            this.date = date;
            this.counts = new ArrayList<>();
            this.values = new ArrayList<>();
            this.interval = new ArrayList<>();
        }

        public String date;
        public List<Float> counts;
        public List<Float> values;
        public List<Long> interval;

    }

}
