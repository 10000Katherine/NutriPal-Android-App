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
import com.nutripal.utils.PreferenceManager;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodLogViewModel extends AndroidViewModel {

    private final FoodLogRepository repository;
    private final AchievementDao achievementDao;
    private final ExecutorService executorService;
    private final PreferenceManager preferenceManager;

    // All LiveData declarations from both versions
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();
    private final LiveData<List<FoodLog>> logsForSelectedDate;
    private final MutableLiveData<String> newAchievementUnlocked = new MutableLiveData<>();

    public FoodLogViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogRepository(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        achievementDao = db.achievementDao();
        executorService = Executors.newSingleThreadExecutor();
        preferenceManager = new PreferenceManager(application);

        // Set initial date
        selectedDate.setValue(Calendar.getInstance());

        // The original Transformation logic for getting logs based on the selected date
        logsForSelectedDate = Transformations.switchMap(selectedDate, date -> {
            Calendar startCal = (Calendar) date.clone();
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            long startOfDay = startCal.getTimeInMillis();

            Calendar endCal = (Calendar) date.clone();
            endCal.add(Calendar.DAY_OF_YEAR, 1);
            endCal.set(Calendar.HOUR_OF_DAY, 0);
            endCal.set(Calendar.MINUTE, 0);
            endCal.set(Calendar.SECOND, 0);
            long endOfDay = endCal.getTimeInMillis() - 1;

            return repository.getLogsForDate(startOfDay, endOfDay);
        });
    }

    // --- All Getters ---
    public LiveData<List<FoodLog>> getLogsForSelectedDate() { return logsForSelectedDate; }
    public LiveData<Calendar> getSelectedDate() { return selectedDate; }
    public LiveData<String> getNewAchievementUnlocked() { return newAchievementUnlocked; }
    public void onAchievementShown() { newAchievementUnlocked.setValue(null); }

    // --- All Public Methods ---
    public void nextDay() {
        Calendar oldDate = selectedDate.getValue();
        if (oldDate != null) {
            Calendar newDate = (Calendar) oldDate.clone();
            newDate.add(Calendar.DAY_OF_YEAR, 1);
            selectedDate.setValue(newDate);
        }
    }

    public void previousDay() {
        Calendar oldDate = selectedDate.getValue();
        if (oldDate != null) {
            Calendar newDate = (Calendar) oldDate.clone();
            newDate.add(Calendar.DAY_OF_YEAR, -1);
            selectedDate.setValue(newDate);
        }
    }

    public void insert(FoodLog foodLog) {
        executorService.execute(() -> {
            String userEmail = preferenceManager.getLoggedInUserEmail();
            if (userEmail == null || userEmail.isEmpty()) {
                return;
            }
            foodLog.setUserEmail(userEmail);
            repository.insert(foodLog);
            checkForFirstLogAchievement(userEmail);
        });
    }

    public void delete(FoodLog foodLog) {
        executorService.execute(() -> repository.delete(foodLog));
    }

    private void checkForFirstLogAchievement(String userEmail) {
        int existingCount = achievementDao.hasUserEarnedAchievement(userEmail, "FIRST_LOG");
        if (existingCount > 0) return;

        int totalLogs = repository.getLogCountForUser(userEmail);
        if (totalLogs == 1) {
            UserAchievement userAchievement = new UserAchievement(userEmail, "FIRST_LOG", System.currentTimeMillis());
            achievementDao.insertUserAchievement(userAchievement);
            newAchievementUnlocked.postValue("First Log");
        }
    }
}