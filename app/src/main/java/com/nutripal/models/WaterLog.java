package com.nutripal.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "water_log_table")
public class WaterLog {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int amountMl;
    private long date;

    // --- Add this new field ---
    private String userEmail;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAmountMl() { return amountMl; }
    public void setAmountMl(int amountMl) { this.amountMl = amountMl; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    // --- Add the getter and setter for the new field ---
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}