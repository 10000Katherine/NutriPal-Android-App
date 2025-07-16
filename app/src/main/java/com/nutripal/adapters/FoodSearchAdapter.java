package com.nutripal.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.nutripal.R;
import com.nutripal.models.Food;
import com.nutripal.models.User;
import com.nutripal.utils.NutritionQualityScorer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.FoodViewHolder> {

    public interface OnFoodItemClickListener { void onFoodItemClicked(Food food); }
    private List<Food> foodList = new ArrayList<>();
    private OnFoodItemClickListener clickListener;
    private User userPreferences;
    public void setOnFoodItemClickListener(OnFoodItemClickListener listener) { this.clickListener = listener; }
    public void setUserPreferences(User user) { this.userPreferences = user; }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImageView, warningImageView;
        TextView foodNameTextView, foodCaloriesTextView, nutritionScoreTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImageView = itemView.findViewById(R.id.imageView_food);
            foodNameTextView = itemView.findViewById(R.id.textView_food_name);
            foodCaloriesTextView = itemView.findViewById(R.id.textView_food_calories);
            nutritionScoreTextView = itemView.findViewById(R.id.textView_nutrition_score);
            warningImageView = itemView.findViewById(R.id.imageView_warning);
        }
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_search_result, parent, false);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food currentFood = foodList.get(position);
        holder.foodNameTextView.setText(currentFood.getName());

        String violationMessage = getViolationMessage(currentFood);
        if (violationMessage != null) {
            holder.warningImageView.setVisibility(View.VISIBLE);
            holder.warningImageView.setContentDescription(violationMessage);
        } else {
            holder.warningImageView.setVisibility(View.GONE);
        }

        if (currentFood.getNutriments() != null && currentFood.getNutriments().getCaloriesPer100g() > 0) {
            double calories = currentFood.getNutriments().getCaloriesPer100g();
            holder.foodCaloriesTextView.setText(String.format(Locale.US, "%.1f kcal per 100g", calories));
            NutritionQualityScorer.Score score = NutritionQualityScorer.getScore(calories);
            holder.nutritionScoreTextView.setText(score.getGrade());
            GradientDrawable scoreBackground = (GradientDrawable) holder.nutritionScoreTextView.getBackground();
            scoreBackground.setColor(score.getParsedColor());
            holder.nutritionScoreTextView.setVisibility(View.VISIBLE);
        } else {
            holder.foodCaloriesTextView.setText("No calorie data");
            holder.nutritionScoreTextView.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext()).load(currentFood.getImageUrl()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.foodImageView);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onFoodItemClicked(currentFood);
            }
        });
    }

    // --- The final, most robust checking logic ---
    @Nullable
    private String getViolationMessage(Food food) {
        if (userPreferences == null) return null;

        // Combine all tags into one list for easy searching
        List<String> allTags = new ArrayList<>();
        allTags.addAll(food.getLabelsTags());
        allTags.addAll(food.getAllergensTags());

        // --- Vegetarian Check ---
        if (userPreferences.isVegetarian()) {
            for (String tag : allTags) {
                // Check if any tag contains keywords for non-vegetarian items
                if (tag.contains("non-vegetarian") || tag.contains("meat") || tag.contains("pork") || tag.contains("beef") || tag.contains("chicken") || tag.contains("fish")) {
                    return "Not Vegetarian";
                }
            }
        }

        // --- Vegan Check ---
        if (userPreferences.isVegan()) {
            for (String tag : allTags) {
                // Check for non-vegan keywords and common animal-product allergens
                if (tag.contains("non-vegan") || tag.contains("meat") || tag.contains("pork") || tag.contains("beef") || tag.contains("chicken") || tag.contains("fish") || tag.contains("dairy") || tag.contains("milk") || tag.contains("egg") || tag.contains("honey")) {
                    return "Not Vegan";
                }
            }
        }

        // --- Allergen Checks ---
        if (userPreferences.isGlutenFree()) {
            for (String tag : allTags) {
                if (tag.contains("gluten")) return "Contains Gluten";
            }
        }
        if (userPreferences.isDairyFree()) {
            for (String tag : allTags) {
                if (tag.contains("dairy") || tag.contains("milk")) return "Contains Dairy";
            }
        }

        return null; // No violations found
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void setFoods(List<Food> newFoodList) {
        this.foodList = newFoodList;
        notifyDataSetChanged();
    }
}