package com.example.advanced_system_programing;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Video.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract VideoDao videoDao();

}


