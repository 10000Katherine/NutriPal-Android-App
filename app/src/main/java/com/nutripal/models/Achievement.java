package com.nutripal.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "achievement_table")
public class Achievement {

    @PrimaryKey
    @NonNull
    private String id; // A unique ID like "FIRST_LOG" or "7_DAY_STREAK"

    private String name; // "First Log"
    private String description; // "Log your first food item."
    private int iconResId; // The drawable resource ID for the badge icon

    // Constructor, Getters, and Setters
    public Achievement(@NonNull String id, String name, String description, int iconResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconResId = iconResId;
    }

    @NonNull
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getIconResId() { return iconResId; }
}