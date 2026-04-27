package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Random;

public class OrderConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        // Get real token from intent
        String token = getIntent().getStringExtra("token_number");
        if (token == null) token = "QM-0000";

        TextView tvTokenNumber = findViewById(R.id.tvTokenNumber);
        tvTokenNumber.setText(token);

        // Random prep time (this could also be fetched from API if needed)
        Random random = new Random();
        TextView tvPrepTime = findViewById(R.id.tvPrepTime);
        int prepTime = random.nextInt(15) + 8;
        tvPrepTime.setText(prepTime + " mins");

        // Track Order button
        int orderId = getIntent().getIntExtra("order_id", -1);
        String finalToken = token;
        MaterialButton btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderConfirmationActivity.this, OrderTrackingActivity.class);
            intent.putExtra("order_id", orderId);
            intent.putExtra("token_number", finalToken);
            intent.putExtra("prep_time", prepTime);
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
