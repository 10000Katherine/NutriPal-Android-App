package com.nutripal.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "NutripalPrefs";
    private static final String KEY_USER_LOGGED_IN = "user_logged_in";
    private static final String KEY_NAME = "name";
    private static final String KEY_AGE = "age";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";

    // ↓↓↓ 新增一个 Key 用来存储邮箱 ↓↓↓
    private static final String KEY_LOGGED_IN_EMAIL = "loggedInEmail";
    private static final String KEY_LAST_CLOUD_SYNC_TIME = "lastCloudSyncTime";
    private static final String KEY_GOAL_TYPE = "goalType";
    private static final String KEY_CUSTOM_CALORIE_GOAL = "customCalorieGoal";
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_TEXT_SCALE = "textScale";
    private static final String KEY_COLOR_THEME = "colorTheme";
    private static final String KEY_LAST_CLOUD_SYNC_ATTEMPT_TIME = "lastCloudSyncAttemptTime";
    private static final String KEY_LAST_CLOUD_SYNC_SUCCESS = "lastCloudSyncSuccess";

    private final SharedPreferences prefs;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setUserLoggedIn(boolean loggedIn) {
        prefs.edit().putBoolean(KEY_USER_LOGGED_IN, loggedIn).apply();
    }

    public boolean isUserLoggedIn() {
        return prefs.getBoolean(KEY_USER_LOGGED_IN, false);
    }

    public void setLoggedInUserEmail(String email) {
        prefs.edit().putString(KEY_LOGGED_IN_EMAIL, email).apply();
    }

    public String getLoggedInUserEmail() {
        return prefs.getString(KEY_LOGGED_IN_EMAIL, null); // 如果没存过，返回 null
    }

    public void setLastCloudSyncTime(long timestamp) {
        prefs.edit().putLong(KEY_LAST_CLOUD_SYNC_TIME, timestamp).apply();
    }

    public long getLastCloudSyncTime() {
        return prefs.getLong(KEY_LAST_CLOUD_SYNC_TIME, 0L);
    }

    public void setGoalType(String goalType) {
        prefs.edit().putString(KEY_GOAL_TYPE, goalType).apply();
    }

    public String getGoalType() {
        return prefs.getString(KEY_GOAL_TYPE, "maintain");
    }

    public void setCustomCalorieGoal(int customCalorieGoal) {
        prefs.edit().putInt(KEY_CUSTOM_CALORIE_GOAL, customCalorieGoal).apply();
    }

    public int getCustomCalorieGoal() {
        return prefs.getInt(KEY_CUSTOM_CALORIE_GOAL, 0);
    }

    public void setDarkModeEnabled(boolean darkModeEnabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, darkModeEnabled).apply();
    }

    public boolean isDarkModeEnabled() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setTextScale(float textScale) {
        prefs.edit().putFloat(KEY_TEXT_SCALE, textScale).apply();
    }

    public float getTextScale() {
        return prefs.getFloat(KEY_TEXT_SCALE, 1.0f);
    }

    public void setColorTheme(String colorTheme) {
        prefs.edit().putString(KEY_COLOR_THEME, colorTheme).apply();
    }

    public String getColorTheme() {
        return prefs.getString(KEY_COLOR_THEME, "green");
    }

    public void setLastCloudSyncAttemptTime(long timestamp) {
        prefs.edit().putLong(KEY_LAST_CLOUD_SYNC_ATTEMPT_TIME, timestamp).apply();
    }

    public long getLastCloudSyncAttemptTime() {
        return prefs.getLong(KEY_LAST_CLOUD_SYNC_ATTEMPT_TIME, 0L);
    }

    public void setLastCloudSyncSuccess(boolean success) {
        prefs.edit().putBoolean(KEY_LAST_CLOUD_SYNC_SUCCESS, success).apply();
    }

    public boolean wasLastCloudSyncSuccessful() {
        return prefs.getBoolean(KEY_LAST_CLOUD_SYNC_SUCCESS, false);
    }


    public void setOnboardingCompleted(boolean completed) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply();
    }

    public boolean isOnboardingCompleted() {
        return prefs.getBoolean("onboarding_completed", false);
    }


    public void setName(String name) {
        prefs.edit().putString(KEY_NAME, name).apply();
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public void setAge(int age) {
        prefs.edit().putInt(KEY_AGE, age).apply();
    }

    public int getAge() {
        return prefs.getInt(KEY_AGE, 0);
    }

    public void setGender(String gender) {
        prefs.edit().putString(KEY_GENDER, gender).apply();
    }

    public String getGender() {
        return prefs.getString(KEY_GENDER, "");
    }

    public void setHeight(float height) {
        prefs.edit().putFloat(KEY_HEIGHT, height).apply();
    }

    public float getHeight() {
        return prefs.getFloat(KEY_HEIGHT, 0.0f);
    }

    public void setWeight(float weight) {
        prefs.edit().putFloat(KEY_WEIGHT, weight).apply();
    }

    public float getWeight() {
        return prefs.getFloat(KEY_WEIGHT, 0.0f);
    }
}
