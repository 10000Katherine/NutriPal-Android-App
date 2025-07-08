package com.nutripal.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SearchResponse {

    @SerializedName("products")
    private List<Food> products;


    public List<Food> getProducts() {
        return products;
    }
}