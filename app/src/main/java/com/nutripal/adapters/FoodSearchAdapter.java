package com.nutripal.adapters;

import android.graphics.drawable.GradientDrawable;
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
import com.nutripal.utils.NutritionQualityScorer; // 导入我们的评分工具

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

    // --- ViewHolder 修改 ---
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImageView;
        TextView foodNameTextView;
        TextView foodCaloriesTextView;
        TextView nutritionScoreTextView; // 新增：评分控件的引用

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImageView = itemView.findViewById(R.id.imageView_food);
            foodNameTextView = itemView.findViewById(R.id.textView_food_name);
            foodCaloriesTextView = itemView.findViewById(R.id.textView_food_calories);
            nutritionScoreTextView = itemView.findViewById(R.id.textView_nutrition_score); // 新增：关联ID
        }
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_search_result, parent, false);
        return new FoodViewHolder(itemView);
    }

    // --- onBindViewHolder 修改 ---
    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food currentFood = foodList.get(position);

        holder.foodNameTextView.setText(currentFood.getName());

        if (currentFood.getNutriments() != null && currentFood.getNutriments().getCaloriesPer100g() > 0) {
            double calories = currentFood.getNutriments().getCaloriesPer100g();
            String caloriesText = String.format("%.1f kcal per 100g", calories);
            holder.foodCaloriesTextView.setText(caloriesText);

            // --- 新增：计算并显示营养评分 ---
            NutritionQualityScorer.Score score = NutritionQualityScorer.getScore(calories);
            holder.nutritionScoreTextView.setText(score.getGrade());

            // 动态修改背景颜色
            GradientDrawable scoreBackground = (GradientDrawable) holder.nutritionScoreTextView.getBackground();
            scoreBackground.setColor(score.getParsedColor());
            holder.nutritionScoreTextView.setVisibility(View.VISIBLE);
            // --- 新增代码结束 ---

        } else {
            holder.foodCaloriesTextView.setText("No calorie data");
            holder.nutritionScoreTextView.setVisibility(View.GONE); // 新增：如果没有数据则隐藏评分
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