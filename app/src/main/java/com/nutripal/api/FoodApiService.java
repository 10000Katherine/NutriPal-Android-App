package com.nutripal.api;

import com.nutripal.api.models.FoodSearchResponse;
import com.nutripal.api.models.ProductResponse; // <-- Import the new model

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path; // <-- Import @Path
import retrofit2.http.Query;

public interface FoodApiService {

    @GET("cgi/search.pl?search_simple=1&json=1&action=process")
    Call<FoodSearchResponse> searchFood(@Query("search_terms") String query, @Query("page") String page);

    // --- Add this new method ---
    @GET("api/v0/product/{barcode}.json")
    Call<ProductResponse> getFoodByBarcode(@Path("barcode") String barcode);

}