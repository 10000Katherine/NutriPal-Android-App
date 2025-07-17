package com.nutripal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider; // 1. Import ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nutripal.R;
import com.nutripal.models.Food;
import com.nutripal.models.FoodLog; // 2. Import FoodLog
import com.nutripal.models.Nutriments;
import com.nutripal.viewmodels.FoodLogViewModel; // 3. Import FoodLogViewModel

public class AddFoodBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "AddFoodBottomSheet";
    private static final String ARG_FOOD = "arg_food";
    private static final String ARG_DATE = "arg_date";

    private Food food;
    private long selectedDateTimestamp;
    private FoodLogViewModel foodLogViewModel; // 4. Add a reference to the ViewModel

    public static AddFoodBottomSheet newInstance(Food food, long selectedDate) {
        // ... (This method remains the same)
        AddFoodBottomSheet fragment = new AddFoodBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD, food);
        args.putLong(ARG_DATE, selectedDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 5. Initialize the ViewModel, scoped to the Activity
        foodLogViewModel = new ViewModelProvider(requireActivity()).get(FoodLogViewModel.class);
        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable(ARG_FOOD);
            selectedDateTimestamp = getArguments().getLong(ARG_DATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ... (Finding views remains the same)
        TextView foodNameTextView = view.findViewById(R.id.textView_bottom_sheet_food_name);
        TextView caloriesTextView = view.findViewById(R.id.textView_bottom_sheet_calories);
        EditText quantityEditText = view.findViewById(R.id.editText_bottom_sheet_quantity);
        RadioGroup mealTypeRadioGroup = view.findViewById(R.id.radioGroup_bottom_sheet_meal_type);
        Button addButton = view.findViewById(R.id.button_bottom_sheet_add_to_log);
        foodNameTextView.setText(food.getName());
        Nutriments nutriments = food.getNutriments();
        if (nutriments != null) {
            caloriesTextView.setText(String.format("%.1f kcal per 100g", nutriments.getCaloriesPer100g()));
        }

        // --- 6. The main logic change is here ---
        addButton.setOnClickListener(v -> {
            String quantityStr = quantityEditText.getText().toString();
            if (quantityStr.isEmpty() || nutriments == null) {
                Toast.makeText(getContext(), "Please enter a quantity.", Toast.LENGTH_SHORT).show();
                return;
            }

            double quantity = Double.parseDouble(quantityStr);
            int selectedRadioButtonId = mealTypeRadioGroup.getCheckedRadioButtonId();
            String mealType = "Breakfast";
            if (selectedRadioButtonId == R.id.radioButton_bottom_sheet_lunch) mealType = "Lunch";
            else if (selectedRadioButtonId == R.id.radioButton_bottom_sheet_dinner) mealType = "Dinner";

            // Create the FoodLog object
            FoodLog newLog = new FoodLog();
            newLog.setFoodName(food.getName());
            newLog.setQuantity(quantity);
            newLog.setMealType(mealType);
            newLog.setDate(selectedDateTimestamp);
            double ratio = quantity / 100.0;
            newLog.setCalories(nutriments.getCaloriesPer100g() * ratio);
            newLog.setProtein(nutriments.getProteinPer100g() * ratio);
            newLog.setCarbs(nutriments.getCarbsPer100g() * ratio);
            newLog.setFat(nutriments.getFatPer100g() * ratio);
            newLog.setCaloriesPer100g(nutriments.getCaloriesPer100g());
            // Note: We don't set the userEmail here. The ViewModel will handle that.

            // Directly call the ViewModel's insert method
            foodLogViewModel.insert(newLog);

            // Close the bottom sheet
            dismiss();
        });
    }
}