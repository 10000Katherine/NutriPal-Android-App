package com.nutripal.models;

public class MealHeader implements LogItem {
    private String mealType;
    private double totalCalories;

    public MealHeader(String mealType, double totalCalories) {
        this.mealType = mealType;
        this.totalCalories = totalCalories;
    }

    // Getters
    public String getMealType() { return mealType; }
    public double getTotalCalories() { return totalCalories; }
}