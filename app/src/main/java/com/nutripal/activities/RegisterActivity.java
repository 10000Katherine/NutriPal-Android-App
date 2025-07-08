package com.nutripal.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nutripal.R;
import com.nutripal.database.AppDatabase;
import com.nutripal.models.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {


    private EditText etEmail, etPassword, etName, etAge, etGender, etHeight, etWeight;
    private Button btnRegister;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = AppDatabase.getDatabase(getApplicationContext());

        etEmail = findViewById(R.id.editText_register_email);
        etPassword = findViewById(R.id.editText_register_password);
        etName = findViewById(R.id.editText_register_name);
        etAge = findViewById(R.id.editText_register_age); //
        etGender = findViewById(R.id.editText_register_gender);
        etHeight = findViewById(R.id.editText_register_height);
        etWeight = findViewById(R.id.editText_register_weight);
        btnRegister = findViewById(R.id.button_register);

        btnRegister.setOnClickListener(v -> {
            handleRegistration();
        });
    }

    private void handleRegistration() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim(); //
        String gender = etGender.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Email, password and name are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            User existingUser = db.userDao().findUserByEmailOnce(email);

            if (existingUser != null) {

                runOnUiThread(() -> Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT).show());
            } else {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setPassword(password);
                newUser.setName(name);
                newUser.setGender(gender);
                newUser.setAge(ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr));
                newUser.setHeight(heightStr.isEmpty() ? 0 : Double.parseDouble(heightStr));
                newUser.setWeight(weightStr.isEmpty() ? 0 : Double.parseDouble(weightStr));
                newUser.setActivityLevel("Sedentary");

                db.userDao().insert(newUser);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
}