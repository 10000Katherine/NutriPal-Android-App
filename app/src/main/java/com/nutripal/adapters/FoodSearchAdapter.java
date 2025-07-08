package com.nutripal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nutripal.R;
import com.nutripal.models.Food;
import java.util.ArrayList;
import java.util.List;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.FoodViewHolder> {

    public interface OnFoodItemClickListener {
        void onFoodItemClicked(Food food);
    }

    private List<Food> foodList = new ArrayList<>();
    private OnFoodItemClickListener clickListener;

    public void setOnFoodItemClickListener(OnFoodItemClickListener listener) {
        this.clickListener = listener;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImageView;
        TextView foodNameTextView;
        TextView foodCaloriesTextView;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImageView = itemView.findViewById(R.id.imageView_food);
            foodNameTextView = itemView.findViewById(R.id.textView_food_name);
            foodCaloriesTextView = itemView.findViewById(R.id.textView_food_calories);
        }
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_search_result, parent, false);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food currentFood = foodList.get(position);

        holder.foodNameTextView.setText(currentFood.getName()); //
        if (currentFood.getNutriments() != null) {
            String caloriesText = currentFood.getNutriments().getCaloriesPer100g() + " kcal per 100g";
            holder.foodCaloriesTextView.setText(caloriesText);
        } else {
            holder.foodCaloriesTextView.setText("No calorie data");
        }

        Glide.with(holder.itemView.getContext())
                .load(currentFood.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.foodImageView);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onFoodItemClicked(currentFood);
            }
        });
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
