package com.nutripal.api;


import com.nutripal.api.models.FoodSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FoodApiService {

    @GET("cgi/search.pl")
    Call<FoodSearchResponse> searchFood(
                                         @Query("search_terms") String searchTerm,
                                         @Query("json") String jsonFlag
    );
}