package com.nutripal.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.nutripal.models.Achievement;
import com.nutripal.models.User;
import com.nutripal.repositories.FoodLogRepository;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileViewModel extends AndroidViewModel {
    private FoodLogRepository repository;
    private final ExecutorService executorService; // Add an executor

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogRepository(application);
        executorService = Executors.newSingleThreadExecutor(); // Initialize it
    }

    public LiveData<User> getUserByEmail(String email) {
        return repository.getUserByEmail(email);
    }

    // Wrap the repository call in a background thread
    public void updateUser(User user) {
        executorService.execute(() -> repository.updateUser(user));
    }

    public LiveData<List<Achievement>> getAchievementsForUser(String userEmail) {
        LiveData<List<String>> earnedIdsLiveData = repository.getEarnedAchievementIdsForUser(userEmail);
        return Transformations.switchMap(earnedIdsLiveData, ids -> repository.getAchievementsByIds(ids));
    }
}