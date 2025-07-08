package com.nutripal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nutripal.R;
import com.nutripal.models.FoodLog;
import com.nutripal.models.LogItem;
import com.nutripal.models.MealHeader;

import java.util.ArrayList;
import java.util.List;

public class FoodLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnFoodLogItemLongClickListener {
        void onFoodLogItemLongClicked(FoodLog foodLog);
    }

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<LogItem> items = new ArrayList<>();
    private OnFoodLogItemLongClickListener longClickListener; //

    public void setOnFoodLogItemLongClickListener(OnFoodLogItemLongClickListener listener) {
        this.longClickListener = listener;
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView mealTypeTextView;
        TextView totalCaloriesTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            mealTypeTextView = itemView.findViewById(R.id.textView_header_meal_type);
            totalCaloriesTextView = itemView.findViewById(R.id.textView_header_total_calories);
        }

        public void bind(MealHeader header) {
            mealTypeTextView.setText(header.getMealType());
            totalCaloriesTextView.setText(String.format("Total: %.0f kcal", header.getTotalCalories()));
        }
    }

    public static class FoodLogViewHolder extends RecyclerView.ViewHolder {
        TextView foodNameTextView;
        TextView caloriesTextView;

        public FoodLogViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.textView_log_food_name);
            caloriesTextView = itemView.findViewById(R.id.textView_log_calories);
        }

        public void bind(FoodLog foodLog) {
            foodNameTextView.setText(foodLog.getFoodName());
            caloriesTextView.setText(String.format("%.0f kcal", foodLog.getCalories()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof MealHeader) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_meal_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_food_log, parent, false);
            return new FoodLogViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            MealHeader header = (MealHeader) items.get(position);
            ((HeaderViewHolder) holder).bind(header);
        } else {
            FoodLog foodLog = (FoodLog) items.get(position);
            ((FoodLogViewHolder) holder).bind(foodLog);

            holder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onFoodLogItemLongClicked(foodLog);
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<LogItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
}