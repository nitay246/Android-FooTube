package com.example.advanced_system_programing;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
//class to represent the application

public class MyApplication extends Application {
    public static Context context;
    private VideosViewModel videosViewModel;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApplication", "Application started");
        context=getApplicationContext();
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean wasProperlyClosed = prefs.getBoolean("was_properly_closed", true);

        if (!wasProperlyClosed) {
            clearRoomDatabase();
        }

        // Reset the flag
        prefs.edit().putBoolean("was_properly_closed", false).apply();
    }
    @Override
    public void onTerminate() {
        super.onTerminate();

        // Clear Room database before the application terminates
        clearRoomDatabase();
    }

    private void clearRoomDatabase() {
        Log.d("MyApplication", "Clearing Room database");
        VideosViewModel videosViewModel = new ViewModelProvider.AndroidViewModelFactory(this)
                .create(VideosViewModel.class);
        videosViewModel.clearAllVideos();

    }
}
