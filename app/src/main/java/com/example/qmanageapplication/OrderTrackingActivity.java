package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qmanageapplication.models.Order;
import com.example.qmanageapplication.network.ApiClient;
import com.example.qmanageapplication.network.responses.SingleOrderResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTrackingActivity extends AppCompatActivity {

    private static final String TAG = "OrderTrackingActivity";
    private int orderId;
    private View statusPreparing, statusReady, line1, line2;
    private Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        // Get data from intent
        orderId = getIntent().getIntExtra("order_id", -1);
        String tokenNumber = getIntent().getStringExtra("token_number");
        int prepTime = getIntent().getIntExtra("prep_time", 8);

        // Initialize Views
        statusPreparing = findViewById(R.id.statusPreparing);
        statusReady = findViewById(R.id.statusReady);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);

        // Set token number
        TextView tvTokenNumber = findViewById(R.id.tvTokenNumber);
        tvTokenNumber.setText(tokenNumber != null ? tokenNumber : "#B-42");

        // Set time left
        TextView tvTimeLeft = findViewById(R.id.tvTimeLeft);
        tvTimeLeft.setText(prepTime + " mins left");

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> goBack());

        // Initial fetch
        if (orderId != -1) {
            fetchOrderStatus();
            startAutoRefresh();
        }
    }

    private void fetchOrderStatus() {
        ApiClient.getApiService().getOrderById(orderId).enqueue(new Callback<SingleOrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<SingleOrderResponse> call, @NonNull Response<SingleOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    updateUI(response.body().getOrder().getStatus());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SingleOrderResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Fetch status failed", t);
            }
        });
    }

    private void updateUI(String status) {
        if (status == null) return;
        
        // Reset colors to default (gray/inactive) first if needed, 
        // but here we just progress forward.
        
        String currentStatus = status.toLowerCase();
        
        if (currentStatus.equals("preparing")) {
            statusPreparing.setBackgroundResource(R.drawable.bg_status_active);
            line1.setBackgroundColor(getResources().getColor(R.color.success));
        } else if (currentStatus.equals("ready")) {
            statusPreparing.setBackgroundResource(R.drawable.bg_status_active);
            line1.setBackgroundColor(getResources().getColor(R.color.success));
            statusReady.setBackgroundResource(R.drawable.bg_status_active);
            line2.setBackgroundColor(getResources().getColor(R.color.success));
            
            // Update time text when ready
            TextView tvTimeLeft = findViewById(R.id.tvTimeLeft);
            tvTimeLeft.setText("Order is Ready!");
        } else if (currentStatus.equals("completed")) {
            // If completed, maybe show a toast or redirect
            Toast.makeText(this, "Order completed! Thank you.", Toast.LENGTH_SHORT).show();
            goBack();
        }
    }

    private void startAutoRefresh() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                fetchOrderStatus();
                handler.postDelayed(this, 5000); // Refresh every 5 seconds
            }
        };
        handler.postDelayed(refreshRunnable, 5000);
    }

    private void goBack() {
        Intent intent = new Intent(OrderTrackingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
