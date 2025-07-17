package com.nutripal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nutripal.R;
import com.nutripal.adapters.FoodLogAdapter;
import com.nutripal.models.FoodLog;
import com.nutripal.models.LogItem;
import com.nutripal.models.MealHeader;
import com.nutripal.viewmodels.FoodLogViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class FoodLogFragment extends Fragment implements FoodLogAdapter.OnFoodLogItemLongClickListener {

    private FoodLogViewModel viewModel;
    private FoodLogAdapter adapter;
    private TextView currentDateTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(FoodLogViewModel.class);
        return inflater.inflate(R.layout.fragment_food_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentDateTextView = view.findViewById(R.id.textView_current_date);
        ImageButton prevDayButton = view.findViewById(R.id.button_previous_day);
        ImageButton nextDayButton = view.findViewById(R.id.button_next_day);
        RecyclerView recyclerView = view.findViewById(R.id.food_log_recycler_view);
        FloatingActionButton addFoodFab = view.findViewById(R.id.fab_add_food);

        adapter = new FoodLogAdapter();
        adapter.setOnFoodLogItemLongClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        prevDayButton.setOnClickListener(v -> viewModel.previousDay());
        nextDayButton.setOnClickListener(v -> viewModel.nextDay());
        addFoodFab.setOnClickListener(v -> {
            Calendar selectedDate = viewModel.getSelectedDate().getValue();
            if (selectedDate != null) {
                Bundle bundle = new Bundle();
                bundle.putLong("selected_date", selectedDate.getTimeInMillis());
                Navigation.findNavController(v).navigate(R.id.action_foodLogFragment_to_addFoodFragment, bundle);
            }
        });

        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            Calendar today = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            if (sdf.format(date.getTime()).equals(sdf.format(today.getTime()))) {
                currentDateTextView.setText("Today");
            } else {
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                currentDateTextView.setText(displayFormat.format(date.getTime()));
            }
        });

        viewModel.getLogsForSelectedDate().observe(getViewLifecycleOwner(), allFoodLogs -> {
            if (allFoodLogs != null) {
                adapter.setItems(groupLogs(allFoodLogs));
            }
        });

        // This observer for the badge notification is correct.
        viewModel.getNewAchievementUnlocked().observe(getViewLifecycleOwner(), achievementName -> {
            if (achievementName != null && !achievementName.isEmpty()) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Badge Unlocked!")
                        .setMessage("Congratulations! You've earned the '" + achievementName + "' badge.")
                        .setPositiveButton("Awesome!", (dialog, which) -> viewModel.onAchievementShown())
                        .setOnDismissListener(dialog -> viewModel.onAchievementShown())
                        .show();
            }
        });
    }

    @Override
    public void onFoodLogItemLongClicked(FoodLog foodLog) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete '" + foodLog.getFoodName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.delete(foodLog))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private List<LogItem> groupLogs(List<FoodLog> allFoodLogs) {
        Map<String, List<FoodLog>> groupedLogs = allFoodLogs.stream()
                .collect(Collectors.groupingBy(FoodLog::getMealType));
        List<LogItem> displayList = new ArrayList<>();
        processMealType("Breakfast", groupedLogs, displayList);
        processMealType("Lunch", groupedLogs, displayList);
        processMealType("Dinner", groupedLogs, displayList);
        return displayList;
    }

    private void processMealType(String mealType, Map<String, List<FoodLog>> groupedLogs, List<LogItem> displayList) {
        List<FoodLog> logsForMeal = groupedLogs.get(mealType);
        double totalCalories = 0.0;
        if (logsForMeal != null) {
            totalCalories = logsForMeal.stream().mapToDouble(FoodLog::getCalories).sum();
        }
        displayList.add(new MealHeader(mealType, totalCalories));
        if (logsForMeal != null && !logsForMeal.isEmpty()) {
            displayList.addAll(logsForMeal);
        }
    }
}