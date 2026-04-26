package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrderTrackingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        // Get data from intent
        String tokenNumber = getIntent().getStringExtra("token_number");
        int prepTime = getIntent().getIntExtra("prep_time", 8);

        // Set token number
        TextView tvTokenNumber = findViewById(R.id.tvTokenNumber);
        tvTokenNumber.setText(tokenNumber != null ? tokenNumber : "#B-42");

        // Set time left
        TextView tvTimeLeft = findViewById(R.id.tvTimeLeft);
        tvTimeLeft.setText(prepTime + " mins left");

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(OrderTrackingActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
