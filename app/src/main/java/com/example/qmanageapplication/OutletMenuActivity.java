package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import com.example.qmanageapplication.adapters.FoodItemAdapter;
import com.example.qmanageapplication.models.CartManager;
import com.example.qmanageapplication.models.FoodItem;
import com.example.qmanageapplication.network.ApiClient;
import com.example.qmanageapplication.network.responses.MenuResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletMenuActivity extends AppCompatActivity implements FoodItemAdapter.OnAddClickListener {

    private static final String TAG = "OutletMenuActivity";
    private RecyclerView rvFoodItems;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> allFoodItems = new ArrayList<>();
    private LinearLayout cartBarLayout;
    private TextView tvCartItemCount, tvCartTotal;
    private TextView tabAllItems, tabMainCourse, tabSides, tabRolls;
    private TextView selectedTab;
    private ProgressBar progressBar;
    private int outletId;
    private boolean isOutletOpen = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_menu);

        // Get outlet info from intent
        outletId = getIntent().getIntExtra("outlet_id", -1);
        String outletName = getIntent().getStringExtra("outlet_name");
        String outletCategories = getIntent().getStringExtra("outlet_categories");
        float outletRating = getIntent().getFloatExtra("outlet_rating", 4.5f);
        isOutletOpen = getIntent().getBooleanExtra("is_open", true);


        // Set outlet info
        TextView tvOutletName = findViewById(R.id.tvOutletName);
        TextView tvOutletSubtitle = findViewById(R.id.tvOutletSubtitle);
        TextView tvRating = findViewById(R.id.tvRating);
        progressBar = findViewById(R.id.progressBar);

        tvOutletName.setText(outletName != null ? outletName : "Kathi Junction");
        tvOutletSubtitle.setText(outletCategories != null ? outletCategories : "Exquisite Rolls");
        tvRating.setText(String.format(Locale.getDefault(), "%.1f (500+)", outletRating));

        // Set outlet banner image
        ImageView imgBanner = findViewById(R.id.imgBanner);
        String outletImageUrl = getIntent().getStringExtra("outlet_image_url");
        String outletImageRes = getIntent().getStringExtra("outlet_image_res");

        if (outletImageUrl != null && !outletImageUrl.isEmpty()) {
            String fullUrl = ApiClient.BASE_URL.replace("/api/", "") + outletImageUrl;
            Glide.with(this)
                    .load(fullUrl)
                    .placeholder(R.drawable.placeholder_outlet_banner)
                    .into(imgBanner);
        } else if (outletImageRes != null) {
            int resId = getResources().getIdentifier(outletImageRes, "drawable", getPackageName());
            if (resId != 0) {
                imgBanner.setImageResource(resId);
            }
        }

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Cart bar
        cartBarLayout = findViewById(R.id.cartBarLayout);
        tvCartItemCount = findViewById(R.id.tvCartItemCount);
        tvCartTotal = findViewById(R.id.tvCartTotal);

        cartBarLayout.setOnClickListener(v -> {
            Intent intent = new Intent(OutletMenuActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Tabs
        tabAllItems = findViewById(R.id.tabAllItems);
        tabMainCourse = findViewById(R.id.tabMainCourse);
        tabSides = findViewById(R.id.tabSides);
        tabRolls = findViewById(R.id.tabRolls);
        selectedTab = tabAllItems;

        setupTabs();

        // Food items
        rvFoodItems = findViewById(R.id.rvFoodItems);
        foodItemAdapter = new FoodItemAdapter(allFoodItems, this);
        foodItemAdapter.setOutletOpen(isOutletOpen);
        rvFoodItems.setLayoutManager(new LinearLayoutManager(this));
        rvFoodItems.setAdapter(foodItemAdapter);


        if (outletId != -1) {
            fetchMenu();
        } else {
            Toast.makeText(this, "Invalid Outlet", Toast.LENGTH_SHORT).show();
        }

        updateCartBar();

        if (!isOutletOpen) {
            Toast.makeText(this, "This outlet is currently closed. You cannot place orders.", Toast.LENGTH_LONG).show();
        }
    }


    private void fetchMenu() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        ApiClient.getApiService().getOutletMenu(outletId).enqueue(new Callback<MenuResponse>() {
            @Override
            public void onResponse(@NonNull Call<MenuResponse> call, @NonNull Response<MenuResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    allFoodItems = response.body().getMenuItems();
                    foodItemAdapter.updateList(allFoodItems);
                } else {
                    Toast.makeText(OutletMenuActivity.this, "Failed to load menu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MenuResponse> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(OutletMenuActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBar();
    }

    private void setupTabs() {
        View.OnClickListener tabClickListener = v -> {
            if (selectedTab != null) {
                selectedTab.setTextColor(getResources().getColor(R.color.text_secondary, null));
            }

            selectedTab = (TextView) v;
            selectedTab.setTextColor(getResources().getColor(R.color.primary, null));

            filterByCategory(selectedTab.getText().toString());
        };

        tabAllItems.setOnClickListener(tabClickListener);
        tabMainCourse.setOnClickListener(tabClickListener);
        tabSides.setOnClickListener(tabClickListener);
        tabRolls.setOnClickListener(tabClickListener);
    }

    private void filterByCategory(String category) {
        if (category.equals(getString(R.string.tab_all_items))) {
            foodItemAdapter.updateList(allFoodItems);
        } else {
            List<FoodItem> filtered = new ArrayList<>();
            for (FoodItem item : allFoodItems) {
                if (item.getCategory() != null && item.getCategory().equalsIgnoreCase(category)) {
                    filtered.add(item);
                }
            }

            foodItemAdapter.updateList(filtered);
        }
    }

    @Override
    public void onAddClick(FoodItem foodItem, int position) {
        CartManager.getInstance().addItem(foodItem);
        Toast.makeText(this, foodItem.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        updateCartBar();
    }

    private void updateCartBar() {
        CartManager cart = CartManager.getInstance();
        if (cart.isEmpty()) {
            cartBarLayout.setVisibility(View.GONE);
        } else {
            cartBarLayout.setVisibility(View.VISIBLE);
            tvCartItemCount.setText(cart.getItemCount() + " ITEMS");
            tvCartTotal.setText(String.format(Locale.getDefault(), "Rs. %.0f", cart.getSubtotal()));
        }
    }
}
