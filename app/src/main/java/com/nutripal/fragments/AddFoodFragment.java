package com.nutripal.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.nutripal.R;
import com.nutripal.adapters.FoodSearchAdapter;
import com.nutripal.api.FoodApiService;
import com.nutripal.api.models.FoodSearchResponse;
import com.nutripal.api.models.ProductResponse;
import com.nutripal.models.Food;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFoodFragment extends Fragment implements FoodSearchAdapter.OnFoodItemClickListener {

    private EditText searchEditText;
    private FoodApiService apiService;
    private RecyclerView recyclerView;
    private FoodSearchAdapter adapter;
    private long selectedDateTimestamp;

    // Launcher for the camera permission request
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchScanner(); // Permission granted, launch scanner
                } else {
                    Toast.makeText(getContext(), "Camera permission is required to scan barcodes", Toast.LENGTH_SHORT).show();
                }
            });

    // Launcher for the barcode scanner activity
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String barcode = result.getContents();
                    fetchFoodByBarcode(barcode);
                }
            });

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Setup Views ---
        recyclerView = view.findViewById(R.id.recyclerView_search_results);
        searchEditText = view.findViewById(R.id.editText_search_food);
        ImageButton scanButton = view.findViewById(R.id.button_scan_barcode);

        // --- Setup Adapter ---
        adapter = new FoodSearchAdapter();
        adapter.setOnFoodItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // --- Setup Retrofit/API Service ---
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(FoodApiService.class);

        // --- Setup Listeners ---
        scanButton.setOnClickListener(v -> checkCameraPermissionAndLaunchScanner());

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

    // --- Barcode Scanner Logic ---
    private void checkCameraPermissionAndLaunchScanner() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchScanner();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
        options.setPrompt("Scan a barcode");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);

        // --- Add this line to unlock the screen orientation ---
        options.setOrientationLocked(false);

        barcodeLauncher.launch(options);
    }

    private void fetchFoodByBarcode(String barcode) {
        Toast.makeText(getContext(), "Searching for barcode...", Toast.LENGTH_SHORT).show();
        apiService.getFoodByBarcode(barcode).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 1 && response.body().getProduct() != null) {
                        Food foundFood = response.body().getProduct();
                        // Use the new BottomSheet to show the result
                        AddFoodBottomSheet bottomSheet = AddFoodBottomSheet.newInstance(foundFood, selectedDateTimestamp);
                        bottomSheet.show(getParentFragmentManager(), AddFoodBottomSheet.TAG);
                    } else {
                        showNotFoundDialog();
                    }
                } else {
                    showNotFoundDialog();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNotFoundDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Product Not Found")
                .setMessage("Sorry, this barcode was not found in our database. You can try searching for it manually.")
                .setPositiveButton("Manual Search", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // --- Text Search Logic ---
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
                }
            }
            @Override
            public void onFailure(Call<FoodSearchResponse> call, Throwable t) {
                Log.e("API_FAILURE", "API call failed.", t);
            }
        });
    }

    // --- On Click from Search Results ---
    @Override
    public void onFoodItemClicked(Food food) {
        if (food.getNutriments() == null) {
            Toast.makeText(getContext(), "No nutritional data available for this item.", Toast.LENGTH_SHORT).show();
            return;
        }
        // When clicking from a search result, we also use the BottomSheet
        AddFoodBottomSheet bottomSheet = AddFoodBottomSheet.newInstance(food, selectedDateTimestamp);
        bottomSheet.show(getParentFragmentManager(), AddFoodBottomSheet.TAG);
    }
}