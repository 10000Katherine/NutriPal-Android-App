package com.nutripal.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.nutripal.R;
import com.nutripal.adapters.AchievementAdapter;
import com.nutripal.models.User;
import com.nutripal.utils.PreferenceManager;
import com.nutripal.utils.ReminderScheduler;
import com.nutripal.viewmodels.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private PreferenceManager preferenceManager;
    private EditText etName, etAge, etGender, etHeight, etWeight;
    private EditText etCustomCalorieGoal;
    private TextView textCloudSyncInfo;
    private Spinner spinnerGoalType, spinnerTextSize, spinnerColorTheme;
    private Button btnSave, btnExport, btnCloudSync, btnEnableReminders, btnShareProgress;
    private LiveData<User> userLiveData;
    private User currentUser;
    private SwitchMaterial switchVegetarian, switchVegan, switchGlutenFree, switchDairyFree, switchDarkMode;
    private RecyclerView achievementsRecyclerView;
    private AchievementAdapter achievementAdapter;

    private static final String[] GOAL_LABELS = {
            "Weight Loss",
            "Maintain",
            "Weight Gain",
            "Custom"
    };
    private static final String[] GOAL_VALUES = {
            "loss",
            "maintain",
            "gain",
            "custom"
    };
    private static final String[] TEXT_SIZE_LABELS = {
            "Small",
            "Default",
            "Large"
    };
    private static final float[] TEXT_SIZE_VALUES = {
            0.9f,
            1.0f,
            1.15f
    };
    private static final String[] COLOR_THEME_LABELS = {
            "Green",
            "Blue"
    };
    private static final String[] COLOR_THEME_VALUES = {
            "green",
            "blue"
    };

    // --- 2. Add a launcher to handle the file creation intent ---
    private final ActivityResultLauncher<String> createFileLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/csv"),
            uri -> {
                if (uri != null) {
                    // When user selects a location and file name, the URI is returned.
                    // We then tell the ViewModel to write the data to this URI.
                    viewModel.writeDataToUri(uri);
                    Toast.makeText(getContext(), "Exporting data...", Toast.LENGTH_LONG).show();
                } else {
                    // User cancelled the save dialog
                    Toast.makeText(getContext(), "Export cancelled.", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        preferenceManager = new PreferenceManager(requireContext());
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view); // Using a helper method to initialize views
        setupGoalTypeUi();
        setupAppearanceUi();

        String loggedInUserEmail = preferenceManager.getLoggedInUserEmail();

        if (loggedInUserEmail != null && !loggedInUserEmail.isEmpty()) {
            userLiveData = viewModel.getUserByEmail(loggedInUserEmail);
            userLiveData.observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    currentUser = user;
                    populateUI(user);
                }
            });

            viewModel.getAchievementsForUser(loggedInUserEmail).observe(getViewLifecycleOwner(), earnedAchievements -> {
                if (earnedAchievements != null) {
                    achievementAdapter.setDisplayModeEarnedOnly(earnedAchievements);
                }
            });
        }

        // --- 4. Add a new observer to trigger the file save dialog ---
        viewModel.getCsvContent().observe(getViewLifecycleOwner(), content -> {
            // This is triggered after the ViewModel has prepared the CSV data
            if (content != null && !content.isEmpty()) {
                // Now that we have the content, launch the file saver
                String fileName = "NutriPal_Export_" + System.currentTimeMillis() + ".csv";
                createFileLauncher.launch(fileName);
                viewModel.onCsvDataReady(); // Reset the event
            }
        });

        viewModel.getCloudSyncStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null && !status.isEmpty()) {
                Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getLastCloudSyncInfo().observe(getViewLifecycleOwner(), info -> {
            if (info != null && textCloudSyncInfo != null) {
                textCloudSyncInfo.setText(info);
            }
        });

        btnSave.setOnClickListener(v -> saveProfileChanges());
        btnExport.setOnClickListener(v -> showDateRangeDialog()); // Set listener for new button
        btnCloudSync.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Syncing to cloud...", Toast.LENGTH_SHORT).show();
            viewModel.syncCurrentUserAndLogsToCloud();
        });
        btnEnableReminders.setOnClickListener(v -> {
            ReminderScheduler.scheduleDefaultReminders(requireContext());
            ReminderScheduler.sendTestReminderNow(requireContext());
            Toast.makeText(
                    getContext(),
                    "Daily reminders enabled: " + ReminderScheduler.DEFAULT_REMINDER_SCHEDULE,
                    Toast.LENGTH_LONG
            ).show();
        });
        btnShareProgress.setOnClickListener(v -> shareProgress());
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.editText_profile_name);
        etAge = view.findViewById(R.id.editText_profile_age);
        // ... (all your other findViewById calls)
        etGender = view.findViewById(R.id.editText_profile_gender);
        etHeight = view.findViewById(R.id.editText_profile_height);
        etWeight = view.findViewById(R.id.editText_profile_weight);
        etCustomCalorieGoal = view.findViewById(R.id.editText_custom_calorie_goal);
        btnSave = view.findViewById(R.id.button_save_profile);
        switchVegetarian = view.findViewById(R.id.switch_vegetarian);
        switchVegan = view.findViewById(R.id.switch_vegan);
        switchGlutenFree = view.findViewById(R.id.switch_gluten_free);
        switchDairyFree = view.findViewById(R.id.switch_dairy_free);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        achievementsRecyclerView = view.findViewById(R.id.recyclerView_achievements);
        achievementAdapter = new AchievementAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        achievementsRecyclerView.setLayoutManager(layoutManager);
        achievementsRecyclerView.setAdapter(achievementAdapter);

        // Find the new export button
        btnExport = view.findViewById(R.id.button_export_data);
        btnCloudSync = view.findViewById(R.id.button_cloud_sync);
        btnEnableReminders = view.findViewById(R.id.button_enable_reminders);
        btnShareProgress = view.findViewById(R.id.button_share_progress);
        textCloudSyncInfo = view.findViewById(R.id.text_cloud_sync_info);
        spinnerGoalType = view.findViewById(R.id.spinner_goal_type);
        spinnerTextSize = view.findViewById(R.id.spinner_text_size);
        spinnerColorTheme = view.findViewById(R.id.spinner_color_theme);
    }

    private void shareProgress() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Profile not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String goalType = preferenceManager.getGoalType();
        String goalLabel;
        if ("loss".equals(goalType)) {
            goalLabel = "Weight Loss";
        } else if ("gain".equals(goalType)) {
            goalLabel = "Weight Gain";
        } else if ("custom".equals(goalType)) {
            goalLabel = "Custom Goal";
        } else {
            goalLabel = "Maintain";
        }

        String shareText = "I am using NutriPal to stay healthy!\n"
                + "Goal Type: " + goalLabel + "\n"
                + "Current Weight: " + currentUser.getWeight() + " kg\n"
                + "Water Goal: " + currentUser.getWaterGoal() + " ml/day\n"
                + "Tracking my meals and progress every day.";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My NutriPal Progress");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(shareIntent, "Share progress with"));
    }

    private void setupGoalTypeUi() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                GOAL_LABELS
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoalType.setAdapter(adapter);

        String storedGoalType = preferenceManager.getGoalType();
        int index = goalValueToIndex(storedGoalType);
        spinnerGoalType.setSelection(index);
        updateCustomGoalVisibility(index);

        int customGoal = preferenceManager.getCustomCalorieGoal();
        if (customGoal > 0) {
            etCustomCalorieGoal.setText(String.valueOf(customGoal));
        }

        spinnerGoalType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateCustomGoalVisibility(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void setupAppearanceUi() {
        ArrayAdapter<String> textSizeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                TEXT_SIZE_LABELS
        );
        textSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTextSize.setAdapter(textSizeAdapter);

        ArrayAdapter<String> colorThemeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                COLOR_THEME_LABELS
        );
        colorThemeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColorTheme.setAdapter(colorThemeAdapter);

        switchDarkMode.setChecked(preferenceManager.isDarkModeEnabled());
        spinnerTextSize.setSelection(textScaleToIndex(preferenceManager.getTextScale()));
        spinnerColorTheme.setSelection(colorThemeToIndex(preferenceManager.getColorTheme()));
    }

    private void updateCustomGoalVisibility(int selectedIndex) {
        if (selectedIndex >= 0 && selectedIndex < GOAL_VALUES.length && "custom".equals(GOAL_VALUES[selectedIndex])) {
            etCustomCalorieGoal.setVisibility(View.VISIBLE);
        } else {
            etCustomCalorieGoal.setVisibility(View.GONE);
        }
    }

    private int goalValueToIndex(String value) {
        if (value == null) {
            return 1;
        }
        for (int i = 0; i < GOAL_VALUES.length; i++) {
            if (GOAL_VALUES[i].equals(value)) {
                return i;
            }
        }
        return 1;
    }

    private int textScaleToIndex(float textScale) {
        for (int i = 0; i < TEXT_SIZE_VALUES.length; i++) {
            if (Math.abs(TEXT_SIZE_VALUES[i] - textScale) < 0.02f) {
                return i;
            }
        }
        return 1;
    }

    private int colorThemeToIndex(String colorTheme) {
        if (colorTheme == null) {
            return 0;
        }

        for (int i = 0; i < COLOR_THEME_VALUES.length; i++) {
            if (COLOR_THEME_VALUES[i].equals(colorTheme)) {
                return i;
            }
        }
        return 0;
    }

    // --- 5. Add this new method to show the date range selection dialog ---
    private void showDateRangeDialog() {
        final String[] dateRanges = {"Last 7 Days", "Last 30 Days", "All Time"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Date Range")
                .setItems(dateRanges, (dialog, which) -> {
                    String selectedRange = dateRanges[which];
                    viewModel.prepareCsvData(selectedRange);
                    Toast.makeText(getContext(), "Preparing data...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void populateUI(User user) {
        if (user == null) {
            // 如果 user 对象为空，最好在这里处理一下，防止后续代码出错
            Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 将用户数据设置到 EditText 控件上
        etName.setText(user.getName());
        etGender.setText(user.getGender());
        // 对于数字类型，必须转换为字符串再设置
        etAge.setText(String.valueOf(user.getAge()));
        etHeight.setText(String.valueOf(user.getHeight()));
        etWeight.setText(String.valueOf(user.getWeight()));

        // 设置饮食偏好开关（Switch）的状态
        switchVegetarian.setChecked(user.isVegetarian());
        switchVegan.setChecked(user.isVegan());
        switchGlutenFree.setChecked(user.isGlutenFree());
        switchDairyFree.setChecked(user.isDairyFree());
    }

    private void saveProfileChanges() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "No user data to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean previousDarkMode = preferenceManager.isDarkModeEnabled();
        float previousTextScale = preferenceManager.getTextScale();
        String previousColorTheme = preferenceManager.getColorTheme();

        currentUser.setName(etName.getText().toString().trim());
        currentUser.setGender(etGender.getText().toString().trim());
        currentUser.setAge(safeParseInt(etAge.getText().toString().trim()));
        currentUser.setHeight(safeParseDouble(etHeight.getText().toString().trim()));
        currentUser.setWeight(safeParseDouble(etWeight.getText().toString().trim()));
        currentUser.setVegetarian(switchVegetarian.isChecked());
        currentUser.setVegan(switchVegan.isChecked());
        currentUser.setGlutenFree(switchGlutenFree.isChecked());
        currentUser.setDairyFree(switchDairyFree.isChecked());

        int selectedGoalIndex = spinnerGoalType.getSelectedItemPosition();
        if (selectedGoalIndex < 0 || selectedGoalIndex >= GOAL_VALUES.length) {
            selectedGoalIndex = 1;
        }
        String goalType = GOAL_VALUES[selectedGoalIndex];
        preferenceManager.setGoalType(goalType);

        if ("custom".equals(goalType)) {
            int customGoal = safeParseInt(etCustomCalorieGoal.getText().toString().trim());
            if (customGoal < 1200) {
                Toast.makeText(getContext(), "Custom calorie goal must be at least 1200.", Toast.LENGTH_SHORT).show();
                return;
            }
            preferenceManager.setCustomCalorieGoal(customGoal);
        }

        boolean newDarkMode = switchDarkMode.isChecked();
        preferenceManager.setDarkModeEnabled(newDarkMode);

        int selectedTextSizeIndex = spinnerTextSize.getSelectedItemPosition();
        if (selectedTextSizeIndex < 0 || selectedTextSizeIndex >= TEXT_SIZE_VALUES.length) {
            selectedTextSizeIndex = 1;
        }
        float newTextScale = TEXT_SIZE_VALUES[selectedTextSizeIndex];
        preferenceManager.setTextScale(newTextScale);

        int selectedColorThemeIndex = spinnerColorTheme.getSelectedItemPosition();
        if (selectedColorThemeIndex < 0 || selectedColorThemeIndex >= COLOR_THEME_VALUES.length) {
            selectedColorThemeIndex = 0;
        }
        String newColorTheme = COLOR_THEME_VALUES[selectedColorThemeIndex];
        preferenceManager.setColorTheme(newColorTheme);

        viewModel.updateUser(currentUser);
        Toast.makeText(getContext(), "Profile saved.", Toast.LENGTH_SHORT).show();

        boolean appearanceChanged = previousDarkMode != newDarkMode
                || Math.abs(previousTextScale - newTextScale) > 0.01f
                || (previousColorTheme != null ? !previousColorTheme.equals(newColorTheme) : newColorTheme != null);

        if (appearanceChanged && getActivity() != null) {
            getActivity().recreate();
        }
    }

    private int safeParseInt(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private double safeParseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
    }
}
