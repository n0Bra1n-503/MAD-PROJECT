package com.example.qmanageapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handle edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get views
        ImageView imgLogo = findViewById(R.id.imgLogo);
        TextView tvAppName = findViewById(R.id.tvAppName);
        LinearLayout logoContainer = findViewById(R.id.logoContainer);

        // Set initial state - invisible and scaled down
        logoContainer.setAlpha(0f);
        logoContainer.setScaleX(0.6f);
        logoContainer.setScaleY(0.6f);

        // Animate logo appearing with scale + fade
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(logoContainer, View.ALPHA, 0f, 1f);
        fadeIn.setDuration(800);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logoContainer, View.SCALE_X, 0.6f, 1f);
        scaleX.setDuration(800);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logoContainer, View.SCALE_Y, 0.6f, 1f);
        scaleY.setDuration(800);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, scaleX, scaleY);
        animatorSet.setInterpolator(new OvershootInterpolator(1.2f));
        animatorSet.setStartDelay(300);
        animatorSet.start();

        // Navigate to the onboarding screen after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
            startActivity(intent);
            finish();
            // Smooth transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, SPLASH_DELAY);
    }
}
