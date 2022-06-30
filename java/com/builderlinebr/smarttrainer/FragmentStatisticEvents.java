package com.builderlinebr.smarttrainer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.adapters.StatisticEventsAdapter;
import com.builderlinebr.smarttrainer.customviews.ItemTouchHelperCallback2;
import com.builderlinebr.smarttrainer.database.Exercises;
import com.builderlinebr.smarttrainer.database.WorkoutExercises;
import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseEvent;
import com.builderlinebr.smarttrainer.viewmodel.EventsViewModel;

import java.util.List;

@SuppressLint("ValidFragment")
public class FragmentStatisticEvents extends Fragment {

    RecyclerView recyclerView;
    public List<WorkoutExerciseEvent> events;

    Context context;
    View view;
    LinearLayout waitLayout;

    public FragmentStatisticEvents(Context context) {
        this.context = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_statistic_events, container, false);

        waitLayout = view.findViewById(R.id.wait_layout);
        waitLayout.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                initList();
                waitLayout.setVisibility(View.GONE);
            }
        });


        return view;
    }

    private void initList() {
        EventsViewModel eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        String date = ((StatisticActivity) context).currentDate; //((StatisticActivity) context).dateItems.get(((StatisticActivity) context).dateItems.size() - 2);
        if (date != null) {
            String dBegin = date + " 00:00:00";
            String dEnd = date + " 23:59:59";

            events = eventsViewModel.getWorkoutExerciseEventsByWorkoutIdAndDates(((StatisticActivity) context).workoutId, dBegin, dEnd);
        }

        for (int i = 0; i < events.size(); i++) {
            for (WorkoutExercises workoutExercise : ((StatisticActivity) context).workoutExercises) {
                if (events.get(i).getWorkoutExercisePk() == workoutExercise.getPk()) {
                    for (Exercises exercise : ((StatisticActivity) context).exercises) {
                        if (exercise.getId() == workoutExercise.getExercisesId()) {
                            events.get(i).exercise = exercise;
                        }
                    }

                }

            }
        }

        recyclerView = view.findViewById(R.id.events_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        StatisticEventsAdapter adapter = new StatisticEventsAdapter(context, events);
        recyclerView.setAdapter(adapter);

        // Для смахивания items
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback2(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }
}
