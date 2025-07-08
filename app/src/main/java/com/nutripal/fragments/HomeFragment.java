package com.nutripal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.nutripal.R;
import com.nutripal.viewmodels.HomeViewModel;
import com.nutripal.views.CircularProgressView;
import com.nutripal.views.MacroProgressView;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    private TextView totalCaloriesText, caloriesRemainingText;
    private CircularProgressView caloriesProgressView;
    private MacroProgressView proteinProgressView, carbsProgressView, fatProgressView;
    private TextView proteinValueText, carbsValueText, fatValueText;
    private ProgressBar hydrationProgressBar;
    private TextView hydrationText;
    private Button addWaterButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);


        viewModel.getHomeScreenData().observe(getViewLifecycleOwner(), homeScreenData -> {
            if (homeScreenData != null) {
                updateUI(homeScreenData);
            }
        });
    }

    private void initViews(View view) {
        totalCaloriesText = view.findViewById(R.id.total_calories_text);
        caloriesRemainingText = view.findViewById(R.id.calories_remaining_text);
        caloriesProgressView = view.findViewById(R.id.calories_progress_view);
        proteinProgressView = view.findViewById(R.id.protein_progress_view);
        carbsProgressView = view.findViewById(R.id.carbs_progress_view);
        fatProgressView = view.findViewById(R.id.fat_progress_view);
        proteinValueText = view.findViewById(R.id.protein_value_text);
        carbsValueText = view.findViewById(R.id.carbs_value_text);
        fatValueText = view.findViewById(R.id.fat_value_text);
        hydrationProgressBar = view.findViewById(R.id.hydration_progress_bar);
        hydrationText = view.findViewById(R.id.hydration_text);
        addWaterButton = view.findViewById(R.id.button_add_water_quick);

        addWaterButton.setOnClickListener(v -> {
            viewModel.addWater(250);
            Toast.makeText(getContext(), "Added 250ml of water!", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUI(HomeViewModel.HomeScreenData data) {
        int caloriesRemaining = data.calorieGoal - data.caloriesConsumed;

        totalCaloriesText.setText(String.valueOf(data.caloriesConsumed));
        caloriesRemainingText.setText(caloriesRemaining + " kcal remaining");

        float progress = 0;
        if (data.calorieGoal > 0) {
            progress = (float) data.caloriesConsumed / data.calorieGoal;
        }
        caloriesProgressView.setProgress(progress);

        proteinValueText.setText(String.format("%dg", data.proteinConsumed));
        carbsValueText.setText(String.format("%dg", data.carbsConsumed));
        fatValueText.setText(String.format("%dg", data.fatConsumed));


        proteinProgressView.setProgress(0.7f);
        carbsProgressView.setProgress(0.45f);
        fatProgressView.setProgress(0.6f);

        int waterGoal = 2000;
        hydrationText.setText(String.format("%d / %d ml", data.waterConsumed, waterGoal));
        hydrationProgressBar.setMax(waterGoal);
        hydrationProgressBar.setProgress(data.waterConsumed);
    }
}