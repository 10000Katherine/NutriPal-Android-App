package com.nutripal.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase; // 1. Import this

import com.nutripal.R; // 2. Import R
import com.nutripal.database.dao.AchievementDao;
import com.nutripal.database.dao.FoodLogDao;
import com.nutripal.database.dao.UserDao;
import com.nutripal.database.dao.WaterLogDao;
import com.nutripal.models.Achievement;
import com.nutripal.models.FoodLog;
import com.nutripal.models.User;
import com.nutripal.models.UserAchievement;
import com.nutripal.models.WaterLog;

import java.util.concurrent.ExecutorService; // 3. Import these
import java.util.concurrent.Executors;

// Increment the version number again for the schema change in the next step
@Database(entities = {User.class, FoodLog.class, WaterLog.class, Achievement.class, UserAchievement.class}, version = 8, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract FoodLogDao foodLogDao();
    public abstract WaterLogDao waterLogDao();
    public abstract AchievementDao achievementDao();

    private static volatile AppDatabase INSTANCE;
    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "nutripal_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback) // 4. Add the callback here
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // 5. Add this entire callback block
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                AchievementDao dao = INSTANCE.achievementDao();
                // Pre-populate the achievements table
                dao.insertAchievement(new Achievement("FIRST_LOG", "First Log", "Log your first food item.", R.drawable.ic_launcher_foreground));
                dao.insertAchievement(new Achievement("7_DAY_STREAK", "7-Day Streak", "Log food for 7 days in a row.", R.drawable.ic_launcher_foreground));
                dao.insertAchievement(new Achievement("HYDRATION_HERO", "Hydration Hero", "Meet your daily water goal.", R.drawable.ic_launcher_foreground));
            });
        }
    };
}