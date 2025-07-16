package com.nutripal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nutripal.R;
import com.nutripal.models.Achievement;
import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.BadgeViewHolder> {

    private List<Achievement> achievements = new ArrayList<>();

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BadgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement_badge, parent, false);
        return new BadgeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BadgeViewHolder holder, int position) {
        Achievement currentBadge = achievements.get(position);
        holder.badgeName.setText(currentBadge.getName());
        holder.badgeIcon.setImageResource(currentBadge.getIconResId());
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    static class BadgeViewHolder extends RecyclerView.ViewHolder {
        ImageView badgeIcon;
        TextView badgeName;

        public BadgeViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeIcon = itemView.findViewById(R.id.imageView_badge_icon);
            badgeName = itemView.findViewById(R.id.textView_badge_name);
        }
    }
}