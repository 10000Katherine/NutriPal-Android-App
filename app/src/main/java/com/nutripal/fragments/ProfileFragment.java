package com.nutripal.fragments;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.nutripal.viewmodels.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private PreferenceManager preferenceManager;
    private EditText etName, etAge, etGender, etHeight, etWeight;
    private Button btnSave, btnExport; // 1. Add export button variable
    private LiveData<User> userLiveData;
    private User currentUser;
    private SwitchMaterial switchVegetarian, switchVegan, switchGlutenFree, switchDairyFree;
    private RecyclerView achievementsRecyclerView;
    private AchievementAdapter achievementAdapter;

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

        btnSave.setOnClickListener(v -> saveProfileChanges());
        btnExport.setOnClickListener(v -> showDateRangeDialog()); // Set listener for new button
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.editText_profile_name);
        etAge = view.findViewById(R.id.editText_profile_age);
        // ... (all your other findViewById calls)
        etGender = view.findViewById(R.id.editText_profile_gender);
        etHeight = view.findViewById(R.id.editText_profile_height);
        etWeight = view.findViewById(R.id.editText_profile_weight);
        btnSave = view.findViewById(R.id.button_save_profile);
        switchVegetarian = view.findViewById(R.id.switch_vegetarian);
        switchVegan = view.findViewById(R.id.switch_vegan);
        switchGlutenFree = view.findViewById(R.id.switch_gluten_free);
        switchDairyFree = view.findViewById(R.id.switch_dairy_free);
        achievementsRecyclerView = view.findViewById(R.id.recyclerView_achievements);
        achievementAdapter = new AchievementAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        achievementsRecyclerView.setLayoutManager(layoutManager);
        achievementsRecyclerView.setAdapter(achievementAdapter);

        // Find the new export button
        btnExport = view.findViewById(R.id.button_export_data);
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
    private void saveProfileChanges() { /* ... remains the same ... */ }
}