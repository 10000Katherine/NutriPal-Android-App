package com.nutripal.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_achievement_table",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "email",
                        childColumns = "userEmail",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Achievement.class,
                        parentColumns = "id",
                        childColumns = "achievementId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = {"userEmail", "achievementId"}, unique = true)})
public class UserAchievement {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userEmail;
    private String achievementId;
    private long dateEarned;

    // Constructor, Getters, and Setters
    public UserAchievement(String userEmail, String achievementId, long dateEarned) {
        this.userEmail = userEmail;
        this.achievementId = achievementId;
        this.dateEarned = dateEarned;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserEmail() { return userEmail; }
    public String getAchievementId() { return achievementId; }
    public long getDateEarned() { return dateEarned; }
}