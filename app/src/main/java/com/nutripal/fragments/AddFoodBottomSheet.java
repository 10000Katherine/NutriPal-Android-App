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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nutripal.R;
import com.nutripal.models.Food;
import com.nutripal.models.Nutriments;

public class AddFoodBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "AddFoodBottomSheet";
    private static final String ARG_FOOD = "arg_food";
    private static final String ARG_DATE = "arg_date";

    private Food food;
    private long selectedDateTimestamp;

    // Factory method to create a new instance of this fragment
    public static AddFoodBottomSheet newInstance(Food food, long selectedDate) {
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
        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable(ARG_FOOD);
            selectedDateTimestamp = getArguments().getLong(ARG_DATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottom_sheet_add_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find all the views in the layout
        TextView foodNameTextView = view.findViewById(R.id.textView_bottom_sheet_food_name);
        TextView caloriesTextView = view.findViewById(R.id.textView_bottom_sheet_calories);
        EditText quantityEditText = view.findViewById(R.id.editText_bottom_sheet_quantity);
        RadioGroup mealTypeRadioGroup = view.findViewById(R.id.radioGroup_bottom_sheet_meal_type);
        Button addButton = view.findViewById(R.id.button_bottom_sheet_add_to_log);

        // Populate the views with food data
        foodNameTextView.setText(food.getName());
        Nutriments nutriments = food.getNutriments();
        if (nutriments != null) {
            caloriesTextView.setText(String.format("%.1f kcal per 100g", nutriments.getCaloriesPer100g()));
        }

        // Set the click listener for the "Add to Log" button
        addButton.setOnClickListener(v -> {
            String quantityStr = quantityEditText.getText().toString();
            if (quantityStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nutriments == null) {
                Toast.makeText(getContext(), "Cannot add food without nutritional data.", Toast.LENGTH_SHORT).show();
                return;
            }

            double quantity = Double.parseDouble(quantityStr);
            int selectedRadioButtonId = mealTypeRadioGroup.getCheckedRadioButtonId();
            String mealType = "Breakfast";
            if (selectedRadioButtonId == R.id.radioButton_bottom_sheet_lunch) {
                mealType = "Lunch";
            } else if (selectedRadioButtonId == R.id.radioButton_bottom_sheet_dinner) {
                mealType = "Dinner";
            }

            // Create a bundle with all the data to send back to FoodLogFragment
            Bundle result = new Bundle();
            result.putString("mealType", mealType);
            result.putString("foodName", food.getName());
            result.putDouble("quantity", quantity);
            result.putLong("date", selectedDateTimestamp);

            double ratio = quantity / 100.0;
            result.putDouble("calories", nutriments.getCaloriesPer100g() * ratio);
            result.putDouble("protein", nutriments.getProteinPer100g() * ratio);
            result.putDouble("carbs", nutriments.getCarbsPer100g() * ratio);
            result.putDouble("fat", nutriments.getFatPer100g() * ratio);
            result.putDouble("caloriesPer100g", nutriments.getCaloriesPer100g());

            // Use FragmentResult to send the data back
            getParentFragmentManager().setFragmentResult("add_food_request", result);

            // Close the bottom sheet
            dismiss();
        });
    }
}