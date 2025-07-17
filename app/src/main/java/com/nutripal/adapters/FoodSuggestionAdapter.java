package com.nutripal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nutripal.R;
import com.nutripal.models.FoodSuggestion;
import java.util.ArrayList;
import java.util.List;

public class FoodSuggestionAdapter extends RecyclerView.Adapter<FoodSuggestionAdapter.SuggestionViewHolder> {

    private List<FoodSuggestion> suggestions = new ArrayList<>();

    public void setSuggestions(List<FoodSuggestion> newSuggestions) {
        this.suggestions = newSuggestions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        FoodSuggestion suggestion = suggestions.get(position);
        holder.name.setText(suggestion.getName());
        holder.description.setText(suggestion.getDescription());
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.suggestion_food_name);
            description = itemView.findViewById(R.id.suggestion_food_description);
        }
    }
}