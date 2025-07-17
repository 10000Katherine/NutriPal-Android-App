package com.nutripal.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.nutripal.models.Achievement;
import com.nutripal.repositories.FoodLogRepository;
import java.util.List;

public class AchievementViewModel extends AndroidViewModel {

    private final FoodLogRepository repository;

    public AchievementViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogRepository(application);
    }

    /**
     * 获取所有可能获得的徽章的列表
     */
    public LiveData<List<Achievement>> getAllAchievements() {
        return repository.getAllAchievements();
    }

    /**
     * 获取指定用户已经获得的徽章ID的列表
     */
    public LiveData<List<String>> getEarnedAchievementIds(String userEmail) {
        return repository.getEarnedAchievementIdsForUser(userEmail);
    }
}