package com.builderlinebr.smarttrainer;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.*;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.adapters.DateAdapter;
import com.builderlinebr.smarttrainer.calculation.CalcCollections;
import com.builderlinebr.smarttrainer.calculation.CalcStatistic;
import com.builderlinebr.smarttrainer.calculation.CalcTime;
import com.builderlinebr.smarttrainer.customviews.CenterZoomLayoutManager;
import com.builderlinebr.smarttrainer.customviews.MiddleItemFinder;
import com.builderlinebr.smarttrainer.database.Exercises;
import com.builderlinebr.smarttrainer.database.WorkoutExercises;
import com.builderlinebr.smarttrainer.model.exercises.ExerciseStatistic;
import com.builderlinebr.smarttrainer.model.exercises.WorkoutExerciseEvent;
import com.builderlinebr.smarttrainer.viewmodel.EventsViewModel;
import com.builderlinebr.smarttrainer.viewmodel.ExercisesViewModel;
import com.builderlinebr.smarttrainer.viewmodel.WorkoutExercisesViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class StatisticActivity extends AppCompatActivity {

    private AdView mAdView;

    public static final int TYPE_COUNT = 0;
    public static final int TYPE_VALUE = 1;
    public static final int TYPE_INTERVAL = 2;
    public static final int TYPE_AVERAGE = 3;
    public static final int TYPE_ALL = 4;

    public List<String> dateItems;
    public List<Integer> exercisesIds;
    public WorkoutExercisesViewModel workoutExercisesViewModel;
    public ExercisesViewModel exercisesViewModel;
    public List<WorkoutExercises> workoutExercises;

    public int workoutId;


    public List<WorkoutExerciseEvent> events;
    public List<Exercises> exercises;

    public List<ExerciseStatistic> exercisesStatistic;
    public List<ExerciseStatistic> oldExercisesStatistic;

    public String title;

    Toolbar toolbar;

    BottomNavigationView bottomNavigationView;

    RecyclerView recyclerView;
    public int centralPosition;

    public String currentDate;

    LinearLayout waitLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        waitLayout = findViewById(R.id.wait_layout);
        waitLayout.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                initComponents();
                waitLayout.setVisibility(View.GONE);
            }
        });


        // Показ рекламы
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (getResources().getBoolean(R.bool.ad_use))
            mAdView.loadAd(adRequest);
        else mAdView.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.statistic_menu, menu);
        return true;
    }


    private void initComponents() {
        workoutId = getIntent().getIntExtra("workoutId", -1); // Получаем переданный идентификатор тренировки
        title = getIntent().getStringExtra("workoutName");


        EventsViewModel eventsViewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        events = eventsViewModel.getWorkoutExerciseEventsByWorkoutId(workoutId, 0);

        // получаем id упражнениц тренировки из БД
        workoutExercisesViewModel = new ViewModelProvider(this).get(WorkoutExercisesViewModel.class);
        workoutExercises = workoutExercisesViewModel.selectAllExercises(workoutId);

        // ----- Получаем список уникальных дат, когда была тренировка, для верхнего recyclerView
        dateItems = new ArrayList<>();
        dateItems.add(null);
        for (int i = 0; i < events.size(); i++) {

            String date = new CalcTime().convertLongToDate(events.get(i).getDateTime());
            if (new CalcCollections().getWorkoutExerciseEvent(events, date) == null) {
                dateItems.add(date);
            }
            events.get(i).dateTimeString = date;
        }
        dateItems.add(null);


        currentDate = dateItems.get(dateItems.size() - 2);
        if (currentDate != null) {
            String dBegin = "01-01-1970 3:00:00";
            String dEnd = currentDate + " 23:59:59";

            if (dateItems.size() > 3 && dateItems.get(dateItems.size() - 3) != null) {
                dBegin = dateItems.get(dateItems.size() - 3) + " 0:00:00";
            }
            events = eventsViewModel.getWorkoutExerciseEventsByWorkoutIdAndDates((StatisticActivity.this).workoutId, dBegin, dEnd);
        }


        // ----- Получаем уникальные упражнения
        exercises = new ArrayList<>();
        exercisesIds = new ArrayList<>();
        for (WorkoutExercises workoutExercise : workoutExercises) {
            if (!new CalcCollections().findExerciseId(exercisesIds, workoutExercise.getExercisesId())) {
                exercisesIds.add(workoutExercise.getExercisesId());
                exercisesViewModel = new ViewModelProvider(this).get(ExercisesViewModel.class);
                Exercises exer = exercisesViewModel.getExerciseById(workoutExercise.getExercisesId());
                exercises.add(exer);
            }
        }


        if (dateItems.size() > 2) {
            exercisesStatistic = new CalcStatistic().getExercisesStatistic(workoutExercises, exercises, events, dateItems.get(dateItems.size() - 2));
        }
        if (dateItems.size() > 3) {
            oldExercisesStatistic = new CalcStatistic().getExercisesStatistic(workoutExercises, exercises, events, dateItems.get(dateItems.size() - 3));
        }

        // ----- Toolbar
        toolbar = (Toolbar) findViewById(R.id.statistic_toolbar);
        toolbar.setTitle("Статистика");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Настраиваем нижнюю навигацию
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bn_item_graph:
                        updateFragment(new FragmentStatisticGraphics(StatisticActivity.this));
                        break;
                    case R.id.bn_item_table:
                        updateFragment(new FragmentStatisticTables(StatisticActivity.this));
                        break;
                    case R.id.bn_item_list:
                        updateFragment(new FragmentStatisticEvents(StatisticActivity.this));
                        break;
                }
                return true;
            }
        });


        // ----- Заполняем верхний recyclerView с датами
        recyclerView = findViewById(R.id.date_recycler_view);

        LinearLayoutManager layoutManager = new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        Display display = getWindowManager().getDefaultDisplay(); // получаем размер  экрана
        Point size = new Point();
        display.getSize(size);
        int padding = size.x / 2 - 100; // вычисляем отступ (рамер нулвого пустого элемента) для расположения первого элемента посреди экрана.
        // 100 - половина ширины изображения
        DateAdapter myAdapter = new DateAdapter(dateItems, this, padding);
        recyclerView.setAdapter(myAdapter);
        recyclerView.scrollToPosition(dateItems.size() - 2);


        // ----- Для получения центрального видимого элемента--------
        MiddleItemFinder.MiddleItemCallback callbackM =
                new MiddleItemFinder.MiddleItemCallback() {
                    @Override
                    public void scrollFinished(int middleElement) { // вычисляем позицию центрального элемента.
                        if (centralPosition != middleElement) {
                            centralPosition = middleElement;
                            currentDate = dateItems.get(centralPosition);
                            if (currentDate != null) {

                                String dBegin = "01-01-1970 3:00:00";
                                String dEnd = currentDate + " 23:59:59";
                                if (dateItems.get(centralPosition - 1) != null) {
                                    dBegin = dateItems.get(centralPosition - 1) + " 0:00:00";
                                }

                                EventsViewModel eventsViewModel = new ViewModelProvider(StatisticActivity.this).get(EventsViewModel.class);
                                events = eventsViewModel.getWorkoutExerciseEventsByWorkoutIdAndDates((StatisticActivity.this).workoutId, dBegin, dEnd);

                                exercisesStatistic = new CalcStatistic().getExercisesStatistic(
                                        workoutExercises,
                                        exercises,
                                        events,
                                        dateItems.get(centralPosition));
                                oldExercisesStatistic = null;
                                if (dateItems.get(centralPosition - 1) != null)
                                    oldExercisesStatistic = new CalcStatistic().getExercisesStatistic(
                                            workoutExercises,
                                            exercises,
                                            events,
                                            dateItems.get(centralPosition - 1));


                                switch (bottomNavigationView.getSelectedItemId()) {
                                    case R.id.bn_item_graph:
                                        updateFragment(new FragmentStatisticGraphics(StatisticActivity.this));
                                        break;
                                    case R.id.bn_item_table:
                                        updateFragment(new FragmentStatisticTables(StatisticActivity.this));
                                        break;
                                    case R.id.bn_item_list:
                                        updateFragment(new FragmentStatisticEvents(StatisticActivity.this));
                                        break;
                                }


                            }
                        }
                    }
                };
        recyclerView.addOnScrollListener(
                new MiddleItemFinder(this, layoutManager,
                        callbackM, RecyclerView.SCROLL_STATE_IDLE));

        // ----------------------------


        // Подключаем фрагмент графиков
        updateFragment(new FragmentStatisticGraphics(this));
    }

    private void updateFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.statistic_fragment, fragment).commit();
    }


    public void reCalcExerciseStatistic() {
        if (dateItems.size() > 2) {
            exercisesStatistic = new CalcStatistic().getExercisesStatistic(workoutExercises, exercises, events, dateItems.get(dateItems.size() - 2));
        }
        if (dateItems.size() > 3) {
            oldExercisesStatistic = new CalcStatistic().getExercisesStatistic(workoutExercises, exercises, events, dateItems.get(dateItems.size() - 3));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.share_item: {
                toShareResult();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void toShareResult() {
        Bitmap bitmap0 = null;
        Bitmap bitmap1 = null;
        Bitmap bitmap2 = null;
        Bitmap bitmap3 = null;
        Bitmap bitmap4 = null;
        String fileName = currentDate != null ? currentDate : "_";
        float x = 20;

        String fragmentName = getSupportFragmentManager().findFragmentById(R.id.statistic_fragment).getClass().getName();
        switch (fragmentName) {
            case "com.example.smarttrainer.FragmentStatisticGraphics":
                FragmentStatisticGraphics fragment = (FragmentStatisticGraphics) getSupportFragmentManager().findFragmentById(R.id.statistic_fragment);

                bitmap0 = getBitmapFromView(fragment.view.findViewById(R.id.card_view0)); //getRecyclerViewScreenshot(fragment.linerChartRecyclerView);
                bitmap1 = getBitmapFromView(fragment.view.findViewById(R.id.card_view1));
                bitmap2 = getBitmapFromView(fragment.view.findViewById(R.id.card_view2));
                bitmap0 = overlay(bitmap0, bitmap1);
                bitmap0 = overlay(bitmap0, bitmap2);

                int count = fragment.linerChartRecyclerView.getChildCount();
                for (int i = 0; i < count; i++) {
                    bitmap3 = getBitmapFromView(fragment.linerChartRecyclerView.getLayoutManager().findViewByPosition(i).findViewById(R.id.statistic_exercise_name));
                    bitmap4 = getBitmapFromView(fragment.linerChartRecyclerView.getLayoutManager().findViewByPosition(i).findViewById(R.id.def));
                    bitmap0 = overlay(bitmap0, bitmap3);
                    bitmap0 = overlay(bitmap0, bitmap4);
                }


                fileName = "Результаты тренировки " + fileName + " (графики)";
                x = bitmap0.getWidth() / 2;
                break;
            case "com.example.smarttrainer.FragmentStatisticTables":
                FragmentStatisticTables fragment1 = (FragmentStatisticTables) getSupportFragmentManager().findFragmentById(R.id.statistic_fragment);
                bitmap0 = getBitmapFromView(fragment1.view.findViewById(R.id.tables_card_view0)); //getRecyclerViewScreenshot(fragment.linerChartRecyclerView);
                bitmap1 = getBitmapFromView(fragment1.view.findViewById(R.id.tables_card_view1));
                bitmap2 = getBitmapFromView(fragment1.view.findViewById(R.id.tables_card_view2));
                bitmap0 = overlay(bitmap0, bitmap1);
                bitmap0 = overlay(bitmap0, bitmap2);

                int count1 = fragment1.exerciseStatisticTable.getChildCount();
                for (int i = 0; i < count1; i++) {
                    bitmap3 = getBitmapFromView(fragment1.exerciseStatisticTable.getLayoutManager().findViewByPosition(i).findViewById(R.id.tables_exercise_name));
                    bitmap4 = getBitmapFromView(fragment1.exerciseStatisticTable.getLayoutManager().findViewByPosition(i).findViewById(R.id.tables_exercise_data));
                    bitmap0 = overlay(bitmap0, bitmap3);
                    bitmap0 = overlay(bitmap0, bitmap4);
                }

                fileName = "Результаты тренировки " + fileName + " (таблицы)";
                x = bitmap0.getWidth() / 2;
                break;
            case "com.example.smarttrainer.FragmentStatisticEvents":
                FragmentStatisticEvents fragment2 = (FragmentStatisticEvents) getSupportFragmentManager().findFragmentById(R.id.statistic_fragment);

                bitmap0 = Bitmap.createBitmap(fragment2.view.getWidth(), 450,
                        Bitmap.Config.ARGB_8888);

                bitmap1 = getBitmapFromView(fragment2.view.findViewById(R.id.event_statistic_recyclerview_header));
                bitmap2 = getRecyclerViewScreenshot(fragment2.recyclerView);
                bitmap0 = overlay(bitmap0, bitmap1);
                bitmap0 = overlay(bitmap0, bitmap2);
                fileName = "Результаты тренировки " + fileName + " (список упражнений)";
                break;
        }

        Canvas c = new Canvas(bitmap0);
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.colorPrimary));
        p.setStyle(Paint.Style.FILL); // очертание (нет заливки)
        p.setStrokeWidth(2f); // толщина линии
        p.setAntiAlias(true); // автосглаживание
        p.setTextSize(70f);
        c.drawText("Создано при помощи", x, 200, p);
        c.drawText("приложения", x, 280, p);
        c.drawText("SmartTrainer", x, 360, p);


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(this, bitmap0, fileName));
        shareIntent.setType("application/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Мои результаты"));
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);

        bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        c.drawColor(Color.WHITE);
        view.draw(c);

        view.setDrawingCacheEnabled(false);

        return bitmap;

    }


    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth() > bmp2.getWidth() ? bmp1.getWidth() : bmp2.getWidth(), bmp1.getHeight() + bmp2.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
        return bmOverlay;
    }


    public Uri getImageUri(Context inContext, Bitmap inImage, String title) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }

    public static Bitmap getRecyclerViewScreenshot(RecyclerView view) {
        int size = view.getAdapter().getItemCount();
        RecyclerView.ViewHolder holder = view.getAdapter().createViewHolder(view, 0);
        view.getAdapter().onBindViewHolder(holder, 0);
        holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
        Bitmap bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), holder.itemView.getMeasuredHeight() * size,
                Bitmap.Config.ARGB_8888);
        Canvas bigCanvas = new Canvas(bigBitmap);
        bigCanvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        int iHeight = 0;
        holder.itemView.setDrawingCacheEnabled(true);
        holder.itemView.buildDrawingCache();
        bigCanvas.drawBitmap(holder.itemView.getDrawingCache(), 0f, iHeight, paint);
        holder.itemView.setDrawingCacheEnabled(false);
        holder.itemView.destroyDrawingCache();
        iHeight += holder.itemView.getMeasuredHeight();
        for (int i = 1; i < size; i++) {
            view.getAdapter().onBindViewHolder(holder, i);
            holder.itemView.setDrawingCacheEnabled(true);
            holder.itemView.buildDrawingCache();
            bigCanvas.drawBitmap(holder.itemView.getDrawingCache(), 0f, iHeight, paint);
            iHeight += holder.itemView.getMeasuredHeight();
            holder.itemView.setDrawingCacheEnabled(false);
            holder.itemView.destroyDrawingCache();
        }
        return bigBitmap;
    }
}
