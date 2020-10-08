package com.example.recordapplicationcoderbyte.room;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {RecordEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "recordsDb";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "getInstance: Creating a new database instance");
                sInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        AppDatabase.DATABASE_NAME
                ).build();
            }
        }
        Log.d(TAG, "getInstance: Getting the database instance, no need to recreate one");
        return sInstance;
    }


    public abstract RecordDao recordDao();
}