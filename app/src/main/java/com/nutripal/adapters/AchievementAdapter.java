package com.nutripal.adapters;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.BadgeViewHolder> {

    private List<Achievement> achievementList = new ArrayList<>();
    private Set<String> earnedAchievementIds = new HashSet<>();
    private boolean showOnlyEarned = false; // A flag to control display mode

    // Use this method when you want to show ALL badges (locked and unlocked)
    public void setDisplayModeAll(List<Achievement> allAchievements, List<String> earnedIds) {
        this.showOnlyEarned = false;
        this.achievementList = allAchievements;
        this.earnedAchievementIds.clear();
        this.earnedAchievementIds.addAll(earnedIds);
        notifyDataSetChanged();
    }

    // Use this method when you ONLY want to show earned badges
    public void setDisplayModeEarnedOnly(List<Achievement> earnedAchievements) {
        this.showOnlyEarned = true;
        this.achievementList = earnedAchievements;
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
        Achievement currentBadge = achievementList.get(position);
        holder.badgeName.setText(currentBadge.getName());
        holder.badgeIcon.setImageResource(currentBadge.getIconResId());

        if (showOnlyEarned) {
            // If we are only showing earned badges, they are always full color
            holder.badgeIcon.clearColorFilter();
            holder.itemView.setAlpha(1.0f);
        } else {
            // If showing all, check if the badge is earned to set its state
            if (earnedAchievementIds.contains(currentBadge.getId())) {
                // Unlocked state: full color
                holder.badgeIcon.clearColorFilter();
                holder.itemView.setAlpha(1.0f);
            } else {
                // Locked state: grayscale
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                holder.badgeIcon.setColorFilter(new ColorMatrixColorFilter(matrix));
                holder.itemView.setAlpha(0.6f);
            }
        }
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
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