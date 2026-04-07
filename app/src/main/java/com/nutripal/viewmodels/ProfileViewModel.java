package com.nutripal.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.nutripal.models.Achievement;
import com.nutripal.models.FoodLog;
import com.nutripal.models.User;
import com.nutripal.repositories.FoodLogRepository;
import com.nutripal.utils.CloudSyncManager;
import com.nutripal.utils.PreferenceManager;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileViewModel extends AndroidViewModel {
    private final FoodLogRepository repository;
    private final ExecutorService executorService;
    private final PreferenceManager preferenceManager;
    private final CloudSyncManager cloudSyncManager;

    // LiveData for the CSV export feature
    private final MutableLiveData<String> csvContent = new MutableLiveData<>();
    private final MutableLiveData<String> cloudSyncStatus = new MutableLiveData<>();
    private final MutableLiveData<String> lastCloudSyncInfo = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogRepository(application);
        executorService = Executors.newSingleThreadExecutor();
        preferenceManager = new PreferenceManager(application);
        cloudSyncManager = new CloudSyncManager();
        refreshLastCloudSyncInfo();
    }

    // --- Getters for UI ---
    public LiveData<User> getUserByEmail(String email) {
        return repository.getUserByEmail(email);
    }

    public LiveData<List<Achievement>> getAchievementsForUser(String userEmail) {
        // This is the fully implemented method.
        // It gets the IDs of earned badges, then uses those IDs to get the full badge details.
        LiveData<List<String>> earnedIdsLiveData = repository.getEarnedAchievementIdsForUser(userEmail);
        return Transformations.switchMap(earnedIdsLiveData, ids -> {
            if (ids == null || ids.isEmpty()) {
                // If user has no achievements, return an empty list.
                return new MutableLiveData<>(new ArrayList<>());
            }
            return repository.getAchievementsByIds(ids);
        });
    }

    public LiveData<String> getCsvContent() {
        return csvContent;
    }

    public LiveData<String> getCloudSyncStatus() {
        return cloudSyncStatus;
    }

    public LiveData<String> getLastCloudSyncInfo() {
        return lastCloudSyncInfo;
    }


    // --- Methods to handle actions ---
    public void updateUser(User user) {
        executorService.execute(() -> repository.updateUser(user));
    }

    public void prepareCsvData(String dateRange) {
        executorService.execute(() -> {
            String userEmail = preferenceManager.getLoggedInUserEmail();
            if (userEmail == null || userEmail.isEmpty()) return;

            Calendar cal = Calendar.getInstance();
            long endDate = cal.getTimeInMillis();
            long startDate;

            switch (dateRange) {
                case "Last 7 Days":
                    cal.add(Calendar.DAY_OF_YEAR, -7);
                    startDate = cal.getTimeInMillis();
                    break;
                case "Last 30 Days":
                    cal.add(Calendar.DAY_OF_YEAR, -30);
                    startDate = cal.getTimeInMillis();
                    break;
                default: // "All Time"
                    startDate = 0L;
                    break;
            }

            List<FoodLog> logs = repository.getLogsForExport(userEmail, startDate, endDate);
            String content = createCsvContent(logs);
            csvContent.postValue(content);
        });
    }

    public void writeDataToUri(Uri uri) {
        if (uri != null && csvContent.getValue() != null) {
            String contentToWrite = csvContent.getValue();
            executorService.execute(() -> {
                try {
                    OutputStream os = getApplication().getContentResolver().openOutputStream(uri);
                    OutputStreamWriter writer = new OutputStreamWriter(os);
                    writer.write(contentToWrite);
                    writer.flush();
                    writer.close();
                    os.close();
                    Log.d("Export", "Successfully wrote to file.");
                } catch (Exception e) {
                    Log.e("Export", "Error writing to file", e);
                }
            });
        }
    }

    public void onCsvDataReady() {
        // Reset the event so the save dialog is not triggered again on screen rotation
        // The actual content is still needed for writeDataToUri
    }

    public void syncCurrentUserAndLogsToCloud() {
        executorService.execute(() -> {
            String userEmail = preferenceManager.getLoggedInUserEmail();
            if (userEmail == null || userEmail.isEmpty()) {
                cloudSyncStatus.postValue("Please login first.");
                return;
            }

            long attemptTime = System.currentTimeMillis();
            preferenceManager.setLastCloudSyncAttemptTime(attemptTime);
            refreshLastCloudSyncInfo();

            User user = repository.findUserByEmailOnce(userEmail);
            List<FoodLog> logs = repository.getLogsForExport(userEmail, 0L, System.currentTimeMillis());

            cloudSyncManager.syncUserAndLogs(
                    getApplication(),
                    userEmail,
                    user,
                    logs,
                    new CloudSyncManager.SyncCallback() {
                        @Override
                        public void onSuccess(String message) {
                            preferenceManager.setLastCloudSyncTime(System.currentTimeMillis());
                            preferenceManager.setLastCloudSyncSuccess(true);
                            refreshLastCloudSyncInfo();
                            cloudSyncStatus.postValue(message + " Synced logs: " + logs.size());
                        }

                        @Override
                        public void onFailure(String message) {
                            preferenceManager.setLastCloudSyncSuccess(false);
                            refreshLastCloudSyncInfo();
                            cloudSyncStatus.postValue(message);
                        }
                    }
            );
        });
    }

    private String createCsvContent(List<FoodLog> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("Date,Meal,Food Name,Quantity (g),Calories (kcal),Protein (g),Carbs (g),Fat (g)\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        for (FoodLog log : logs) {
            sb.append(sdf.format(new Date(log.getDate()))).append(",");
            sb.append(log.getMealType()).append(",");
            sb.append("\"").append(log.getFoodName().replace("\"", "\"\"")).append("\","); // Handle quotes in names
            sb.append(String.format(Locale.US, "%.1f", log.getQuantity())).append(",");
            sb.append(String.format(Locale.US, "%.1f", log.getCalories())).append(",");
            sb.append(String.format(Locale.US, "%.1f", log.getProtein())).append(",");
            sb.append(String.format(Locale.US, "%.1f", log.getCarbs())).append(",");
            sb.append(String.format(Locale.US, "%.1f", log.getFat())).append("\n");
        }
        return sb.toString();
    }

    private void refreshLastCloudSyncInfo() {
        long lastAttemptTime = preferenceManager.getLastCloudSyncAttemptTime();
        long lastSyncTime = preferenceManager.getLastCloudSyncTime();
        if (lastAttemptTime <= 0L && lastSyncTime <= 0L) {
            lastCloudSyncInfo.postValue("Last cloud sync: Never");
            return;
        }

        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

        if (lastSyncTime > 0L && preferenceManager.wasLastCloudSyncSuccessful()) {
            String formattedSuccessTime = displayFormat.format(new Date(lastSyncTime));
            lastCloudSyncInfo.postValue("Last cloud sync: Success at " + formattedSuccessTime);
            return;
        }

        String formattedAttemptTime = displayFormat.format(new Date(lastAttemptTime));
        lastCloudSyncInfo.postValue("Last cloud sync: Attempted at " + formattedAttemptTime + " (not synced)");
    }
}
