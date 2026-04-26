package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText etEmail, etCreatePassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etCreatePassword = findViewById(R.id.etCreatePassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);
        MaterialButton btnGoogle = findViewById(R.id.btnGoogle);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);

        // Sign up button
        btnSignUp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etCreatePassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etCreatePassword.setError("Password is required");
                etCreatePassword.requestFocus();
                return;
            }

            if (password.length() < 6) {
                etCreatePassword.setError("Password must be at least 6 characters");
                etCreatePassword.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                etConfirmPassword.requestFocus();
                return;
            }

            // Navigate to Home
            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Google button
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Google Sign-In coming soon", Toast.LENGTH_SHORT).show();
        });

        // Navigate back to Login
        tvLoginLink.setOnClickListener(v -> {
            finish();
        });
    }
}
