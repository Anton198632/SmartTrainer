package com.builderlinebr.smarttrainer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.*;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.adapters.WorkoutExercisesAdapter;
import com.builderlinebr.smarttrainer.calculation.CalcCollections;
import com.builderlinebr.smarttrainer.calculation.CalcTime;
import com.builderlinebr.smarttrainer.customviews.CenterZoomLayoutManager;
import com.builderlinebr.smarttrainer.customviews.MiddleItemFinder;
import com.builderlinebr.smarttrainer.customviews.SimpleItemTouchHelperCallback;
import com.builderlinebr.smarttrainer.database.Exercises;
import com.builderlinebr.smarttrainer.database.WorkoutExercises;
import com.builderlinebr.smarttrainer.database.Workouts;
import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseEvent;
import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseEventsObject;
import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseModel;
import com.builderlinebr.smarttrainer.viewmodel.EventsViewModel;
import com.builderlinebr.smarttrainer.viewmodel.ExercisesViewModel;
import com.builderlinebr.smarttrainer.viewmodel.WorkoutExercisesViewModel;
import com.builderlinebr.smarttrainer.viewmodel.WorkoutsViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.util.*;

public class WorkoutExercisesActivity extends AppCompatActivity {

    private AdView mAdView;

    public static final int REQUEST_CODE_GET_EXERCISE = 23;

    public RecyclerView recyclerView;
    public List<WorkoutExerciseModel> items;
    public List<Integer> selectItems;

    public List<WorkoutExercises> workoutExercises;
    int workoutId;
    public int forGirl;
    public int isMy;
    public WorkoutExercisesViewModel workoutExercisesViewModel;
    public ExercisesViewModel exercisesViewModel;
    public TextView exerciseHeaderTextView;
    public TextView timerTextId;
    public FloatingActionButton fab;

    public Toolbar toolbar;
    public ActionBar actionBar;

    public Thread thread;
    public long interval;
    public long intervalPre;

    public List<WorkoutExerciseEvent> workoutEvents;
    private WorkoutExerciseEventsObject workoutExerciseEventsObject; // Для сохранения списка событий в объекте при изменении ориентации экрана
    public List<WorkoutExerciseEvent> workoutEventsOld;
    public EventsViewModel eventsViewModel;

    public int allPixel;

    public TextView countTextView;
    public TextView valueTextView;
    public TextView timeTextView;
    public TextView countText;
    public TextView valueText;
    public TextView timeText;
    public TextView oldCountText;
    public TextView oldValueText;
    public TextView oldTimeText;

    public String title;

    public SeekBar seekBar;

    public int centralPosition;

    public PowerManager.WakeLock mWakeLock; // Для запрета гасить экран во время тренировки

    LinearLayout waitLayout;

