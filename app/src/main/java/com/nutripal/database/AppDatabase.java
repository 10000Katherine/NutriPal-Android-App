package com.nutripal.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nutripal.database.dao.FoodLogDao;
import com.nutripal.database.dao.UserDao;
import com.nutripal.database.dao.WaterLogDao;
import com.nutripal.models.FoodLog;
import com.nutripal.models.User;
import com.nutripal.models.WaterLog;

@Database(entities = {User.class, FoodLog.class, WaterLog.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract FoodLogDao foodLogDao();
    public abstract WaterLogDao waterLogDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "nutripal_database")

                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}