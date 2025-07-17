package com.nutripal.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.nutripal.models.Achievement;
import com.nutripal.models.UserAchievement;
import java.util.List;

@Dao
public interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAchievement(Achievement achievement);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUserAchievement(UserAchievement userAchievement);

    @Query("SELECT achievementId FROM user_achievement_table WHERE userEmail = :userEmail")
    LiveData<List<String>> getEarnedAchievementIdsForUser(String userEmail);

    @Query("SELECT COUNT(*) FROM user_achievement_table WHERE userEmail = :userEmail AND achievementId = :achievementId")
    int hasUserEarnedAchievement(String userEmail, String achievementId);

    @Query("SELECT * FROM achievement_table WHERE id IN (:ids)")
    LiveData<List<Achievement>> getAchievementsByIds(List<String> ids);

    // --- ↓↓↓ 添加这个新方法 ↓↓↓ ---
    @Query("SELECT * FROM achievement_table ORDER BY name ASC")
    LiveData<List<Achievement>> getAllAchievements();
}