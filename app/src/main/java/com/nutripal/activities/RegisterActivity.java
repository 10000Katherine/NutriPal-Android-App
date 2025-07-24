package com.nutripal.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Import this
import com.nutripal.R;
import com.nutripal.viewmodels.RegisterViewModel; // Import the new ViewModel

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etName, etAge, etGender, etHeight, etWeight;
    private Button btnRegister;
    private RegisterViewModel viewModel; // Use ViewModel instead of database directly

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        etEmail = findViewById(R.id.editText_register_email);
        etPassword = findViewById(R.id.editText_register_password);
        etName = findViewById(R.id.editText_register_name);
        etAge = findViewById(R.id.editText_register_age);
        etGender = findViewById(R.id.editText_register_gender);
        etHeight = findViewById(R.id.editText_register_height);
        etWeight = findViewById(R.id.editText_register_weight);
        btnRegister = findViewById(R.id.button_register);

        btnRegister.setOnClickListener(v -> handleRegistration());

        // Observe the result from the ViewModel
        viewModel.getRegistrationResult().observe(this, result -> {
            if (result != null) {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                if (result.isSuccess) {
                    finish(); // Go back to Login screen if successful
                }
            }
        });
    }

    private void handleRegistration() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Email, password and name are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            return;
        }

        // The Activity's only job is to pass the data to the ViewModel
        viewModel.registerUser(email, password, name, gender, heightStr, weightStr, ageStr);
    }
}