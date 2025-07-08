package com.nutripal.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nutripal.R;

public class FoodLogActivity extends AppCompatActivity {

    private TextView tvTotalCalories;
    private Button btnAddFood;
    private EditText etFoodSearch;
    private LinearLayout mealBreakfast, mealLunch, mealDinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_log); // Make sure the layout file exists

        // Initialize views
        tvTotalCalories = findViewById(R.id.tv_calories_total);
        btnAddFood = findViewById(R.id.btn_add_food);
        etFoodSearch = findViewById(R.id.et_food_search);
        mealBreakfast = findViewById(R.id.meal_breakfast);
        mealLunch = findViewById(R.id.meal_lunch);
        mealDinner = findViewById(R.id.meal_dinner);

        // Setup button click listeners
        btnAddFood.setOnClickListener(v -> {
            String foodName = etFoodSearch.getText().toString().trim();
            if (!foodName.isEmpty()) {
                Toast.makeText(FoodLogActivity.this, "Searching for " + foodName, Toast.LENGTH_SHORT).show();
                // Add food to meal
                addFoodToMeal(foodName, "Custom Meal");
            } else {
                Toast.makeText(FoodLogActivity.this, "Please enter food name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addFoodToMeal(String foodName, String mealType) {
        TextView mealTextView = null;

        // Add the food to the correct meal section (Breakfast, Lunch, or Dinner)
        if (mealType.equals("Breakfast")) {
            mealTextView = new TextView(this);
            mealTextView.setText(foodName);
            mealBreakfast.addView(mealTextView);
        } else if (mealType.equals("Lunch")) {
            mealTextView = new TextView(this);
            mealTextView.setText(foodName);
            mealLunch.addView(mealTextView);
        } else if (mealType.equals("Dinner")) {
            mealTextView = new TextView(this);
            mealTextView.setText(foodName);
            mealDinner.addView(mealTextView);
        } else {
            // Default to Breakfast if Custom Meal is selected
            mealTextView = new TextView(this);
            mealTextView.setText(foodName);
            mealBreakfast.addView(mealTextView);
        }

        updateTotalCalories();
    }

    private void updateTotalCalories() {

        int totalCalories = 1420;
        tvTotalCalories.setText(totalCalories + " / 2000 kcal");
    }
}
