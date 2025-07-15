package com.nutripal.api.models;

import com.google.gson.annotations.SerializedName;
import com.nutripal.models.Food;

// This class represents the JSON response for a barcode lookup
public class ProductResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("product")
    private Food product;

    // Getters
    public int getStatus() {
        return status;
    }

    public Food getProduct() {
        return product;
    }
}