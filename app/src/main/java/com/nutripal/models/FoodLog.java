package com.nutripal.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_log_table")
public class FoodLog implements LogItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String foodName;
    private double calories;
    private double protein; // <-- 添加这个字段
    private double carbs;   // <-- 添加这个字段
    private double fat;     // <-- 添加这个字段
    private double quantity;
    private String mealType;
    private long date;

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

    // ↓↓↓ 添加这些缺失的 Getters 和 Setters ↓↓↓

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

    // --- 已有的其他 Getters and Setters ---

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
}