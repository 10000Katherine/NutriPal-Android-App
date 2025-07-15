package com.nutripal.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Food implements Serializable {

    @SerializedName("product_name")
    private String name;

    @SerializedName("brands")
    private String brand;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("code")
    private String barcode;

    @SerializedName("nutriments")
    private Nutriments nutriments;


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public Nutriments getNutriments() { return nutriments; }
    public void setNutriments(Nutriments nutriments) { this.nutriments = nutriments; }
}