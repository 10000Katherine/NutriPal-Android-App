package com.nutripal.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openNearbyHealthyFoodInMap();
                } else {
                    Toast.makeText(getContext(), "Location permission denied. Opening generic nearby search.", Toast.LENGTH_SHORT).show();
                    openMapWithQuery("geo:0,0?q=healthy+restaurant+near+me");
                }
            });


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

        Button findNearbyButton = view.findViewById(R.id.button_find_healthy_nearby);
        findNearbyButton.setOnClickListener(v -> openNearbyHealthyFoodInMap());

        // --- Initialize New Views ---
        suggestionCard = view.findViewById(R.id.suggestion_card);
        suggestionTitleText = view.findViewById(R.id.suggestion_title_text);
        suggestionRecyclerView = view.findViewById(R.id.suggestion_recycler_view);

        // --- Setup New RecyclerView ---
        suggestionAdapter = new FoodSuggestionAdapter();
        suggestionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        suggestionRecyclerView.setAdapter(suggestionAdapter);
    }

    private void openNearbyHealthyFoodInMap() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        String queryUri = buildNearbyQueryUri();
        openMapWithQuery(queryUri);
    }

    private String buildNearbyQueryUri() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return "geo:0,0?q=healthy+restaurant+near+me";
        }

        try {
            Location bestLocation = null;
            for (String provider : locationManager.getProviders(true)) {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }
                if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            }

            if (bestLocation != null) {
                return "geo:" + bestLocation.getLatitude() + "," + bestLocation.getLongitude() + "?q=healthy+restaurant";
            }
        } catch (SecurityException ignored) {
            return "geo:0,0?q=healthy+restaurant+near+me";
        }

        return "geo:0,0?q=healthy+restaurant+near+me";
    }

    private void openMapWithQuery(String queryUri) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(queryUri));
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
            return;
        } catch (ActivityNotFoundException | SecurityException ignored) {
        }

        Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(queryUri));
        try {
            startActivity(fallbackIntent);
            return;
        } catch (ActivityNotFoundException | SecurityException ignored) {
        }

        String webQuery = "healthy restaurant near me";
        int queryIndex = queryUri.indexOf("q=");
        if (queryIndex >= 0 && queryIndex + 2 < queryUri.length()) {
            webQuery = queryUri.substring(queryIndex + 2).replace('+', ' ');
        }

        String webUrl = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(webQuery);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        try {
            startActivity(webIntent);
            return;
        } catch (ActivityNotFoundException | SecurityException ignored) {
        }

        Toast.makeText(getContext(), "No map application available.", Toast.LENGTH_SHORT).show();
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
