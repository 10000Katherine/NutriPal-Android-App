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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nutripal.R;
import com.nutripal.adapters.FoodSuggestionAdapter;
import com.nutripal.viewmodels.HomeViewModel;
import com.nutripal.views.CircularProgressView;
import com.nutripal.views.MacroProgressView;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    // Existing Views
    private TextView totalCaloriesText, caloriesRemainingText;
    private CircularProgressView caloriesProgressView;
    private TextView proteinValueText, carbsValueText, fatValueText;
    private ProgressBar hydrationProgressBar;
    private TextView hydrationText;

    // --- New Views for Suggestions ---
    private CardView suggestionCard;
    private TextView suggestionTitleText;
    private RecyclerView suggestionRecyclerView;
    private FoodSuggestionAdapter suggestionAdapter;


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

        // Observer for main dashboard data
        viewModel.getHomeScreenData().observe(getViewLifecycleOwner(), homeScreenData -> {
            if (homeScreenData != null) {
                updateUI(homeScreenData);
            }
        });

        // --- New Observer for Suggestion Data ---
        viewModel.getSuggestionData().observe(getViewLifecycleOwner(), suggestionData -> {
            if (suggestionData != null && !suggestionData.suggestions.isEmpty()) {
                suggestionCard.setVisibility(View.VISIBLE);
                suggestionTitleText.setText(suggestionData.title);
                suggestionAdapter.setSuggestions(suggestionData.suggestions);
            } else {
                suggestionCard.setVisibility(View.GONE);
            }
        });
    }

    private void initViews(View view) {
        // ... (findViewById for all existing views)
        totalCaloriesText = view.findViewById(R.id.total_calories_text);
        caloriesRemainingText = view.findViewById(R.id.calories_remaining_text);
        caloriesProgressView = view.findViewById(R.id.calories_progress_view);
        proteinValueText = view.findViewById(R.id.protein_value_text);
        carbsValueText = view.findViewById(R.id.carbs_value_text);
        fatValueText = view.findViewById(R.id.fat_value_text);
        hydrationProgressBar = view.findViewById(R.id.hydration_progress_bar);
        hydrationText = view.findViewById(R.id.hydration_text);
        Button addWaterButton = view.findViewById(R.id.button_add_water_quick);
        addWaterButton.setOnClickListener(v -> {
            viewModel.addWater(250);
            Toast.makeText(getContext(), "Added 250ml of water!", Toast.LENGTH_SHORT).show();
        });

        // --- Initialize New Views ---
        suggestionCard = view.findViewById(R.id.suggestion_card);
        suggestionTitleText = view.findViewById(R.id.suggestion_title_text);
        suggestionRecyclerView = view.findViewById(R.id.suggestion_recycler_view);

        // --- Setup New RecyclerView ---
        suggestionAdapter = new FoodSuggestionAdapter();
        suggestionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        suggestionRecyclerView.setAdapter(suggestionAdapter);
    }

    private void updateUI(HomeViewModel.HomeScreenData data) {
        // ... (This method remains the same)
        int caloriesRemaining = data.calorieGoal - data.caloriesConsumed;
        totalCaloriesText.setText(String.valueOf(data.caloriesConsumed));
        caloriesRemainingText.setText(caloriesRemaining + " kcal remaining");
        if (data.calorieGoal > 0) {
            caloriesProgressView.setProgress((float) data.caloriesConsumed / data.calorieGoal);
        }
        proteinValueText.setText(String.format("%dg", data.proteinConsumed));
        carbsValueText.setText(String.format("%dg", data.carbsConsumed));
        fatValueText.setText(String.format("%dg", data.fatConsumed));
        int waterGoal = 2000; // This should ideally come from user profile
        hydrationText.setText(String.format("%d / %d ml", data.waterConsumed, waterGoal));
        hydrationProgressBar.setMax(waterGoal);
        hydrationProgressBar.setProgress(data.waterConsumed);
    }
}