package com.nutripal.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_log_table")
public class FoodLog implements LogItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String foodName;
    private double calories; // 实际摄入热量
    private double protein;
    private double carbs;
    private double fat;
    private double quantity;
    private String mealType;
    private long date;

    // --- ↓↓↓ 在这里添加新字段 ↓↓↓ ---
    private double caloriesPer100g; // 食物本身每100g的标准热量

    public FoodLog() {}

    // --- Getters and Setters for all fields ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    // --- ↓↓↓ 在这里添加新字段的 Getter 和 Setter ↓↓↓ ---

    public double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(double caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }
}