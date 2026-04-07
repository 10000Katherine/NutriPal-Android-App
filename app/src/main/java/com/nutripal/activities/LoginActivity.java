package com.nutripal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import com.nutripal.MainActivity;
import com.nutripal.R;
import com.nutripal.database.AppDatabase;
import com.nutripal.models.User;
import com.nutripal.utils.AppAppearanceManager;
import com.nutripal.utils.PreferenceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends BaseActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AppDatabase db;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(this);
        AppAppearanceManager.applyAppearance(this, preferenceManager);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = AppDatabase.getDatabase(getApplicationContext());


        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);


        btnLogin.setOnClickListener(v -> {
            handleLogin();
        });


        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }


        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            User user = db.userDao().findByUserCredentials(email, password);


            runOnUiThread(() -> {
                if (user != null) {
                    validateVerificationAndProceed(user, email, password);
                } else {
                  // if login failed
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void validateVerificationAndProceed(User user, String email, String password) {
        if (FirebaseApp.getApps(this).isEmpty()) {
            proceedToMain(user);
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                            auth.signOut();
                            proceedToMain(user);
                            return;
                        }

                        if (firebaseUser != null) {
                            firebaseUser.sendEmailVerification();
                        }
                        auth.signOut();
                        Toast.makeText(
                                this,
                                "Please verify your email first. Verification mail has been sent again.",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    String errorMessage = task.getException() != null
                            ? task.getException().getMessage()
                            : "Cloud verification failed.";

                    String normalizedError = errorMessage == null ? "" : errorMessage.toLowerCase();
                    String firebaseErrorCode = "";
                    if (task.getException() instanceof FirebaseAuthException) {
                        firebaseErrorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                    }

                    boolean shouldFallbackToLocalMode = normalizedError.contains("network")
                            || normalizedError.contains("internal")
                            || normalizedError.contains("temporar")
                            || normalizedError.contains("service")
                            || "ERROR_INTERNAL_ERROR".equals(firebaseErrorCode)
                            || "ERROR_NETWORK_REQUEST_FAILED".equals(firebaseErrorCode)
                            || "ERROR_TOO_MANY_REQUESTS".equals(firebaseErrorCode)
                            || "ERROR_USER_NOT_FOUND".equals(firebaseErrorCode)
                            || "ERROR_INVALID_CREDENTIAL".equals(firebaseErrorCode)
                            || "ERROR_OPERATION_NOT_ALLOWED".equals(firebaseErrorCode);

                    if (shouldFallbackToLocalMode) {
                        Toast.makeText(
                                this,
                                "Cloud verification unavailable. Logged in with local mode.",
                                Toast.LENGTH_LONG
                        ).show();
                        proceedToMain(user);
                        return;
                    }

                    Toast.makeText(this, "Login blocked: " + errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    private void proceedToMain(User user) {
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

        preferenceManager.setUserLoggedIn(true);
        preferenceManager.setLoggedInUserEmail(user.getEmail());

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
