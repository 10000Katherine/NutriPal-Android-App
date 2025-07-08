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