package com.nutripal.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.nutripal.database.AppDatabase;
import com.nutripal.database.dao.AchievementDao;
import com.nutripal.database.dao.FoodLogDao;
import com.nutripal.database.dao.UserDao;
import com.nutripal.database.dao.WaterLogDao;
import com.nutripal.models.Achievement;
import com.nutripal.models.FoodLog;
import com.nutripal.models.User;
import com.nutripal.models.WaterLog;
import java.util.List;

public class FoodLogRepository {

    private final FoodLogDao foodLogDao;
    private final UserDao userDao;
    private final WaterLogDao waterLogDao;
    private final AchievementDao achievementDao;

    public FoodLogRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        foodLogDao = db.foodLogDao();
        userDao = db.userDao();
        waterLogDao = db.waterLogDao();
        achievementDao = db.achievementDao();
    }

    // User Methods
    public LiveData<User> getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }
    // Make this a direct call. The ViewModel will handle the background thread.
    public void updateUser(User user) {
        userDao.update(user);
    }

    // FoodLog Methods
    public void insert(FoodLog foodLog) {
        foodLogDao.insert(foodLog);
    }
    public void delete(FoodLog foodLog) {
        foodLogDao.delete(foodLog);
    }
    public int getLogCountForUser(String userEmail) {
        return foodLogDao.getLogCountForUser(userEmail);
    }
    // Add this method back
    public LiveData<List<FoodLog>> getLogsSince(long startDate) {
        return foodLogDao.getLogsSince(startDate);
    }

    // ... (other methods remain the same)
    public LiveData<List<FoodLog>> getLogsForDate(long startOfDay, long endOfDay) { return foodLogDao.getLogsForDate(startOfDay, endOfDay); }
    public LiveData<Integer> getTotalWaterForDate(long startOfDay, long endOfDay) { return waterLogDao.getTotalWaterForDate(startOfDay, endOfDay); }
    public void insertWaterLog(WaterLog waterLog) {
        waterLogDao.insert(waterLog);
    }
    public LiveData<List<String>> getEarnedAchievementIdsForUser(String userEmail) { return achievementDao.getEarnedAchievementIdsForUser(userEmail); }
    public LiveData<List<Achievement>> getAchievementsByIds(List<String> ids) { return achievementDao.getAchievementsByIds(ids); }

    public LiveData<List<Achievement>> getAllAchievements() {
        return achievementDao.getAllAchievements();
    }
    public List<FoodLog> getLogsForExport(String userEmail, long startDate, long endDate) {
        // 这是一个同步调用，必须在后台线程执行
        return foodLogDao.getLogsForExport(userEmail, startDate, endDate);
    }

    public User findUserByEmailOnce(String email) {
        return userDao.findUserByEmailOnce(email);
    }

    public void insertUser(User user) {
        // We will now handle threading in the ViewModel, but for consistency with other DAOs
        userDao.insert(user);
    }
}