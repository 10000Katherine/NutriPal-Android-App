package com.nutripal.models;

public class FoodSuggestion {
    private final String name;
    private final String description;

    public FoodSuggestion(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}