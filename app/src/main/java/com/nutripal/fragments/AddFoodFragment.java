package com.nutripal.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nutripal.R;
import com.nutripal.adapters.FoodSearchAdapter;
import com.nutripal.api.FoodApiService;
import com.nutripal.api.models.FoodSearchResponse;
import com.nutripal.models.Food;
import com.nutripal.models.FoodLog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddFoodFragment extends Fragment implements FoodSearchAdapter.OnFoodItemClickListener {

    private EditText searchEditText;
    private FoodApiService apiService;
    private RecyclerView recyclerView;
    private FoodSearchAdapter adapter;
    private long selectedDateTimestamp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDateTimestamp = getArguments().getLong("selected_date", System.currentTimeMillis());
        } else {
            selectedDateTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView_search_results);
        adapter = new FoodSearchAdapter();
        adapter.setOnFoodItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(FoodApiService.class);

        searchEditText = view.findViewById(R.id.editText_search_food);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                if (!query.trim().isEmpty()) {
                    performSearch(query);
                } else {
                    if (adapter != null) {
                        adapter.setFoods(new ArrayList<>());
                    }
                }
            }
        });
    }

    private void performSearch(String query) {
        apiService.searchFood(query, "1").enqueue(new Callback<FoodSearchResponse>() {
            @Override
            public void onResponse(Call<FoodSearchResponse> call, Response<FoodSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Food> products = response.body().getProducts();

                    if (products != null) {

                        Collections.sort(products, Comparator.comparingInt(p -> p.getName() != null ? p.getName().length() : 0));
                    }

                    adapter.setFoods(products);

                } else {
                    Log.e("API_ERROR", "Response not successful. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<FoodSearchResponse> call, Throwable t) {
                Log.e("API_FAILURE", "API call failed.", t);
            }
        });
    }

    @Override
    public void onFoodItemClicked(Food food) {
        if (getContext() == null) return;

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_food_quantity, null);
        final EditText quantityEditText = dialogView.findViewById(R.id.editText_quantity);
        final RadioGroup mealTypeRadioGroup = dialogView.findViewById(R.id.radioGroup_meal_type);

        new AlertDialog.Builder(getContext())
                .setTitle("Add " + food.getName())
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String quantityStr = quantityEditText.getText().toString();
                    if (!quantityStr.isEmpty() && food.getNutriments() != null) {
                        double quantity = Double.parseDouble(quantityStr);
                        int selectedRadioButtonId = mealTypeRadioGroup.getCheckedRadioButtonId();
                        String mealType = "Breakfast";
                        if (selectedRadioButtonId == R.id.radioButton_lunch) {
                            mealType = "Lunch";
                        } else if (selectedRadioButtonId == R.id.radioButton_dinner) {
                            mealType = "Dinner";
                        }

                        Bundle result = new Bundle();
                        result.putString("mealType", mealType);
                        result.putString("foodName", food.getName());
                        result.putDouble("quantity", quantity);
                        result.putLong("date", selectedDateTimestamp); // Pass the date back

                        double ratio = quantity / 100.0;
                        result.putDouble("calories", food.getNutriments().getCaloriesPer100g() * ratio);
                        result.putDouble("protein", food.getNutriments().getProteinPer100g() * ratio);
                        result.putDouble("carbs", food.getNutriments().getCarbsPer100g() * ratio);
                        result.putDouble("fat", food.getNutriments().getFatPer100g() * ratio);

                        getParentFragmentManager().setFragmentResult("add_food_request", result);
                        Navigation.findNavController(requireView()).popBackStack();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}