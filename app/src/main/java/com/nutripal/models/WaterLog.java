package com.nutripal.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "water_log_table")
public class WaterLog {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int amountMl; // Amount in milliliters
    private long date;   // Timestamp

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAmountMl() { return amountMl; }
    public void setAmountMl(int amountMl) { this.amountMl = amountMl; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
}