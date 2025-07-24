package com.nutripal.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "height")
    private double height;

    @ColumnInfo(name = "weight")
    private double weight;

    @ColumnInfo(name = "age")
    private int age;

    @ColumnInfo(name = "activity_level")
    private String activityLevel;

    @ColumnInfo(name = "vegetarian")
    private boolean vegetarian;

    @ColumnInfo(name = "vegan")
    private boolean vegan;

    @ColumnInfo(name = "gluten_free")
    private boolean glutenFree;

    @ColumnInfo(name = "dairy_free")
    private boolean dairyFree;

    @ColumnInfo(name = "water_goal")
    private int waterGoal;

    // Empty constructor required by Room
    public User() {
        this.email = ""; // Initialize non-null primary key
    }

    // Full constructor for easier object creation
    @Ignore
    public User(@NonNull String email, String password, String name, String gender, double height, double weight, int age, String activityLevel) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.activityLevel = activityLevel;
        // Default preferences to false
        this.vegetarian = false;
        this.vegan = false;
        this.glutenFree = false;
        this.dairyFree = false;
        this.waterGoal = 2000; // Default water goal
    }

    // --- All Getters and Setters ---
    @NonNull
    public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    public boolean isVegetarian() { return vegetarian; }
    public void setVegetarian(boolean vegetarian) { this.vegetarian = vegetarian; }
    public boolean isVegan() { return vegan; }
    public void setVegan(boolean vegan) { this.vegan = vegan; }
    public boolean isGlutenFree() { return glutenFree; }
    public void setGlutenFree(boolean glutenFree) { this.glutenFree = glutenFree; }
    public boolean isDairyFree() { return dairyFree; }
    public void setDairyFree(boolean dairyFree) { this.dairyFree = dairyFree; }
    public int getWaterGoal() { return waterGoal; }
    public void setWaterGoal(int waterGoal) { this.waterGoal = waterGoal; }
}