package com.nutripal.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.nutripal.models.User;
import com.nutripal.repositories.FoodLogRepository;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterViewModel extends AndroidViewModel {

    private final FoodLogRepository repository;
    private final ExecutorService executorService;
    private final MutableLiveData<RegistrationResult> registrationResult = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodLogRepository(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<RegistrationResult> getRegistrationResult() {
        return registrationResult;
    }

    public void registerUser(String email, String password, String name, String gender, String heightStr, String weightStr, String ageStr) {
        executorService.execute(() -> {
            User existingUser = repository.findUserByEmailOnce(email);

            if (existingUser != null) {
                registrationResult.postValue(new RegistrationResult(false, "This email is already registered."));
            } else {
                int age = ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr);
                double height = heightStr.isEmpty() ? 0.0 : Double.parseDouble(heightStr);
                double weight = weightStr.isEmpty() ? 0.0 : Double.parseDouble(weightStr);

                User newUser = new User(email, password, name, gender, height, weight, age, "Sedentary");
                repository.insertUser(newUser); // We will create this method in the repository
                registrationResult.postValue(new RegistrationResult(true, "Registration successful!"));
            }
        });
    }

    // Inner class to hold the result of the registration attempt
    public static class RegistrationResult {
        public final boolean isSuccess;
        public final String message;

        RegistrationResult(boolean isSuccess, String message) {
            this.isSuccess = isSuccess;
            this.message = message;
        }
    }
}