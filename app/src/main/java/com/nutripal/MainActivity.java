package com.nutripal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nutripal.activities.BaseActivity;
import com.nutripal.utils.AppAppearanceManager;
import com.nutripal.utils.PreferenceManager;

import android.content.res.ColorStateList;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager preferenceManager = new PreferenceManager(this);
        AppAppearanceManager.applyAppearance(this, preferenceManager);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);

        int primaryColor = AppAppearanceManager.resolvePrimaryColor(this, preferenceManager);
        toolbar.setBackgroundColor(primaryColor);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{}
        };
        int[] colors = new int[]{
                primaryColor,
                ContextCompat.getColor(this, R.color.gray_dark)
        };
        ColorStateList bottomNavColors = new ColorStateList(states, colors);
        bottomNav.setItemIconTintList(bottomNavColors);
        bottomNav.setItemTextColor(bottomNavColors);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.nav_home);
        topLevelDestinations.add(R.id.nav_food_log);
        topLevelDestinations.add(R.id.nav_progress);
        topLevelDestinations.add(R.id.nav_profile);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(bottomNav, navController);

        requestNotificationPermissionIfNeeded();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                2001
        );
    }
}
