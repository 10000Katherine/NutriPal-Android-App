package com.nutripal.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nutripal.models.FoodLog;
import java.util.List;

@Dao
public interface FoodLogDao {
    @Insert
    void insert(FoodLog foodLog);

    @Delete
    void delete(FoodLog foodLog);

    @Update
    void update(FoodLog foodLog);

    @Query("SELECT * FROM food_log_table WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY date DESC")
    LiveData<List<FoodLog>> getLogsForDate(long startOfDay, long endOfDay);


    @Query("SELECT * FROM food_log_table ORDER BY date ASC")
    LiveData<List<FoodLog>> getAllFoodLogs();

    @Query("SELECT * FROM food_log_table WHERE date >= :startDate ORDER BY date ASC")
    LiveData<List<FoodLog>> getLogsSince(long startDate);

}