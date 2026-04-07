package com.nutripal.utils;

import android.app.Activity;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.nutripal.R;

public class AppAppearanceManager {

    public static void applyAppearance(Activity activity, PreferenceManager preferenceManager) {
        if (preferenceManager == null || activity == null) {
            return;
        }

        AppCompatDelegate.setDefaultNightMode(
                preferenceManager.isDarkModeEnabled()
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    @ColorRes
    public static int resolvePrimaryColorRes(String colorTheme) {
        if ("blue".equals(colorTheme)) {
            return R.color.primary_blue;
        }
        return R.color.primary_green;
    }

    public static int resolvePrimaryColor(Activity activity, PreferenceManager preferenceManager) {
        String colorTheme = preferenceManager.getColorTheme();
        return ContextCompat.getColor(activity, resolvePrimaryColorRes(colorTheme));
    }
}
