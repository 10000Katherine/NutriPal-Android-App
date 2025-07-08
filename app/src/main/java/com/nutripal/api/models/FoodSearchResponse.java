package com.nutripal.api.models;

import com.google.gson.annotations.SerializedName;
import com.nutripal.models.Food;

import java.util.List;

public class FoodSearchResponse {

    @SerializedName("products")
    private List<Food> products;

    public List<Food> getProducts() {
        return products;
    }
}