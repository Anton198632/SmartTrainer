package com.builderlinebr.smarttrainer;

import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.builderlinebr.smarttrainer.customviews.CenterZoomLayoutManager;
import com.builderlinebr.smarttrainer.customviews.MiddleItemFinder;
import com.builderlinebr.smarttrainer.model.exercises.GeolocationEvents;
import com.builderlinebr.smarttrainer.viewmodel.GeolocationEventsViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yandex.mapkit.mapview.MapView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GeoStatisticActivity extends AppCompatActivity {

    private AdView mAdView;

    int workoutId;
    public List<GeolocationEvents> geoEvents;
    List<String> dates;

    RecyclerView dateRecyclerView;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;

    String currentData;
    String preDate;

    LinearLayout waitLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_statistic);

        workoutId = getIntent().getIntExtra("workoutId", -1);

        waitLayout = findViewById(R.id.wait_layout);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                waitLayout.setVisibility(View.VISIBLE);
                initComponents();
                waitLayout.setVisibility(View.GONE);
            }
        });

        // Показ рекламы
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (getResources().getBoolean(R.bool.ad_use)) {
            mAdView.loadAd(adRequest);
        } else mAdView.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.statistic_menu, menu);
        return true;
    }

    private void initComponents() {

        dateRecyclerView = findViewById(R.id.date_recycler_view);
        // Получаем события предедущих тренировок
        GeolocationEventsViewModel geolocationEventsViewModel = new ViewModelProvider(this).get(GeolocationEventsViewModel.class);
        geoEvents = geolocationEventsViewModel.getEventsByDistanceId(workoutId, 0);

        dates = new ArrayList<>();
        dates.add(null);
        for (GeolocationEvents geoEvent : geoEvents) {
            boolean find = false;
            for (String date : dates) {
                if (date != null && date.equals(geoEvent.getDateString())) {
                    find = true;
                    break;
                }
            }
            if (!find) dates.add(geoEvent.getDateString());
        }
        dates.add(null);

        currentData = dates.get(dates.size() - 2);
        preDate = dates.size() > 3 ? dates.get(dates.size() - 3) : "";

        // ----- Заполняем верхний recyclerView с датами
        dateRecyclerView = findViewById(R.id.date_recycler_view);

        LinearLayoutManager layoutManager = new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);

        Display display = getWindowManager().getDefaultDisplay(); // получаем размер  экрана
        Point size = new Point();
        display.getSize(size);
        int padding = size.x / 2 - 100; // вычисляем отступ (рамер нулвого пустого элемента) для расположения первого элемента посреди экрана.
        // 100 - половина ширины изображения
        DateAdapter myAdapter = new DateAdapter(dates, this, padding);
        dateRecyclerView.setAdapter(myAdapter);
        dateRecyclerView.scrollToPosition(dates.size() - 2);


        // ----- Для получения центрального видимого элемента--------
        MiddleItemFinder.MiddleItemCallback callbackM =
                new MiddleItemFinder.MiddleItemCallback() {
                    @Override
                    public void scrollFinished(int middleElement) { // вычисляем позицию центрального элемента.
                        if (dates.get(middleElement) != null)
                            currentData = dates.get(middleElement);
                        preDate = middleElement > 0 && dates.get(middleElement - 1) != null ? dates.get(middleElement - 1) : "";
                        switch (bottomNavigationView.getSelectedItemId()) {
                            case R.id.bn_item_graph:
                                updateFragment(new FragmentGeoStatisticGraphics(GeoStatisticActivity.this, currentData, preDate));
                                break;
                            case R.id.bn_item_map:
                                updateFragment(new FragmentGeoStatisticMap(GeoStatisticActivity.this, currentData));
                                break;
                        }
                    }
                };
        dateRecyclerView.addOnScrollListener(
                new MiddleItemFinder(this, layoutManager,
                        callbackM, RecyclerView.SCROLL_STATE_IDLE));

        // ----------------------------

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
                        updateFragment(new FragmentGeoStatisticGraphics(GeoStatisticActivity.this, currentData, preDate));
                        break;
                    case R.id.bn_item_map:
                        updateFragment(new FragmentGeoStatisticMap(GeoStatisticActivity.this, currentData));
                        break;
//                    case R.id.bn_item_table:
//                        updateFragment(new FragmentGeoStatisticTable());
//                        break;
//                    case R.id.bn_item_list:
//                        updateFragment(new FragmentGeoStatisticEvents());
//                        break;
                }
                return true;
            }
        });


        updateFragment(new FragmentGeoStatisticGraphics(this, currentData, preDate));

    }


    private void updateFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.statistic_fragment, fragment).commit();
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
        String fileName = currentData != null ? currentData : "_";
        float x = 20;

        String fragmentName = getSupportFragmentManager().findFragmentById(R.id.statistic_fragment).getClass().getName();
        switch (fragmentName) {
            case "com.example.smarttrainer.FragmentGeoStatisticGraphics":
                FragmentGeoStatisticGraphics fragment = (FragmentGeoStatisticGraphics) getSupportFragmentManager().findFragmentById(R.id.statistic_fragment);

                bitmap0 = Bitmap.createBitmap(fragment.view.getWidth(), 450,
                        Bitmap.Config.ARGB_8888);

                bitmap1 = getBitmapFromView(fragment.view.findViewById(R.id.line_chart_geo_speed));
                bitmap2 = getBitmapFromView(fragment.view.findViewById(R.id.line_chart_geo_time));
                bitmap0 = overlay(bitmap0, bitmap1);
                bitmap0 = overlay(bitmap0, bitmap2);

                fileName = "Результаты тренировки " + fileName + " (графики)";
                break;

            case "com.example.smarttrainer.FragmentGeoStatisticMap":
                FragmentGeoStatisticMap fragment2 = (FragmentGeoStatisticMap) getSupportFragmentManager().findFragmentById(R.id.statistic_fragment);

                bitmap0 = Bitmap.createBitmap(fragment2.view.getWidth(), 450,
                        Bitmap.Config.ARGB_8888);

                MapView mapView = fragment2.view.findViewById(R.id.mapView);
                bitmap1 = mapView.getScreenshot();
                //bitmap1 = getBitmapFromView(fragment2.view);

                bitmap0 = overlay(bitmap0, bitmap1);

                fileName = "Результаты тренировки " + fileName + " (карта)";
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
