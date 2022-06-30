package com.builderlinebr.smarttrainer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.builderlinebr.smarttrainer.calculation.CalcMap;
import com.builderlinebr.smarttrainer.calculation.CalcTime;
import com.builderlinebr.smarttrainer.database.Workouts;
import com.builderlinebr.smarttrainer.model.exercises.GeolocationEvents;
import com.builderlinebr.smarttrainer.model.location.CoordinatesObject;
import com.builderlinebr.smarttrainer.viewmodel.GeolocationEventsViewModel;
import com.builderlinebr.smarttrainer.viewmodel.WorkoutsViewModel;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;
import com.yandex.mapkit.map.*;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.ui_view.ViewProvider;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.File;
import java.util.*;

public class MapActivity extends AppCompatActivity {

    private MapView mapview;
    private AdView mAdView;

    Toolbar toolbar;

    LocationManager locationManager;
    private static final String[] LOCATION_PERMS = { // Для запроса на доступ к геоданным для SDK >= 23
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int LOCATION_REQUEST = 123;

    PlacemarkMapObject myLocationPlacemark;
    double[] preCoordinates;


    FloatingActionButton fabRun;
    FloatingActionButton fabStatistic;

    Thread thread;

    TextView timerView, distanceView, averageView, currentSpeedView;

    public long intervalPre;
    public long interval;
    public long interval0;

    public int commonDistance;

    public static float zoom;

    List<PolylineMapObject> paths;

    List<double[]> coordinateList;

    LinearLayout mapTableLayout;

    boolean isMyLocation;

    public static PowerManager.WakeLock mWakeLock;

    TextView coordinatesTextView;

    int workoutId;

    List<GeolocationEvents> geoEvents;

    boolean myLocationFind;


    //  @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        myLocationFind = false;

        MapKitFactory.setApiKey("11f74694-ed89-4d86-8338-b59d918a44c0");
        MapKitFactory.initialize(this);

        if (getIntent() != null) {
            workoutId = getIntent().getIntExtra("workoutId", -1);
        }

        // Получаем события предедущих тренировок (есть ли хотя бы одно?)
        GeolocationEventsViewModel geolocationEventsViewModel = new ViewModelProvider(this).get(GeolocationEventsViewModel.class);
        geoEvents = geolocationEventsViewModel.getEventsByDistanceId(workoutId, 1);

        setContentView(R.layout.activity_map);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Показ рекламы
                        mAdView = findViewById(R.id.adView);
                        AdRequest adRequest = new AdRequest.Builder().build();
                        if (getResources().getBoolean(R.bool.ad_use)) {
                            mAdView.loadAd(adRequest);
                        } else mAdView.setVisibility(View.GONE);
                    }
                });

            }
        }).start();


        mapview = (MapView) findViewById(R.id.mapView);
        mapview.getMap().move(
                new CameraPosition(new Point(55.751574, 37.573856), zoom == 0f ? 15f : zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);


        //region ------ ToolBar --------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar_workout_exercise_id);
        toolbar.setTitle("");
        TextView textView = toolbar.findViewById(R.id.workout_exercise_header_toolbar);
        String title = getIntent().getStringExtra("geoName");
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
        }
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //endregion ---------------------------------------------------------

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        requestPermissions(LOCATION_PERMS, LOCATION_REQUEST); // Запрашиваем разрешения для геолокации (>= SDK-23)

        preCoordinates = new double[]{55.751574, 37.573856};

        //region -------------------- FloatingMenu -----------------------
        fabRun = findViewById(R.id.fab_run);
        fabRun.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InvalidWakeLockTag")
            @Override
            public void onClick(View v) {
                if (thread == null) {
                    if (checkEnabledLocation()) {
                        if (myLocationFind) {
                            interval = 0;
                            commonDistance = 0;
                            mapTableLayout.setVisibility(View.VISIBLE);
                            distanceView.setText(Integer.toString(commonDistance));
                            averageView.setText(getAverageSpeed());

                            fabStatistic.setVisibility(View.GONE);
                            if (getResources().getBoolean(R.bool.ad_use))
                                mAdView.pause();
                            mAdView.setVisibility(View.GONE);

                            // Делаем пустой слушатель для провайдера network
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED  // Проверяем разрешения на доступ к геолокации
                                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                locationManager.requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER,
                                        1000 * 10,
                                        10,
                                        new LocationListener() {
                                            @Override
                                            public void onLocationChanged(Location location) {

                                            }

                                            @Override
                                            public void onStatusChanged(String provider, int status, Bundle extras) {

                                            }

                                            @Override
                                            public void onProviderEnabled(String provider) {

                                            }

                                            @Override
                                            public void onProviderDisabled(String provider) {

                                            }
                                        }
                                );
                            }


                            thread = new Thread(runnable);
                            thread.start();
                            fabRun.setIconDrawable(getDrawable(R.drawable.ic_stop));

                            // Включение запрета перехода в спящий режим
                            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
                            mWakeLock.acquire();
                        } else {
                            Toast.makeText(MapActivity.this, R.string.my_location_dont_find, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MapActivity.this, R.string.enable_gps, Toast.LENGTH_LONG).show();
                    }
                } else {
                    showConfirmDialog(false);
                }
            }
        });

        fabStatistic = findViewById(R.id.fab_statistic);
        if (geoEvents.size() > 0) fabStatistic.setVisibility(View.VISIBLE); // если есть данные о прошлых тренировках
        else fabStatistic.setVisibility(View.GONE);
        fabStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, GeoStatisticActivity.class);
                intent.putExtra("workoutId", workoutId);
                startActivity(intent);
            }
        });
        //endregion ----------------------------------------------------------------


        timerView = findViewById(R.id.timer_text_id);
        distanceView = findViewById(R.id.distance_text_id);
        averageView = findViewById(R.id.average_speed_text_id);
        currentSpeedView = findViewById(R.id.current_speed_text_id);
        mapTableLayout = findViewById(R.id.map_table_layout);
        mapTableLayout.setVisibility(View.GONE);

        paths = new ArrayList<>();
        coordinateList = new ArrayList<>();

        coordinatesTextView = findViewById(R.id.coordinates_text_view);

        isMyLocation = true;


    }


    //region "Побежали"

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            long milisec = Calendar.getInstance().getTimeInMillis();

            //  interval = 0;
            intervalPre = interval;
            interval0 = interval;
            milisec = Calendar.getInstance().getTimeInMillis();
            while (true) {
                try {

                    if (interval == 0) milisec = Calendar.getInstance().getTimeInMillis();

                    interval = intervalPre + (Calendar.getInstance().getTimeInMillis() - milisec);
                    final String timeString = new CalcTime().ConvertLongToTimeString(interval);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerView.setText(timeString);
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
    // endregion

    //region Слушатель геолокации

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {  // Метод будет вызываться если мы пройдем минимальное растояние,
            // указанное в настройках в методе
            // locationManager.requestLocationUpdates()  (10 метров)


            double[] coordinates = getLocation(location);

            if (!myLocationFind && location.getProvider().equals("gps")) {
                preCoordinates = coordinates;
                myLocationFind = true;
            }

            coordinatesTextView.setText(getString(R.string.latitude) + " " + String.format("%.6f", coordinates[0]) + "\n" +
                    getString(R.string.longitude) + " " + String.format("%.6f", coordinates[1]) + "\n" +
                    getString(R.string.geo_provider) + " " + location.getProvider()); // "Provider: "

            int distance = new CalcMap().getDistanceInM(
                    preCoordinates[0], preCoordinates[1],
                    coordinates[0], coordinates[1]
            );

            if (thread != null && location.getProvider().equals("gps") && distance < 50) { //distance < 15 &&

                // Общая дистанция
                commonDistance += distance;
                distanceView.setText(Integer.toString(commonDistance));

                // Измеряем пройденное расстояние и время его прохождения
                long time = interval - interval0;
                String currentSpeed = getCurrentSpeed(preCoordinates[0], preCoordinates[1],
                        coordinates[0], coordinates[1], time);
                currentSpeedView.setText(currentSpeed);
                interval0 = interval;
                double[] coors = new double[]{coordinates[0], coordinates[1], (double) Float.parseFloat(currentSpeed)};

                coordinateList.add(coors);


                drawPath(preCoordinates[0], preCoordinates[1],
                        coordinates[0], coordinates[1]);

                averageView.setText(getAverageSpeed());

                preCoordinates = coordinates;

            }

            //  preCoordinates = coordinates;


        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        //   @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onProviderEnabled(String provider) { // метод при включении геолокации
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation(locationManager.getLastKnownLocation(provider));
            } else {

            }
            toolbar.getMenu().getItem(1).setIcon(getDrawable(R.drawable.ic_location_enabled));
        }

        @Override
        public void onProviderDisabled(String provider) {
            toolbar.getMenu().getItem(1).setIcon(getDrawable(R.drawable.ic_location_disabled));
        }
    };

    //endregion
    //region Определение местоположения
    private double[] getLocation(Location location) {
        double[] result = new double[]{55.751574, 37.573856};
        if (location == null) return result;
        result[0] = location.getLatitude();
        result[1] = location.getLongitude();

        zoom = mapview.getMap().getCameraPosition().getZoom(); // определяем текущий zoom

        if (isMyLocation && thread == null)
            mapview.getMap().move(
                    new CameraPosition(new Point(result[0], result[1]), zoom, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 2),
                    null);


        if (myLocationPlacemark == null) {
            View view = new View(this);
            view.setBackground(getDrawable(R.drawable.ic_my_location));
            myLocationPlacemark = mapview.getMap().getMapObjects().addPlacemark(new Point(result[0], result[1]), new ViewProvider(view));
        } else {
            myLocationPlacemark.setGeometry(new Point(result[0], result[1]));
        }

        return result;
    }

    //endregion
    //region Прорисовка пути
    private void drawPath(double lat1, double long1, double lat2, double long2) {
        List<Point> points = new ArrayList<>();
        points.add(new Point(lat1, long1));
        points.add(new Point(lat2, long2));
        Polyline polyline = new Polyline(points);

        double azimuth = new CalcMap().calcAzimuth(lat1, long1, lat2, long2);
        float a = (float) azimuth;

        if (isMyLocation)
            mapview.getMap().move(
                    new CameraPosition(new Point(lat2, long2), zoom, (float) azimuth, 0.0f),
                    new Animation(Animation.Type.LINEAR, 0.5f),
                    null);

        paths.add(mapview.getMap().getMapObjects().addPolyline(polyline)); // рисуем линию и добавляем к путям для удаления после прерывания потока

    }

    //endregion
    //region Вычисление среднего и текущего значения скорости
    private String getAverageSpeed() {
        float speed = 1000f * (float) commonDistance / (float) (interval > 0 ? interval : 1);
        return String.format("%.2f", speed).replace(",", ".");
    }

    private String getCurrentSpeed(double lat1, double long1, double lat2, double long2, long time) {
        int distance = new CalcMap().getDistanceInM(lat1, long1, lat2, long2);
        float speed = time > 0 ? 1000f * (float) distance / (float) time : 0;
        return String.format("%.2f", speed).replace(",", ".");
    }

    //endregion
    //region Диалог подьвереждения/отмена конца тренировки (пробежки)

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
                    //     @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        thread.interrupt();
                        fabRun.setIconDrawable(getDrawable(R.drawable.ic_run));
                        thread = null;

                        mapTableLayout.setVisibility(View.GONE);

                        mAdView.setVisibility(View.VISIBLE);
                        if (getResources().getBoolean(R.bool.ad_use))
                            mAdView.loadAd(new AdRequest.Builder().build());
                        else mAdView.setVisibility(View.GONE);

                        for (PolylineMapObject path : paths) {
                            mapview.getMap().getMapObjects().remove(path);
                        }
                        paths = new ArrayList<>();
                        coordinateList = new ArrayList<>();

                        if (geoEvents.size() > 0)
                            fabStatistic.setVisibility(View.VISIBLE); // если есть данные о прошлых тренировках
                        else fabStatistic.setVisibility(View.GONE);

                        if (mWakeLock != null)
                            mWakeLock.release(); // Отключение запрета перехода в спящий режим
                        mWakeLock = null;

                        // Подключаем слушателя для провайдера network
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED  // Проверяем разрешения на доступ к геолокации
                                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    1000 * 10,
                                    10,
                                    locationListener
                            );
                        }

                        if (finish) {
                            finish();
                        }


                    }
                })
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    //    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        thread.interrupt();
                        fabRun.setIconDrawable(getDrawable(R.drawable.ic_run));
                        thread = null;

                        mapTableLayout.setVisibility(View.GONE);

                        mAdView.setVisibility(View.VISIBLE);
                        if (getResources().getBoolean(R.bool.ad_use))
                            mAdView.loadAd(new AdRequest.Builder().build());
                        else mAdView.setVisibility(View.GONE);

                        for (PolylineMapObject path : paths) {
                            mapview.getMap().getMapObjects().remove(path);
                        }

                        // Запись результатов в БД
                        if (coordinateList.size() > 0) {
                            GeolocationEvents geolocationEvents = new GeolocationEvents(
                                    (int) new CalcTime().convertDateToLong(new Date()),
                                    commonDistance,
                                    (int) interval,
                                    coordinateList,
                                    workoutId
                            );
                            GeolocationEventsViewModel viewModel = new ViewModelProvider(MapActivity.this).get(GeolocationEventsViewModel.class);
                            viewModel.addEvent(geolocationEvents);
                            fabStatistic.setVisibility(View.VISIBLE);
                        }

                        paths = new ArrayList<>();
                        coordinateList = new ArrayList<>();

                        fabStatistic.setVisibility(View.VISIBLE);

                        if (mWakeLock != null)
                            mWakeLock.release(); // Отключение запрета перехода в спящий режим
                        mWakeLock = null;

                        // Подключаем слушателя для провайдера network
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED  // Проверяем разрешения на доступ к геолокации
                                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    1000 * 10,
                                    10,
                                    locationListener
                            );
                        }

                        if (finish) {
                            finish();
                        }

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();


    }
    //endregion

    //region Ответ на подтверждение разрешений

    //  @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { // ответ на подтверждение разрешений
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST) { // если ответ на разрешение доступа к геолокации
            if (grantResults[0] == 0) {

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED  // Проверяем разрешения на доступ к геолокации
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000 * 2, // Минимальное время ожидание, в мс
                            3,   // Минимальное расстояние для обновления
                            locationListener

                    );

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000 * 10,
                            10,
                            locationListener
                    );

                } else {
                    finish();
                }
            }
        }
    }

    //endregion
    //region Проверка включения местоопределения
    private boolean checkEnabledLocation() {
        boolean result = false;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            result = true;
        return result;
    }
    // endregion

    //region Сохранение переменных (при пересоздании активити)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (thread != null) {
            Gson gson = new Gson();
            CoordinatesObject coordinatesObject = new CoordinatesObject(preCoordinates, coordinateList);
            String json = gson.toJson(coordinatesObject);
            outState.putString("coordinates", json);
            outState.putLong("interval", interval);
            outState.putInt("commonDistance", commonDistance);
        }
        outState.putBoolean("isMyLocation", isMyLocation);
    }


    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            String json = savedInstanceState.getString("coordinates");
            if (json != null && !json.isEmpty()) {
                Gson gson = new Gson();
                CoordinatesObject coordinatesObject = gson.fromJson(json, CoordinatesObject.class);
                preCoordinates = coordinatesObject.getPreCoordinates();
                coordinateList = coordinatesObject.getCoordinateList();
            }
            interval = savedInstanceState.getLong("interval", 0);
            commonDistance = savedInstanceState.getInt("commonDistance", 0);
            if (interval != 0) {

                for (int i = 1; i < coordinateList.size(); i++) {
                    double[] c1 = coordinateList.get(i - 1);
                    double[] c2 = coordinateList.get(i);
                    drawPath(c1[0], c1[1], c2[0], c2[1]);
                }

                mapTableLayout.setVisibility(View.VISIBLE);
                distanceView.setText(Integer.toString(commonDistance));
                averageView.setText(getAverageSpeed());

                thread = new Thread(runnable);
                thread.start();
                fabRun.setIconDrawable(getDrawable(R.drawable.ic_stop));

                fabStatistic.setVisibility(View.GONE);

                // Включение запрета перехода в спящий режим
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
                mWakeLock.acquire();
            }
            isMyLocation = savedInstanceState.getBoolean("isMyLocation", false);
