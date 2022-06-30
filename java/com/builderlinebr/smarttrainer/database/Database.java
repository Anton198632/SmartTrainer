package com.builderlinebr.smarttrainer.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.builderlinebr.smarttrainer.R;

import java.io.*;
import java.nio.channels.FileChannel;


@androidx.room.Database(entities = {
        Exercises.class,
        ImagesMale.class,
        ImagesFemale.class,
        ImagesM.class,
        Food.class,
        Workouts.class,
        WorkoutExercises.class,
        Events.class,
        UserTable.class,
        GeolocationEventsTable.class
}, version = 2, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract ExercisesDao exercisesDao();

    public abstract ImagesMaleDao imagesMaleDao();

    public abstract ImagesFemaleDao imagesFemaleDao();

    public abstract ImagesMDao imagesMDao();

    public abstract FoodDao foodDao();

    public abstract WorkoutsDao workoutsDao();

    public abstract WorkoutExercisesDao workoutExercisesDao();

    public abstract EventsDao eventsDao();

    public abstract UserTableDao userTableDao();

    public abstract GeolocationEventsDao geolocationEventsDao();

    public static Database INSTANCE;
    public static String dbPath;

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //database.execSQL("ALTER TABLE Workouts ADD COLUMN forGirl INTEGER DEFAULT 0 NOT NULL");
        }

    };


    public static void createDatabase(Context context) {

       // context.deleteDatabase("database.db");


        // копируем БД
        String[] files = context.getExternalFilesDirs("")[0].list();
        boolean dbFind = false;
        for (String file : files) {
            if (file.equals("database.db")) {
                dbFind = true;
                break;
            }
        }
        if (!dbFind) {
            InputStream inputStream = context.getResources().openRawResource(R.raw.mydatabase);
            dbPath = context.getExternalFilesDir("") + "/database.db";
            try {
                OutputStream outputStream = new FileOutputStream(dbPath);
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void replaceDatabase(Context context, String newDbPath) {

        INSTANCE.close(); // Закрываем БД
        INSTANCE = null;
        // удаляем старую БД
        context.deleteDatabase("database.db"); // удаляем из приложения
        new File(context.getExternalFilesDir("") + "/database.db").delete(); // удаляем шаблонную БД
        // комируем новую БД на место шаблона
        copy(new File(newDbPath), new File(context.getExternalFilesDir("") + "/database.db"));

        getDatabase(context); // получаем новую БД из шаблона
    }


    private static void copy(File inFile, File outFile) {

        try {
            FileInputStream inputStream = new FileInputStream(inFile);
            FileOutputStream outputStream = new FileOutputStream(outFile);
            FileChannel inFileChannel = inputStream.getChannel();
            FileChannel outFileChannel = outputStream.getChannel();
            inFileChannel.transferTo(0, inFileChannel.size(), outFileChannel);
            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Database getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    dbPath = context.getExternalFilesDir("") + "/database.db";

                    INSTANCE = Room.databaseBuilder(context, Database.class, "database.db") // "database.db"
                            .createFromFile(new File(dbPath))
                            .addMigrations(MIGRATION_1_2)
                            .allowMainThreadQueries()
                            .build();
                    int i = 0;
                }
            }
        }
        return INSTANCE;
    }
}
