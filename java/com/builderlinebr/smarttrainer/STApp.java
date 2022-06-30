package com.builderlinebr.smarttrainer;

import android.app.Application;
import com.builderlinebr.smarttrainer.database.Database;

public class STApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Database.createDatabase(this);
    }
}
