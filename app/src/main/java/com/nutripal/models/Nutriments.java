package com.nutripal.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Nutriments implements Serializable{

    @SerializedName("energy-kcal_100g")
    private double caloriesPer100g;

    @SerializedName("proteins_100g")
    private double proteinPer100g;

    @SerializedName("carbohydrates_100g")
    private double carbsPer100g;

    @SerializedName("fat_100g")
    private double fatPer100g;

    public double getCaloriesPer100g() { return caloriesPer100g; }
    public double getProteinPer100g() { return proteinPer100g; }
    public double getCarbsPer100g() { return carbsPer100g; }
    public double getFatPer100g() { return fatPer100g; }
}