    public static boolean isReplaceActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_exercises);

        waitLayout = findViewById(R.id.wait_layout);
        waitLayout.setVisibility(View.VISIBLE);
        fab = findViewById(R.id.fab_start);
        seekBar = findViewById(R.id.seekBar);
        exerciseHeaderTextView = findViewById(R.id.exercise_header_we);
        timerTextId = findViewById((R.id.timer_text_id));
        toolbar = (Toolbar) findViewById(R.id.toolbar_workout_exercise_id);
        countTextView = findViewById(R.id.count_text_view);
        recyclerView = findViewById(R.id.workout_exercise_recyclerView);
        isMy = 0;

        if (!isReplaceActivity) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    isReplaceActivity = false;
                    initComponents();
                    waitLayout.setVisibility(View.GONE);
                }
            });
        } else {
            initComponents();
            waitLayout.setVisibility(View.GONE);
        }

        // Показ рекламы
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (getResources().getBoolean(R.bool.ad_use))
            mAdView.loadAd(adRequest);
        else mAdView.setVisibility(View.GONE);

    }


    private void initComponents() {
        workoutId = getIntent().getIntExtra("workoutId", -1); // Получаем переданный идентификатор тренировки
        forGirl = getIntent().getIntExtra("forGirl", 0);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                recyclerView.scrollToPosition(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Инициализируем recyclerView и заполняем списки
        initRecyclerView(0, true);

        selectItems = new ArrayList<>();


        if (items.size() > 2) {
            exerciseHeaderTextView.setText(items.get(1).exercise.getHeader());
        }

        timerTextId.setVisibility(View.INVISIBLE);

        // ------ ToolBar --------------------------------------

        toolbar.setTitle("");
        TextView textView = toolbar.findViewById(R.id.workout_exercise_header_toolbar);
        title = getIntent().getStringExtra("workoutName");
        textView.setText(title);

        CircleImageView imageHeader = toolbar.findViewById(R.id.workout_exercise_image_toolbar);
        WorkoutsViewModel workoutsViewModel = new ViewModelProvider(this).get(WorkoutsViewModel.class);
        List<Workouts> workouts = workoutsViewModel.getById(workoutId);
        if (workouts.size() > 0) {
            String photoFile = getExternalFilesDir("").getPath() + "/images/" + workouts.get(0).getWorkoutImage();
            File file = new File(photoFile);
            if (file.exists()) {
                if (!workouts.get(0).getWorkoutImage().equals("")) {
                    Uri uri = Uri.fromFile(file);
                    Picasso.get().load(uri).into(imageHeader);
                } else {
                    imageHeader.setVisibility(View.GONE);
                }
            } else {
                imageHeader.setVisibility(View.GONE);
            }
            isMy = workouts.get(0).getIsMy();
        }
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // ---------------------------------------------------------


        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InvalidWakeLockTag")
            @Override
            public void onClick(View v) {

                if (thread != null) {
                    showConfirmDialog(false);

                } else {
                    fab.setImageResource(R.drawable.ic_stop);
                    timerTextId.setVisibility(View.VISIBLE);
                    interval = 0;
                    thread = new Thread(runnable);
                    thread.start();
                    replaceMenu();

                    // Включем запрет перехода в спящий режим
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
                    mWakeLock.acquire();

                }
                recyclerView.getAdapter().notifyDataSetChanged();

            }
        });


        countTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //   countTextView.setOutlineProvider(shadowProvider);
        valueTextView = findViewById(R.id.value_text_view);
        //   valueTextView.setOutlineProvider(shadowProvider);
        timeTextView = findViewById(R.id.time_text_view);
        //    timeTextView.setOutlineProvider(shadowProvider);
        countText = findViewById(R.id.text_count);
        valueText = findViewById(R.id.text_value);
        timeText = findViewById(R.id.text_time);
        oldCountText = findViewById(R.id.old_count_text_view);
        oldValueText = findViewById(R.id.old_value_text_view);
        oldTimeText = findViewById(R.id.old_time_text_view);


        setVisibilityData(View.INVISIBLE);
    }

    public ViewOutlineProvider shadowProvider = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            int w = view.getWidth();
            int h = view.getHeight();
            Rect rect = new Rect(0, 0, w, h);

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            float px = 8f * (metrics.densityDpi / 160f);

            outline.setRoundRect(rect, px);
        }
    };


    public void initRecyclerView(int scrollPosition, boolean scroll) {
        // ---- Заполняем RecyclerView -----------------------------------

        LinearLayoutManager layoutManager = new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // получаем id упражнениц тренировки из БД
        workoutExercisesViewModel = new ViewModelProvider(this).get(WorkoutExercisesViewModel.class);
        workoutExercises = workoutExercisesViewModel.selectAllExercises(workoutId);

        items = new ArrayList<>();

        items.add(null);
        WorkoutExerciseModel modelAdd = new WorkoutExerciseModel();
        for (WorkoutExercises wExercise : workoutExercises) {
            modelAdd = new WorkoutExerciseModel();
            // Получем упраженение по id упраженения
            exercisesViewModel = new ViewModelProvider(this).get(ExercisesViewModel.class);
            Exercises exer = exercisesViewModel.getExerciseById(wExercise.getExercisesId());
            modelAdd.exercise = exer;
            modelAdd.pk = wExercise.getPk();
            modelAdd.workoutId = workoutId;
            modelAdd.typeItem = 0;
            modelAdd.order = wExercise.getOrder();
            items.add(modelAdd);
        }

        // Добавляем кнопку "добавить"
//        modelAdd = new WorkoutExerciseModel();
//        modelAdd.typeItem = 1;
//        items.add(modelAdd);
        items.add(null);


        Display display = getWindowManager().getDefaultDisplay(); // получаем размер  экрана
        Point size = new Point();
        display.getSize(size);
        int padding = size.x / 2 - 200; // вычисляем отступ (рамер нулвого пустого элемента) для расположения первого элемента посреди экрана.
        // 200 - половина ширины изображения
        WorkoutExercisesAdapter workoutExercisesAdapter = new WorkoutExercisesAdapter(items, this, padding);
        recyclerView.setAdapter(workoutExercisesAdapter);

        // Для перемещения items между собой
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(workoutExercisesAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        // ----- Для получения центрального видимого элемента--------
        MiddleItemFinder.MiddleItemCallback callbackM =
                new MiddleItemFinder.MiddleItemCallback() {
                    @Override
                    public void scrollFinished(int middleElement) { // вычисляем позицию центрального элемента.
                        centralPosition = middleElement;
                        WorkoutExerciseModel workoutExerciseModel = items.get(centralPosition);
                        if (workoutExerciseModel != null) {
                            exerciseHeaderTextView.setText(workoutExerciseModel.exercise.getHeader());

                            if (!setInvokeData(workoutExerciseModel.pk)) {
                                setVisibilityData(View.INVISIBLE);
                            }

                        }
                        seekBar.setProgress(centralPosition - 1);
                    }
                };
        recyclerView.addOnScrollListener(
                new MiddleItemFinder(this, layoutManager,
                        callbackM, RecyclerView.SCROLL_STATE_IDLE));

        // ----------------------------

        seekBar.setMax(items.size() - 3);


        if (scroll)
            recyclerView.scrollToPosition(scrollPosition);


        // --------- Формируем список новых и старых событий выполнения упражнений -----------
        workoutEvents = new ArrayList<>();
        workoutEventsOld = new ArrayList<>();
        eventsViewModel = new ViewModelProvider(WorkoutExercisesActivity.this).get(EventsViewModel.class);
        List<WorkoutExerciseEvent> wOld = eventsViewModel.getWorkoutExerciseEventsByWorkoutId(workoutId, 0);

        //      for (int i = 1; i < wOld.size(); i++) {
        //        wOld.get(0).setValue(wOld.get(0).getValue()*100);
        //         eventsViewModel.insertNewEvent(wOld.get(0));
        //      }

        //     eventsViewModel.delete(41);

        for (WorkoutExerciseModel item : items) {
            if (item != null) {
                for (WorkoutExerciseEvent workoutExerciseEvent : wOld) {
                    if (item.pk == workoutExerciseEvent.getWorkoutExercisePk()) {
                        boolean find = false;
                        for (int w = 0; w < workoutEventsOld.size(); w++) {
                            if (workoutEventsOld.get(w).getWorkoutExercisePk() == workoutExerciseEvent.getWorkoutExercisePk()) {
                                workoutEventsOld.set(w, workoutExerciseEvent);
                                find = true;
                                break;
                            }
                        }
                        if (!find) {
                            workoutEventsOld.add(workoutExerciseEvent);
                        }
                    }
                }
            }
        }


        // -------------------------------------------------

    }


    public boolean setInvokeData(int pk) {
        boolean find = false;
        for (WorkoutExerciseEvent workoutEvent : workoutEvents) {
            if (workoutEvent.getWorkoutExercisePk() == pk) {
                setVisibilityData(View.VISIBLE);

                int currentCount = workoutEvent.getCount();
                float currentValue = workoutEvent.getValue();
                long currentInterval = workoutEvent.getInterval();

                int oldCount = 0;
                float oldValue = 0;
                long oldInterval = 0;


                countTextView.setText(Integer.toString(currentCount));
                valueTextView.setText(Float.toString(currentValue));
                timeTextView.setText(workoutEvent.dateTimeString);

                oldCountText.setText(Integer.toString(0));
                oldValueText.setText(Integer.toString(0));
                oldTimeText.setText("");

                WorkoutExerciseEvent oldEvent = new CalcCollections().getWorkoutExerciseEvent(workoutEventsOld, pk);
                if (oldEvent != null) {
                    oldCount = oldEvent.getCount();
                    oldValue = oldEvent.getValue();
                    oldInterval = oldEvent.getInterval();

                    oldCountText.setText(Integer.toString(oldCount));
                    oldValueText.setText(Float.toString(oldValue));
                    String interval = new CalcTime().ConvertLongToTimeString(oldInterval);
                    oldTimeText.setText(interval);
                }

                setColorTextView(currentCount, oldCount, countTextView);
                setColorTextView(currentValue, oldValue, valueTextView);
                setColorTextView(oldInterval, currentInterval, timeTextView);

                find = true;
                break;
            }
        }
        return find;
    }

    private void setColorTextView(float current, float old, TextView textView) {
        if (current > old) {
            textView.setTextColor(Color.GREEN);
        } else if (current < old) {
            textView.setTextColor(Color.RED);
        } else {
            textView.setTextColor(Color.WHITE);
        }
    }


    public void setVisibilityData(int visibility) {
        countTextView.setVisibility(visibility);
        valueTextView.setVisibility(visibility);
        timeTextView.setVisibility(visibility);
        countText.setVisibility(visibility);
        valueText.setVisibility(visibility);
        timeText.setVisibility(visibility);
        oldCountText.setVisibility(visibility);
        oldValueText.setVisibility(visibility);
        oldTimeText.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.workout_exercise_menu3);
        } else if (thread != null) {
            toolbar.getMenu().clear();
        }

    }

    public void showConfirmDialog(final boolean finish) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogWorkout);
        builder.setCancelable(true)

                .setMessage(getString(R.string.message_dialog_stop))
                .setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("Не сохранять", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        thread.interrupt();
                        fab.setImageResource(R.drawable.ic_start);
                        thread = null;
                        workoutEvents = new ArrayList<>();
                        timerTextId.setVisibility(View.INVISIBLE);
                        setVisibilityData(View.INVISIBLE);

                        if (finish) {
                            finish();

                        }

                        recyclerView.getAdapter().notifyDataSetChanged();
                        replaceMenu();

                        mWakeLock.release(); // разрешить спящий режим экрана

                        isReplaceActivity = false;
                    }
                })
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        thread.interrupt();
                        fab.setImageResource(R.drawable.ic_start);
                        thread = null;

                        timerTextId.setVisibility(View.INVISIBLE);
                        setVisibilityData(View.INVISIBLE);


                        //  Запись в БД результатов тренировки
                        for (int i = 0; i < workoutEvents.size(); i++) {
                            eventsViewModel.insertNewEvent(workoutEvents.get(i));
                        }


                        workoutEvents = new ArrayList<>();

                        if (finish) {
                            finish();

                        }

                        initRecyclerView(0, false);

                        //  recyclerView.getAdapter().notifyDataSetChanged();
                        replaceMenu();

                        mWakeLock.release(); // разрешить спящий режим экрана

                        isReplaceActivity = false;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workout_exercise_menu, menu);
        checkOldEvents(); // Если нет старых выполнений, то скрываем статистику
        return true;

    }

    private void checkOldEvents() {
        MenuItem item = toolbar.getMenu().findItem(R.id.workout_exercise_statistic);
        if (workoutEventsOld.size() == 0 && item != null) { // Если нет старых выполнений, то скрываем статистику
            item.setVisible(false);
        } else if (item != null) {
            item.setVisible(true);
        }
        MenuItem itemAdd = toolbar.getMenu().findItem(R.id.workout_exercise_add);
        if (isMy != 1 && itemAdd != null) { // Если не моя тренировка, то скрываем кнопку добавить в меню
            itemAdd.setVisible(false);
        } else if (itemAdd != null) {
            itemAdd.setVisible(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.workout_exercise_add: {
                Intent intent = new Intent(WorkoutExercisesActivity.this, ExercisesActivity.class);
                if (forGirl == 1) intent.putExtra("sex", "female");
                intent.putExtra("forAdd", true);
                WorkoutExercisesActivity.this.startActivityForResult(intent, WorkoutExercisesActivity.REQUEST_CODE_GET_EXERCISE);
                break;
            }
            case android.R.id.home: {
                if (thread != null) {
                    showConfirmDialog(true);
                } else {
                    finish();
                }

                break;
            }
            case R.id.exercises_copy: {
                for (Integer selectItem : selectItems) {
                    addNewExercise(items.get(selectItem).exercise.getId());
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
            }
            case R.id.exercises_delete: {
                List<WorkoutExerciseModel> itemsForDelete = new ArrayList<>();
                for (Integer selectItem : selectItems) {
                    // Удаляем из БД
                    WorkoutExercisesViewModel workoutExercisesViewModel = new ViewModelProvider(WorkoutExercisesActivity.this).get(WorkoutExercisesViewModel.class);
                    WorkoutExercises workoutExercises = new WorkoutExercises(
                            items.get(selectItem).pk,
                            items.get(selectItem).workoutId,
                            items.get(selectItem).exercise.getId(),
                            items.get(selectItem).order);
                    workoutExercisesViewModel.deleteExercise(workoutExercises);
                    // Формируем коллекцию для удаления
                    itemsForDelete.add(items.get(selectItem));
                }

                items.removeAll(itemsForDelete);
                ((WorkoutExercisesAdapter) recyclerView.getAdapter()).removeItems(itemsForDelete);

                selectItems = new ArrayList<>();
                seekBar.setMax(items.size() - 3);
                replaceMenu();
                break;
            }
            case R.id.exercises_clear: {
                selectItems = new ArrayList<>();
                recyclerView.getAdapter().notifyDataSetChanged();
                replaceMenu();
                break;
            }
            case R.id.exercises_cancel: {
                WorkoutExerciseEvent delWE = new WorkoutExerciseEvent();
                delWE.setWorkoutExercisePk(-1);
                for (WorkoutExerciseEvent workoutEvent : workoutEvents) {
                    if (workoutEvent.getWorkoutExercisePk() == items.get(centralPosition).pk) {
                        delWE = workoutEvent;
                    }
                }
                if (delWE.getWorkoutExercisePk() != -1)
                    workoutEvents.remove(delWE);

                setVisibilityData(View.INVISIBLE);

                recyclerView.getAdapter().notifyDataSetChanged();
                break;
            }
            case R.id.workout_exercise_statistic: {
                Intent intent = new Intent(this, StatisticActivity.class);
                intent.putExtra("workoutId", workoutId);
                intent.putExtra("workoutName", title);
                startActivity(intent);
            }
        }


        return super.onOptionsItemSelected(item);
    }

    // Для добавления упражнений из активити - каталога упражнений
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GET_EXERCISE: // если вернуляся ответ от активити с выбором упражнения
                if (data != null) {
                    int exerciseId = data.getIntExtra("exerciseId", -1); // получаем иденитификатор упражнения
                    addNewExercise(exerciseId);
                }
                break;
            case WorkoutExercisesAdapter.RESULT_FAVORITE:
                boolean isFavorite = data.getBooleanExtra("isFavorite", false);
                int exId = data.getIntExtra("exId", -1);
                int currentPosition = data.getIntExtra("currentPosition", -1);
                if (exId != -1 && currentPosition != -1) {
                    items.get(currentPosition).exercise.setFavorites(isFavorite ? 1 : 0);
                    Exercises currentExercise = items.get(currentPosition).exercise;
                    for (int i = 0; i < items.size(); i++) {

                        if (items.get(i) != null && items.get(i).exercise.getId() == exId) {
                            items.get(i).exercise.setFavorites(isFavorite ? 1 : 0);
                        }
                    }
                    // обновляем значение в БД
                    ExercisesViewModel viewModel = new ViewModelProvider(this).get(ExercisesViewModel.class);
                    viewModel.updateExercise(items.get(currentPosition).exercise);
                }
                break;
        }
    }


    private void addNewExercise(int exerciseId) {
        WorkoutExercises workoutExercises = new WorkoutExercises();
        workoutExercises.setWorkoutId(workoutId);
        workoutExercises.setExercisesId(exerciseId);

        int maxOrder = 0;
        for (WorkoutExerciseModel item : items) {

            if (item != null && item.order > maxOrder) maxOrder = item.order;
        }
        maxOrder += 1;
        workoutExercises.setOrder(maxOrder);

        workoutExercisesViewModel.insertExercise(workoutExercises);

        initRecyclerView(0, false);
        recyclerView.scrollToPosition(items.size() - 1);


    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            long milisec = Calendar.getInstance().getTimeInMillis();

            //  interval = 0;
            intervalPre = interval;
            milisec = Calendar.getInstance().getTimeInMillis();
            while (true) {
                try {

                    if (interval == 0) milisec = Calendar.getInstance().getTimeInMillis();

                    interval = intervalPre + (Calendar.getInstance().getTimeInMillis() - milisec);
                    final String timeString = new CalcTime().ConvertLongToTimeString(interval);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerTextId.setText(timeString);
                        }
                    });

                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (thread != null) {
            showConfirmDialog(true);
        } else {
            super.onBackPressed();
        }
    }


    public void replaceMenu() {
        if (thread == null) {
            if (selectItems.size() > 0) {
                toolbar.getMenu().clear();
                //if (isMy!=0)
                toolbar.inflateMenu(R.menu.workout_exercise_menu2);
            } else {
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.workout_exercise_menu);
            }
        } else {
            toolbar.getMenu().clear();
        }
        checkOldEvents(); // Если нет старых выполнений, то скрываем статистику
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (thread != null) {
            Gson gson = new Gson();
            workoutExerciseEventsObject = new WorkoutExerciseEventsObject(workoutEvents);
            String json = gson.toJson(workoutExerciseEventsObject);
            outState.putString("workoutEvents", json);
            outState.putLong("interval", interval);
            isReplaceActivity = true;
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            String json = savedInstanceState.getString("workoutEvents");
            if (json != null && !json.isEmpty()) {
                Gson gson = new Gson();
                workoutExerciseEventsObject = gson.fromJson(json, WorkoutExerciseEventsObject.class);
                workoutEvents = workoutExerciseEventsObject.getEvents();
            }
            interval = savedInstanceState.getLong("interval", 0);
            if (interval != 0) {

                fab.setImageResource(R.drawable.ic_stop);
                timerTextId.setVisibility(View.VISIBLE);
                thread = new Thread(runnable);
                thread.start();

                recyclerView.getAdapter().notifyDataSetChanged();
                replaceMenu();

                // Включем запрет перехода в спящий режим
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
                mWakeLock.acquire();


            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (mWakeLock != null) mWakeLock.release();
        } catch (Exception ex) {
        }
        super.onDestroy();
    }
}
