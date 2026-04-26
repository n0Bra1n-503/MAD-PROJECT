package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        ImageView imgOnboarding = findViewById(R.id.imgOnboarding);
        MaterialButton btnNext = findViewById(R.id.btnNext);

        // Navigate to Login
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
