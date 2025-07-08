package com.nutripal.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.nutripal.models.WaterLog;

@Dao
public interface WaterLogDao {

    @Insert
    void insert(WaterLog waterLog);

    @Query("SELECT SUM(amountMl) FROM water_log_table WHERE date BETWEEN :startOfDay AND :endOfDay")
    LiveData<Integer> getTotalWaterForDate(long startOfDay, long endOfDay);
}