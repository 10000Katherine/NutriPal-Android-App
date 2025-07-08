package com.nutripal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.nutripal.MainActivity;
import com.nutripal.R;
import com.nutripal.database.AppDatabase;
import com.nutripal.models.User;
import com.nutripal.utils.PreferenceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AppDatabase db;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = AppDatabase.getDatabase(getApplicationContext());
        preferenceManager = new PreferenceManager(this);


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

                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();


                    preferenceManager.setUserLoggedIn(true);
                    preferenceManager.setLoggedInUserEmail(user.getEmail());


                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                  // if login failed
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}