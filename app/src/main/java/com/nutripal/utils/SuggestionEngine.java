package com.nutripal.utils;

import com.nutripal.models.FoodSuggestion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuggestionEngine {

    public static List<FoodSuggestion> getProteinSuggestions() {
        List<FoodSuggestion> allProteinFoods = new ArrayList<>();
        allProteinFoods.add(new FoodSuggestion("Grilled Chicken", "≈ 31g Protein"));
        allProteinFoods.add(new FoodSuggestion("Greek Yogurt", "≈ 17g Protein"));
        allProteinFoods.add(new FoodSuggestion("Tofu", "≈ 8g Protein"));
        allProteinFoods.add(new FoodSuggestion("Salmon", "≈ 25g Protein"));
        allProteinFoods.add(new FoodSuggestion("Eggs", "≈ 6g Protein"));
        allProteinFoods.add(new FoodSuggestion("Lentils", "≈ 9g Protein"));

        Collections.shuffle(allProteinFoods);
        return allProteinFoods.subList(0, 3);
    }
}