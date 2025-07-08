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
import com.nutripal.R;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        preferenceManager = new PreferenceManager(requireContext());
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.editText_profile_name);
        etAge = view.findViewById(R.id.editText_profile_age);
        etGender = view.findViewById(R.id.editText_profile_gender);
        etHeight = view.findViewById(R.id.editText_profile_height);
        etWeight = view.findViewById(R.id.editText_profile_weight);
        btnSave = view.findViewById(R.id.button_save_profile);

        String loggedInUserEmail = preferenceManager.getLoggedInUserEmail();

        if (loggedInUserEmail != null && !loggedInUserEmail.isEmpty()) {
            userLiveData = viewModel.getUserByEmail(loggedInUserEmail);
            userLiveData.observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    currentUser = user;
                    populateUI(user);
                }
            });
        }

        btnSave.setOnClickListener(v -> {
            saveProfileChanges();
        });
    }

    private void populateUI(User user) {
        etName.setText(user.getName());
        etAge.setText(String.valueOf(user.getAge()));
        etGender.setText(user.getGender());
        etHeight.setText(String.valueOf(user.getHeight()));
        etWeight.setText(String.valueOf(user.getWeight()));
    }

    private void saveProfileChanges() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Cannot save, user data not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setName(etName.getText().toString().trim());
        currentUser.setAge(Integer.parseInt(etAge.getText().toString().trim()));
        currentUser.setGender(etGender.getText().toString().trim());
        currentUser.setHeight(Double.parseDouble(etHeight.getText().toString().trim()));
        currentUser.setWeight(Double.parseDouble(etWeight.getText().toString().trim()));

        viewModel.updateUser(currentUser);

        Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
    }
}