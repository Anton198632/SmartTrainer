package com.builderlinebr.smarttrainer.model.exercises;

import java.util.List;

public class WorkoutExerciseEventsObject {
    private List<WorkoutExerciseEvent> events;

    public WorkoutExerciseEventsObject(List<WorkoutExerciseEvent> events) {
        this.events = events;
    }

    public List<WorkoutExerciseEvent> getEvents() {
        return events;
    }
}
