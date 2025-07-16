package com.nutripal.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.nutripal.database.AppDatabase;
import com.nutripal.database.dao.AchievementDao;
import com.nutripal.models.FoodLog;
import com.nutripal.models.UserAchievement;
import com.nutripal.repositories.FoodLogRepository;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodLogViewModel extends AndroidViewModel {

    private final FoodLogRepository repository;
    private final AchievementDao achievementDao;
    private final ExecutorService executorService;
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();
    private final LiveData<List<FoodLog>> logsForSelectedDate;
    private final MutableLiveData<String> newAchievementUnlocked = new MutableLiveData<>();

    public FoodLogViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogRepository(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        achievementDao = db.achievementDao();
        executorService = Executors.newSingleThreadExecutor();
        selectedDate.setValue(Calendar.getInstance());
        logsForSelectedDate = Transformations.switchMap(selectedDate, date -> {
            // ... (this part is unchanged)
            Calendar startCal = (Calendar) date.clone(); startCal.set(Calendar.HOUR_OF_DAY, 0); startCal.set(Calendar.MINUTE, 0); startCal.set(Calendar.SECOND, 0); long startOfDay = startCal.getTimeInMillis();
            Calendar endCal = (Calendar) date.clone(); endCal.add(Calendar.DAY_OF_YEAR, 1); endCal.set(Calendar.HOUR_OF_DAY, 0); endCal.set(Calendar.MINUTE, 0); endCal.set(Calendar.SECOND, 0); long endOfDay = endCal.getTimeInMillis() - 1;
            return repository.getLogsForDate(startOfDay, endOfDay);
        });
    }

    // Getters
    public LiveData<List<FoodLog>> getLogsForSelectedDate() { return logsForSelectedDate; }
    public LiveData<Calendar> getSelectedDate() { return selectedDate; }
    public LiveData<String> getNewAchievementUnlocked() { return newAchievementUnlocked; }
    public void onAchievementShown() { newAchievementUnlocked.setValue(null); }

    // Day navigation methods
    public void nextDay() { Calendar newDate = (Calendar) selectedDate.getValue().clone(); newDate.add(Calendar.DAY_OF_YEAR, 1); selectedDate.setValue(newDate); }
    public void previousDay() { Calendar newDate = (Calendar) selectedDate.getValue().clone(); newDate.add(Calendar.DAY_OF_YEAR, -1); selectedDate.setValue(newDate); }

    public void insert(FoodLog foodLog) {
        executorService.execute(() -> {
            // Because the repository methods are now direct, these will execute in order.
            // 1. Insert the food log
            repository.insert(foodLog);
            // 2. Check for achievements
            checkForFirstLogAchievement(foodLog.getUserEmail());
        });
    }

    private void checkForFirstLogAchievement(String userEmail) {
        if (userEmail == null) return;
        int existingCount = achievementDao.hasUserEarnedAchievement(userEmail, "FIRST_LOG");
        if (existingCount > 0) return;

        // 3. Count the logs. This will now correctly run AFTER the insert is complete.
        int totalLogs = repository.getLogCountForUser(userEmail);

        if (totalLogs == 1) {
            // 4. Award the badge
            UserAchievement userAchievement = new UserAchievement(userEmail, "FIRST_LOG", System.currentTimeMillis());
            achievementDao.insertUserAchievement(userAchievement);
            newAchievementUnlocked.postValue("First Log");
        }
    }

    public void delete(FoodLog foodLog) {
        executorService.execute(() -> repository.delete(foodLog));
    }
}