package com.nutripal.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData; // 1. Import MutableLiveData

import com.nutripal.models.FoodLog;
import com.nutripal.models.FoodSuggestion; // 2. Import our new models
import com.nutripal.models.User;
import com.nutripal.models.WaterLog;
import com.nutripal.repositories.FoodLogRepository;
import com.nutripal.utils.PreferenceManager;
import com.nutripal.utils.SuggestionEngine; // 3. Import the SuggestionEngine

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends AndroidViewModel {

    private final FoodLogRepository repository;
    private final LiveData<User> currentUser;
    private final LiveData<List<FoodLog>> todaysLogs;
    private final LiveData<Integer> todaysWaterIntake;
    private final MediatorLiveData<HomeScreenData> homeScreenData = new MediatorLiveData<>();
    private final ExecutorService executorService;

    // --- 4. Add LiveData for our suggestions ---
    private final MutableLiveData<SuggestionData> suggestionData = new MutableLiveData<>();


    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogRepository(application);
        executorService = Executors.newSingleThreadExecutor();
        PreferenceManager preferenceManager = new PreferenceManager(application);
        String userEmail = preferenceManager.getLoggedInUserEmail();

        if (userEmail != null && !userEmail.isEmpty()) {
            currentUser = repository.getUserByEmail(userEmail);
            LocalDate today = LocalDate.now();
            long startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
            todaysLogs = repository.getLogsForDate(startOfDay, endOfDay);
            todaysWaterIntake = repository.getTotalWaterForDate(startOfDay, endOfDay);
            homeScreenData.addSource(currentUser, user -> combineData());
            homeScreenData.addSource(todaysLogs, logs -> combineData());
            homeScreenData.addSource(todaysWaterIntake, water -> combineData());
        } else {
            currentUser = null;
            todaysLogs = null;
            todaysWaterIntake = null;
        }
    }

    private void combineData() {
        if (currentUser == null || todaysLogs == null || currentUser.getValue() == null || todaysLogs.getValue() == null) {
            return;
        }

        User user = currentUser.getValue();
        List<FoodLog> logs = todaysLogs.getValue();
        Integer water = (todaysWaterIntake != null) ? todaysWaterIntake.getValue() : null;
        int waterConsumed = (water == null) ? 0 : water;

        double caloriesConsumed = logs.stream().mapToDouble(FoodLog::getCalories).sum();
        double proteinConsumed = logs.stream().mapToDouble(FoodLog::getProtein).sum();
        double carbsConsumed = logs.stream().mapToDouble(FoodLog::getCarbs).sum();
        double fatConsumed = logs.stream().mapToDouble(FoodLog::getFat).sum();

        double bmr = (user.getGender() != null && user.getGender().equalsIgnoreCase("Male")) ?
                (10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5) :
                (10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161);
        double calorieGoal = bmr * 1.2;

        homeScreenData.setValue(new HomeScreenData(
                (int) caloriesConsumed, (int) calorieGoal,
                (int) proteinConsumed, (int) carbsConsumed, (int) fatConsumed,
                waterConsumed
        ));

        // --- 5. New logic to generate suggestions ---
        generateFoodSuggestions(user, proteinConsumed);
    }

    private void generateFoodSuggestions(User user, double proteinConsumed) {
        // Simple protein goal: 1g of protein per kg of body weight
        double proteinGoal = user.getWeight();

        // If user has eaten less than half their protein goal, suggest protein foods.
        if (proteinGoal > 0 && proteinConsumed < (proteinGoal / 2)) {
            List<FoodSuggestion> suggestions = SuggestionEngine.getProteinSuggestions();
            suggestionData.setValue(new SuggestionData("Need more protein?", suggestions));
        } else {
            // In other cases, clear the suggestions
            suggestionData.setValue(null);
        }
    }

    public LiveData<HomeScreenData> getHomeScreenData() { return homeScreenData; }
    public LiveData<SuggestionData> getSuggestionData() { return suggestionData; } // Getter for new LiveData

    public void addWater(int amountMl) {
        executorService.execute(() -> {
            WaterLog waterLog = new WaterLog();
            waterLog.setAmountMl(amountMl);
            waterLog.setDate(System.currentTimeMillis());
            // Proactively adding userEmail here as well
            String userEmail = new PreferenceManager(getApplication()).getLoggedInUserEmail();
            if (userEmail != null) {
                waterLog.setUserEmail(userEmail); // Assumes WaterLog has this field
            }
            repository.insertWaterLog(waterLog);
        });
    }

    // --- 6. New inner classes to hold our data cleanly ---
    public static class HomeScreenData {
        public final int caloriesConsumed, calorieGoal, proteinConsumed, carbsConsumed, fatConsumed, waterConsumed;
        public HomeScreenData(int cal, int goal, int pro, int carb, int fat, int water) {
            this.caloriesConsumed = cal; this.calorieGoal = goal;
            this.proteinConsumed = pro; this.carbsConsumed = carb; this.fatConsumed = fat;
            this.waterConsumed = water;
        }
    }

    public static class SuggestionData {
        public final String title;
        public final List<FoodSuggestion> suggestions;
        public SuggestionData(String title, List<FoodSuggestion> suggestions) {
            this.title = title;
            this.suggestions = suggestions;
        }
    }
}