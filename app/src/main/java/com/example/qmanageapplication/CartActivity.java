package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qmanageapplication.adapters.CartAdapter;
import com.example.qmanageapplication.models.CartItem;
import com.example.qmanageapplication.models.CartManager;
import com.example.qmanageapplication.network.ApiClient;
import com.example.qmanageapplication.network.OrderRequest;
import com.example.qmanageapplication.network.SessionManager;
import com.example.qmanageapplication.network.responses.OrderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "CartActivity";
    private CartAdapter cartAdapter;
    private TextView tvSubtotal, tvTax, tvTotal;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        sessionManager = new SessionManager(this);

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Cart items RecyclerView
        RecyclerView rvCartItems = findViewById(R.id.rvCartItems);
        cartAdapter = new CartAdapter(CartManager.getInstance().getCartItems());
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);

        // Price summary
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);

        updatePriceSummary();

        // Place Order button
        findViewById(R.id.btnPlaceOrder).setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        CartManager cart = CartManager.getInstance();
        if (cart.isEmpty()) return;

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login to place order", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        List<OrderRequest.OrderItemRequest> items = new ArrayList<>();
        for (CartItem item : cart.getCartItems()) {
            items.add(new OrderRequest.OrderItemRequest(
                    item.getFoodItem().getId(),
                    item.getQuantity(),
                    item.getFoodItem().getPrice()
            ));
        }

        OrderRequest request = new OrderRequest(
                sessionManager.getUserId(),
                cart.getCurrentOutletId(),
                cart.getTotal(),
                items
        );

        ApiClient.getApiService().placeOrder(request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderResponse> call, @NonNull Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Intent intent = new Intent(CartActivity.this, OrderConfirmationActivity.class);
                    intent.putExtra("order_id", response.body().getOrderId());
                    intent.putExtra("token_number", response.body().getTokenNumber());
                    startActivity(intent);
                    CartManager.getInstance().clearCart();
                    finish();
                } else {
                    Toast.makeText(CartActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Order failed", t);
                Toast.makeText(CartActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePriceSummary() {
        CartManager cart = CartManager.getInstance();
        tvSubtotal.setText(String.format(Locale.getDefault(), "Rs. %.0f", cart.getSubtotal()));
        tvTax.setText(String.format(Locale.getDefault(), "Rs. %.1f", cart.getTax()));
        tvTotal.setText(String.format(Locale.getDefault(), "Rs. %.1f", cart.getTotal()));
    }
}
