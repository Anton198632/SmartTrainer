package com.builderlinebr.smarttrainer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.builderlinebr.smarttrainer.adapters.WorkoutAdapter;
import com.builderlinebr.smarttrainer.customviews.ItemTouchHelperCallback2;
import com.builderlinebr.smarttrainer.database.Database;
import com.builderlinebr.smarttrainer.database.RealPathUtil;
import com.builderlinebr.smarttrainer.database.Workouts;
import com.builderlinebr.smarttrainer.dialogs.WorkoutAddDialog;
import com.builderlinebr.smarttrainer.viewmodel.WorkoutsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.*;

import static android.view.Window.FEATURE_NO_TITLE;

public class MyWorkoutsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Workouts> workoutsList;

    public Toolbar toolbar;

    FloatingActionButton fab;

    WorkoutsViewModel workoutsViewModel;

    public int selectedPosition;

    public static int isMy;


    public static final int PICK_DB = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workouts);

        verifyStoragePermissions(this);

        selectedPosition = -1;

        if (getIntent()!=null)
        isMy = getIntent().getIntExtra("isMy", 0);

        // ------ Устанавливае ActionBar ------------------------
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView textView = toolbar.findViewById(R.id.toolbar_title_text_view);
        textView.setText(getString(R.string.workout_list_text));


        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) { // Если actionBar не равен null устанавливаем навигацю назад (стрелка слева вверху)
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // ----------------------------------------------------

        // ------ Устанавливаем FloatingActionButton (add) ----------
        fab = findViewById(R.id.btn_add_my_workout);
        if (isMy == 0) fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogFragment dialogFragment = new WorkoutAddDialog(MyWorkoutsActivity.this);
                FragmentManager fragmentManager = getSupportFragmentManager();
                dialogFragment.show(fragmentManager, "addWorkoutDialog");

                fragmentManager.executePendingTransactions();
                dialogFragment.getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        WorkoutAddDialog workoutAddDialog = (WorkoutAddDialog) dialogFragment;
                        if (workoutAddDialog.result) {
                            String workoutName = workoutAddDialog.editTextWorkoutName.getText().toString();
                            String workoutDescription = workoutAddDialog.editTextWorkoutDescription.getText().toString();

                            Workouts newWorkout = new Workouts();
                            newWorkout.setIsMy(isMy);
                            newWorkout.setForGirl(workoutAddDialog.forGirlChecBox.isChecked()?1:0);
                            newWorkout.setWorkoutName(workoutName);
                            newWorkout.setWorkoutDescription(workoutDescription);
                            newWorkout.setWorkoutImage(workoutAddDialog.photoFileName);
                            if (workoutAddDialog.workout != null) newWorkout.setId(workoutAddDialog.workout.getId());

                            new ViewModelProvider(MyWorkoutsActivity.this).get(WorkoutsViewModel.class).addNewWorkout(newWorkout);

                            initWorkouts();


                        }
                    }
                });
            }
        });
        // ------------------------------------------------------

        // ----------- Получаем список из БД и заполняем recyclerView --------------------
        recyclerView = findViewById(R.id.my_workouts_recyclerview);
        initWorkouts();
        // ------------------------------------------------------


    }


    // Для андрой sdk23+ необходимо дополнительно спросить разрешение к записи чтению файлов
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public void initWorkouts() {

        workoutsViewModel = new ViewModelProvider(this).get(WorkoutsViewModel.class);
        workoutsList = new ArrayList<>();

        toolbar.setTitle("");
        TextView textView = toolbar.findViewById(R.id.toolbar_title_text_view);
        switch (isMy) {
            case 0:
                workoutsList = workoutsViewModel.getOtherWorkouts();
                textView.setText(getString(R.string.workout_list_text));
                break;
            case 1:
                workoutsList = workoutsViewModel.getIsMyWorkouts();
                textView.setText(getString(R.string.workout_my_text));
                break;
            case 2:
                workoutsList = workoutsViewModel.getGeoWorkouts();
                textView.setText(getString(R.string.geo_location_text));
                break;
        }


        Collections.sort(workoutsList, new Comparator<Workouts>() {
            @Override
            public int compare(Workouts o1, Workouts o2) {
                return o1.getWorkoutName().compareTo(o2.getWorkoutName());
            }
        });




//        } else {
//            workoutsList = workoutsViewModel.getOtherWorkouts();
//            fab.setVisibility(View.GONE);
//        }

        // ------ Заполняем RecyclerView ------------------------
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        WorkoutAdapter myAdapter = new WorkoutAdapter(this, workoutsList);
        recyclerView.setAdapter(myAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback2(myAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        // -----------------------------------

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isMy == 1)
            getMenuInflater().inflate(R.menu.workout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, NavigationActivity.class);
                startActivity(intent);
                break;
            case R.id.workout_save: // Сохранение своих тренировок в файл
                try {

                    Database.INSTANCE.close(); // Обязательно необходимо закрыть БД, чтобы все изменения применились
                    Database.INSTANCE = null;

                    File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //  getExternalFilesDir(""); // путь к новой БД
                    String currentDBPath = getDatabasePath("database.db").getAbsolutePath(); // Получаем путь к БД приложения
                    String backupDBPath = "database_backup.db"; // название новой БД
                    File currentDB = new File(currentDBPath); // файл БД приложения
                    File backupDB = new File(sd, backupDBPath); // фаил новой БД

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }


                    Database.getDatabase(this);

                    Toast.makeText(this, "База данных сохранена по пути:\n" + backupDB.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.workout_load: // Загрузка своих тренировок из файла

                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("*/*");
                startActivityForResult(getIntent, PICK_DB);


                break;
            case R.id.workout_edit: // В случае редактирования названия и описания тренировки
                workoutEditDialog();
                break;
            case R.id.workout_delete: // В случае удаления тренировки из списка
                if (selectedPosition != -1) {
                    confirmDeleteWorkout(selectedPosition);
                }
                break;
            case R.id.workout_cancel: // В случае отмены редактирования
                selectedPosition = -1;
                toolbar.getMenu().clear();
                getMenuInflater().inflate(R.menu.workout_menu, toolbar.getMenu());
                recyclerView.getAdapter().notifyDataSetChanged();
                break;
        }

        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DB) {
            if (data != null) {
                Uri uri = data.getData();
                String newDbPath = uri.getPath();
                newDbPath = RealPathUtil.getRealPath(this, uri);
                Database.replaceDatabase(this, newDbPath);

                initWorkouts();
            }
        }
    }




    // диалог редактирования тренировки
    public void workoutEditDialog() {
        final DialogFragment dialogFragment = new WorkoutAddDialog(MyWorkoutsActivity.this, workoutsList.get(selectedPosition));
        FragmentManager fragmentManager = (MyWorkoutsActivity.this).getSupportFragmentManager();
        dialogFragment.show(fragmentManager, "addWorkoutDialog");

        fragmentManager.executePendingTransactions();
        dialogFragment.getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                WorkoutAddDialog workoutAddDialog = (WorkoutAddDialog) dialogFragment;
                if (workoutAddDialog.result) {
                    String workoutName = workoutAddDialog.editTextWorkoutName.getText().toString();
                    String workoutDescription = workoutAddDialog.editTextWorkoutDescription.getText().toString();

                    Workouts newWorkout = new Workouts();
                    newWorkout.setWorkoutName(workoutName);
                    newWorkout.setWorkoutDescription(workoutDescription);
                    newWorkout.setWorkoutImage(workoutAddDialog.photoFileName);
                    newWorkout.setIsMy(isMy);
                    newWorkout.setForGirl(workoutAddDialog.forGirlChecBox.isChecked()?1:0);

                    if (workoutAddDialog.workout != null) newWorkout.setId(workoutAddDialog.workout.getId());

                    new ViewModelProvider(MyWorkoutsActivity.this).get(WorkoutsViewModel.class).addNewWorkout(newWorkout);

                    (MyWorkoutsActivity.this).initWorkouts();
                }
            }
        });
    }

    // подтверждение удаления тренировки
    public void confirmDeleteWorkout(final int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.DialogWorkout)
                .setMessage(getString(R.string.confirm_workout_delete_text) + "«" + workoutsList.get(position).getWorkoutName() + "»")
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }).setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new ViewModelProvider(MyWorkoutsActivity.this).get(WorkoutsViewModel.class).delete(workoutsList.get(position));
                        workoutsList.remove(position);
                        (MyWorkoutsActivity.this).initWorkouts();
                    }
                }).create();
        alertDialog.requestWindowFeature(FEATURE_NO_TITLE);
        alertDialog.show();
    }


}
