package com.nutripal.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.github.mikephil.charting.data.BarEntry;
import com.nutripal.models.FoodLog;
import com.nutripal.models.User;
import com.nutripal.repositories.FoodLogRepository;
import com.nutripal.utils.PreferenceManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProgressViewModel extends AndroidViewModel {

    public static class ChartData {
        public final List<BarEntry> entries;
        public final List<String> labels;
        public final int calorieGoal;
        ChartData(List<BarEntry> e, List<String> l, int g) { this.entries = e; this.labels = l; this.calorieGoal = g; }
    }

    private final MediatorLiveData<ChartData> chartData = new MediatorLiveData<>();
    private final MutableLiveData<String> weeklySummary = new MutableLiveData<>();

    public ProgressViewModel(@NonNull Application application) {
        super(application);
        FoodLogRepository repository = new FoodLogRepository(application);
        PreferenceManager preferenceManager = new PreferenceManager(application);
        String userEmail = preferenceManager.getLoggedInUserEmail();

        if (userEmail != null && !userEmail.isEmpty()) {
            LiveData<User> currentUser = repository.getUserByEmail(userEmail);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -6);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            long startDate = cal.getTimeInMillis();
            LiveData<List<FoodLog>> recentLogs = repository.getLogsSince(startDate);

            chartData.addSource(currentUser, user -> combineData(user, recentLogs.getValue()));
            chartData.addSource(recentLogs, logs -> combineData(currentUser.getValue(), logs));
        }
    }

    private void combineData(User user, List<FoodLog> logs) {
        if (user == null || logs == null) return;

        PreferenceManager preferenceManager = new PreferenceManager(getApplication());
        int calorieGoal = resolveCalorieGoal(user, preferenceManager);

        Map<String, Double> dailyTotals = new LinkedHashMap<>();
        SimpleDateFormat keyFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (FoodLog log : logs) {
            String dayKey = keyFormatter.format(new Date(log.getDate()));
            Double currentTotal = dailyTotals.getOrDefault(dayKey, 0.0);
            dailyTotals.put(dayKey, currentTotal + log.getCalories());
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        SimpleDateFormat labelFormatter = new SimpleDateFormat("MMM dd", Locale.getDefault());
        Calendar dayIterator = Calendar.getInstance();

        for (int i = 6; i >= 0; i--) {
            dayIterator.setTime(new Date());
            dayIterator.add(Calendar.DAY_OF_YEAR, -i);
            String dayKey = keyFormatter.format(dayIterator.getTime());
            String label = labelFormatter.format(dayIterator.getTime());
            Double total = dailyTotals.getOrDefault(dayKey, 0.0);
            entries.add(new BarEntry(6 - i, total.floatValue()));
            labels.add(label);
        }

        chartData.setValue(new ChartData(entries, labels, calorieGoal));
        weeklySummary.setValue(buildWeeklySummary(entries, calorieGoal));
    }

    private String buildWeeklySummary(List<BarEntry> entries, int calorieGoal) {
        if (entries == null || entries.isEmpty()) {
            return "No weekly summary available yet.";
        }

        double totalCalories = 0.0;
        int daysAboveGoal = 0;
        for (BarEntry entry : entries) {
            totalCalories += entry.getY();
            if (entry.getY() > calorieGoal) {
                daysAboveGoal++;
            }
        }

        int dayCount = entries.size();
        int averageCalories = (int) Math.round(totalCalories / dayCount);
        int daysWithinGoal = dayCount - daysAboveGoal;

        return "Weekly Summary (Last 7 Days):\n"
                + "Average Calories: " + averageCalories + " kcal/day\n"
                + "Daily Goal: " + calorieGoal + " kcal\n"
                + "Days Within Goal: " + daysWithinGoal + " / " + dayCount + "\n"
                + "Days Above Goal: " + daysAboveGoal + " / " + dayCount;
    }

    private int resolveCalorieGoal(User user, PreferenceManager preferenceManager) {
        double bmr = (user.getGender() != null && user.getGender().equalsIgnoreCase("Male"))
                ? (10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() + 5)
                : (10 * user.getWeight() + 6.25 * user.getHeight() - 5 * user.getAge() - 161);

        String goalType = preferenceManager.getGoalType();
        if (goalType == null) {
            goalType = "maintain";
        }

        switch (goalType) {
            case "loss":
                return Math.max(1200, (int) Math.round(bmr * 0.85));
            case "gain":
                return Math.max(1200, (int) Math.round(bmr * 1.15));
            case "custom":
                int customCalorieGoal = preferenceManager.getCustomCalorieGoal();
                return customCalorieGoal > 0 ? customCalorieGoal : Math.max(1200, (int) Math.round(bmr));
            case "maintain":
            default:
                return Math.max(1200, (int) Math.round(bmr));
        }
    }

    public LiveData<ChartData> getChartData() {
        return chartData;
    }

    public LiveData<String> getWeeklySummary() {
        return weeklySummary;
    }
}