//            if (isMyLocation){
//               toolbar.getMenu().getItem(1).setIcon(getDrawable(R.drawable.ic_my_location_enable));
//            }
        }
    }

    // endregion

    //region Меню

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        if (checkEnabledLocation()) {
            if (!isMyLocation)
                menu.getItem(1).setIcon(getDrawable(R.drawable.ic_location_enabled));
            else menu.getItem(1).setIcon(getDrawable(R.drawable.ic_my_location_enable));
        } else menu.getItem(1).setIcon(getDrawable(R.drawable.ic_location_disabled));
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (thread != null) {
                    showConfirmDialog(true);
                } else {
                    finish();
                }
                break;
            case R.id.location_settings_item:
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // Вызываем настройки включения/выключения местоположения
                break;
            case R.id.location_item:
                if (checkEnabledLocation()) {
                    if (!isMyLocation) {
                        isMyLocation = true;
                        item.setIcon(getDrawable(R.drawable.ic_my_location_enable));

                    } else {
                        isMyLocation = false;
                        item.setIcon(getDrawable(R.drawable.ic_location_enabled));
                    }
                } else {
                    Toast.makeText(MapActivity.this, R.string.enable_gps, Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //endregion

    @Override
    public void onBackPressed() {
        if (thread != null) {
            showConfirmDialog(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapview.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapview.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    protected void onDestroy() {
        //if (mWakeLock != null)
        //    mWakeLock.release(); // Отключение запрета перехода в спящий режим
        super.onDestroy();
    }

}
