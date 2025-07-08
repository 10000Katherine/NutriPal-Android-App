package com.nutripal.models;

public class NutritionAnalysis {
    private int totalCalories;
    private int proteinGrams;
    private int carbsGrams;
    private int fatGrams;
    private double fiberGrams;
    private double sodiumMilligrams;

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public int getProteinGrams() {
        return proteinGrams;
    }

    public void setProteinGrams(int proteinGrams) {
        this.proteinGrams = proteinGrams;
    }

    public int getCarbsGrams() {
        return carbsGrams;
    }

    public void setCarbsGrams(int carbsGrams) {
        this.carbsGrams = carbsGrams;
    }

    public int getFatGrams() {
        return fatGrams;
    }

    public void setFatGrams(int fatGrams) {
        this.fatGrams = fatGrams;
    }

    public double getFiberGrams() {
        return fiberGrams;
    }

    public void setFiberGrams(double fiberGrams) {
        this.fiberGrams = fiberGrams;
    }

    public double getSodiumMilligrams() {
        return sodiumMilligrams;
    }

    public void setSodiumMilligrams(double sodiumMilligrams) {
        this.sodiumMilligrams = sodiumMilligrams;
    }
}
