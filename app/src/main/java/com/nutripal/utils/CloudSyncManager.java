package com.nutripal.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.nutripal.models.FoodLog;
import com.nutripal.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudSyncManager {

    private static final int MAX_RETRY_ATTEMPTS = 2;

    public interface SyncCallback {
        void onSuccess(String message);

        void onFailure(String message);
    }

    public void syncUserAndLogs(@NonNull Context context,
                                @NonNull String userEmail,
                                User user,
                                @NonNull List<FoodLog> logs,
                                @NonNull SyncCallback callback) {
        syncUserAndLogsWithRetry(context, userEmail, user, logs, callback, 0);
    }

    private void syncUserAndLogsWithRetry(@NonNull Context context,
                                          @NonNull String userEmail,
                                          User user,
                                          @NonNull List<FoodLog> logs,
                                          @NonNull SyncCallback callback,
                                          int attempt) {
        try {
            FirebaseApp app = FirebaseApp.initializeApp(context);
            if (app == null) {
                callback.onFailure("Firebase is not configured. Add google-services.json first.");
                return;
            }

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            String safeUserId = userEmail.replace('.', '_');

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("email", userEmail);
            if (user != null) {
                userMap.put("name", user.getName());
                userMap.put("age", user.getAge());
                userMap.put("gender", user.getGender());
                userMap.put("height", user.getHeight());
                userMap.put("weight", user.getWeight());
                userMap.put("activityLevel", user.getActivityLevel());
                userMap.put("vegetarian", user.isVegetarian());
                userMap.put("vegan", user.isVegan());
                userMap.put("glutenFree", user.isGlutenFree());
                userMap.put("dairyFree", user.isDairyFree());
                userMap.put("waterGoal", user.getWaterGoal());
            }

            firestore.collection("users").document(safeUserId).set(userMap)
                    .addOnSuccessListener(unused -> {
                        WriteBatch batch = firestore.batch();
                        for (FoodLog log : logs) {
                            String foodName = log.getFoodName() == null ? "food" : log.getFoodName();
                            String docId = log.getId() > 0
                                    ? String.valueOf(log.getId())
                                    : log.getDate() + "_" + Math.abs(foodName.hashCode());

                            Map<String, Object> logMap = new HashMap<>();
                            logMap.put("foodName", foodName);
                            logMap.put("calories", log.getCalories());
                            logMap.put("protein", log.getProtein());
                            logMap.put("carbs", log.getCarbs());
                            logMap.put("fat", log.getFat());
                            logMap.put("quantity", log.getQuantity());
                            logMap.put("mealType", log.getMealType());
                            logMap.put("date", log.getDate());
                            logMap.put("userEmail", log.getUserEmail());

                            batch.set(
                                    firestore.collection("users")
                                            .document(safeUserId)
                                            .collection("foodLogs")
                                            .document(docId),
                                    logMap
                            );
                        }

                        batch.commit()
                                .addOnSuccessListener(result -> callback.onSuccess("Cloud sync completed."))
                                .addOnFailureListener(e -> retryOrFail(
                                        context,
                                        userEmail,
                                        user,
                                        logs,
                                        callback,
                                        attempt,
                                        "Cloud sync failed"
                                ));
                    })
                    .addOnFailureListener(e -> retryOrFail(
                            context,
                            userEmail,
                            user,
                            logs,
                            callback,
                            attempt,
                            "User sync failed"
                    ));
        } catch (Exception e) {
            retryOrFail(context, userEmail, user, logs, callback, attempt, "Cloud sync unavailable");
        }
    }

    private void retryOrFail(@NonNull Context context,
                             @NonNull String userEmail,
                             User user,
                             @NonNull List<FoodLog> logs,
                             @NonNull SyncCallback callback,
                             int attempt,
                             @NonNull String errorPrefix) {
        if (attempt < MAX_RETRY_ATTEMPTS) {
            syncUserAndLogsWithRetry(context, userEmail, user, logs, callback, attempt + 1);
            return;
        }

        callback.onFailure(errorPrefix + " after retries. Please check network/Firebase config.");
    }
}
