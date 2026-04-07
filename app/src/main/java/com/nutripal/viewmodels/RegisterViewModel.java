package com.nutripal.viewmodels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.nutripal.models.User;
import com.nutripal.repositories.FoodLogRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterViewModel extends AndroidViewModel {

    private final FoodLogRepository repository;
    private final ExecutorService executorService;
    private final MutableLiveData<RegistrationResult> registrationResult = new MutableLiveData<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
                int age = safeParseInt(ageStr);
                double height = safeParseDouble(heightStr);
                double weight = safeParseDouble(weightStr);

                User newUser = new User(email, password, name, gender, height, weight, age, "Sedentary");
                registerWithFirebaseAndLocal(newUser);
            }
        });
    }

    private void registerWithFirebaseAndLocal(User newUser) {
        mainHandler.post(() -> {
            if (FirebaseApp.getApps(getApplication()).isEmpty()) {
                insertLocalUserAndPostResult(
                        newUser,
                        "Registration successful (local mode).",
                        "Local registration failed. Please try again."
                );
                return;
            }

            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword())
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            String errorMessage = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Cloud registration failed.";
                            String normalizedError = errorMessage == null ? "" : errorMessage.toLowerCase();
                            String firebaseErrorCode = "";

                            if (task.getException() instanceof FirebaseAuthException) {
                                firebaseErrorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            }

                            boolean shouldFallbackToLocalMode = normalizedError.contains("configuration_not_found")
                                    || "ERROR_INTERNAL_ERROR".equals(firebaseErrorCode)
                                    || "ERROR_NETWORK_REQUEST_FAILED".equals(firebaseErrorCode)
                                    || "ERROR_TIMEOUT".equals(firebaseErrorCode);

                            if (shouldFallbackToLocalMode) {
                                insertLocalUserAndPostResult(
                                        newUser,
                                        "Cloud registration unavailable. Registered in local mode.",
                                        "Cloud registration unavailable, and local save failed. Please try again."
                                );
                                return;
                            }

                            registrationResult.postValue(new RegistrationResult(false, errorMessage));
                            return;
                        }

                        if (auth.getCurrentUser() == null) {
                            registrationResult.postValue(new RegistrationResult(false,
                                    "Registered but failed to access user session."));
                            return;
                        }

                        auth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(verificationTask -> {
                                    auth.signOut();

                                    if (verificationTask.isSuccessful()) {
                                        insertLocalUserAndPostResult(
                                                newUser,
                                                "Registration successful. Verification email sent.",
                                                "Registration partially failed while saving local data. Please try again."
                                        );
                                    } else {
                                        insertLocalUserAndPostResult(
                                                newUser,
                                                "Registration successful, but verification email failed. Please try again from login.",
                                                "Registration partially failed while saving local data. Please try again."
                                        );
                                    }
                                });
                    });
        });
    }

    private void insertLocalUserAndPostResult(User user, String successMessage, String failureMessage) {
        executorService.execute(() -> {
            try {
                repository.insertUser(user);
                registrationResult.postValue(new RegistrationResult(true, successMessage));
            } catch (Exception e) {
                registrationResult.postValue(new RegistrationResult(false, failureMessage));
            }
        });
    }

    private int safeParseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private double safeParseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
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
