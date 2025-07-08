package com.nutripal.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.nutripal.models.User;
import com.nutripal.repositories.FoodLogRepository;

public class ProfileViewModel extends AndroidViewModel {
    private FoodLogRepository repository;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogRepository(application);
    }

    public LiveData<User> getUserByEmail(String email) {
        return repository.getUserByEmail(email);
    }

    public void updateUser(User user) {
        repository.updateUser(user);
    }
}