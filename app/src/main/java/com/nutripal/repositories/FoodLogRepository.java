package com.nutripal.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.nutripal.database.AppDatabase;
import com.nutripal.database.dao.FoodLogDao;
import com.nutripal.database.dao.UserDao;
import com.nutripal.database.dao.WaterLogDao;
import com.nutripal.models.FoodLog;
import com.nutripal.models.User;
import com.nutripal.models.WaterLog;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodLogRepository {

    private final FoodLogDao foodLogDao;
    private final UserDao userDao;
    private final WaterLogDao waterLogDao;
    private final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public FoodLogRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        foodLogDao = db.foodLogDao();
        userDao = db.userDao();
        waterLogDao = db.waterLogDao();
    }

    // User Methods
    public LiveData<User> getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }
    public void updateUser(User user) {
        databaseWriteExecutor.execute(() -> userDao.update(user));
    }

    // FoodLog Methods
    public LiveData<List<FoodLog>> getLogsForDate(long startOfDay, long endOfDay) {
        return foodLogDao.getLogsForDate(startOfDay, endOfDay);
    }
    public LiveData<List<FoodLog>> getAllFoodLogs() {
        return foodLogDao.getAllFoodLogs();
    }
    public void insert(FoodLog foodLog) {
        databaseWriteExecutor.execute(() -> foodLogDao.insert(foodLog));
    }
    public void delete(FoodLog foodLog) {
        databaseWriteExecutor.execute(() -> foodLogDao.delete(foodLog));
    }
    public void update(FoodLog foodLog) {
        databaseWriteExecutor.execute(() -> foodLogDao.update(foodLog));
    }

    // WaterLog Methods
    public LiveData<Integer> getTotalWaterForDate(long startOfDay, long endOfDay) {
        return waterLogDao.getTotalWaterForDate(startOfDay, endOfDay);
    }
    public void insertWaterLog(WaterLog waterLog) {
        databaseWriteExecutor.execute(() -> waterLogDao.insert(waterLog));
    }

    public LiveData<List<FoodLog>> getLogsSince(long startDate) {
        return foodLogDao.getLogsSince(startDate);
    }

}