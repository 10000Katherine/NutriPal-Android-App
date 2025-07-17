package com.nutripal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager; // 1. Import RecyclerView components
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.nutripal.R;
import com.nutripal.adapters.AchievementAdapter; // 2. Import your new adapter
import com.nutripal.models.User;
import com.nutripal.utils.PreferenceManager;
import com.nutripal.viewmodels.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private PreferenceManager preferenceManager;
    private EditText etName, etAge, etGender, etHeight, etWeight;
    private Button btnSave;
    private LiveData<User> userLiveData;
    private User currentUser;
    private SwitchMaterial switchVegetarian, switchVegan, switchGlutenFree, switchDairyFree;

    // --- 3. Add variables for the new RecyclerView and its adapter ---
    private RecyclerView achievementsRecyclerView;
    private AchievementAdapter achievementAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        preferenceManager = new PreferenceManager(requireContext());
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Find existing views ---
        etName = view.findViewById(R.id.editText_profile_name);
        etAge = view.findViewById(R.id.editText_profile_age);
        etGender = view.findViewById(R.id.editText_profile_gender);
        etHeight = view.findViewById(R.id.editText_profile_height);
        etWeight = view.findViewById(R.id.editText_profile_weight);
        btnSave = view.findViewById(R.id.button_save_profile);
        switchVegetarian = view.findViewById(R.id.switch_vegetarian);
        switchVegan = view.findViewById(R.id.switch_vegan);
        switchGlutenFree = view.findViewById(R.id.switch_gluten_free);
        switchDairyFree = view.findViewById(R.id.switch_dairy_free);

        // --- Setup the RecyclerView ---
        achievementsRecyclerView = view.findViewById(R.id.recyclerView_achievements);
        achievementAdapter = new AchievementAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        achievementsRecyclerView.setLayoutManager(layoutManager);
        achievementsRecyclerView.setAdapter(achievementAdapter);


        String loggedInUserEmail = preferenceManager.getLoggedInUserEmail();

        if (loggedInUserEmail != null && !loggedInUserEmail.isEmpty()) {
            // Observe user data to populate fields
            userLiveData = viewModel.getUserByEmail(loggedInUserEmail);
            userLiveData.observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    currentUser = user;
                    populateUI(user);
                }
            });

            // Observe achievements data from the ViewModel
            viewModel.getAchievementsForUser(loggedInUserEmail).observe(getViewLifecycleOwner(), earnedAchievements -> {
                if (earnedAchievements != null) {
                    // This now calls the correct method for showing only earned badges
                    achievementAdapter.setDisplayModeEarnedOnly(earnedAchievements);
                }
            });
        }

        btnSave.setOnClickListener(v -> saveProfileChanges());
    }


    private void populateUI(User user) {
        // ... (this method remains the same)
        etName.setText(user.getName());
        etAge.setText(String.valueOf(user.getAge()));
        etGender.setText(user.getGender());
        etHeight.setText(String.valueOf(user.getHeight()));
        etWeight.setText(String.valueOf(user.getWeight()));
        switchVegetarian.setChecked(user.isVegetarian());
        switchVegan.setChecked(user.isVegan());
        switchGlutenFree.setChecked(user.isGlutenFree());
        switchDairyFree.setChecked(user.isDairyFree());
    }

    private void saveProfileChanges() {
        // ... (this method remains the same)
        if (currentUser == null) {
            Toast.makeText(getContext(), "Cannot save, user data not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }
        currentUser.setName(etName.getText().toString().trim());
        currentUser.setAge(Integer.parseInt(etAge.getText().toString().trim()));
        currentUser.setGender(etGender.getText().toString().trim());
        currentUser.setHeight(Double.parseDouble(etHeight.getText().toString().trim()));
        currentUser.setWeight(Double.parseDouble(etWeight.getText().toString().trim()));
        currentUser.setVegetarian(switchVegetarian.isChecked());
        currentUser.setVegan(switchVegan.isChecked());
        currentUser.setGlutenFree(switchGlutenFree.isChecked());
        currentUser.setDairyFree(switchDairyFree.isChecked());
        viewModel.updateUser(currentUser);
        Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }
}