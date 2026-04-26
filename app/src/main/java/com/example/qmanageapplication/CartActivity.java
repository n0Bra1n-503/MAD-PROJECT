package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qmanageapplication.adapters.CartAdapter;
import com.example.qmanageapplication.models.CartManager;

import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private CartAdapter cartAdapter;
    private TextView tvSubtotal, tvTax, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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
        findViewById(R.id.btnPlaceOrder).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, OrderConfirmationActivity.class);
            startActivity(intent);
            // Clear cart after placing order
            CartManager.getInstance().clearCart();
            finish();
        });
    }

    private void updatePriceSummary() {
        CartManager cart = CartManager.getInstance();
        tvSubtotal.setText(String.format(Locale.getDefault(), "Rs. %.0f", cart.getSubtotal()));
        tvTax.setText(String.format(Locale.getDefault(), "Rs. %.1f", cart.getTax()));
        tvTotal.setText(String.format(Locale.getDefault(), "Rs. %.1f", cart.getTotal()));
    }
}
