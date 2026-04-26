package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qmanageapplication.adapters.FoodItemAdapter;
import com.example.qmanageapplication.models.CartManager;
import com.example.qmanageapplication.models.FoodItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OutletMenuActivity extends AppCompatActivity implements FoodItemAdapter.OnAddClickListener {

    private RecyclerView rvFoodItems;
    private FoodItemAdapter foodItemAdapter;
    private List<FoodItem> allFoodItems;
    private LinearLayout cartBarLayout;
    private TextView tvCartItemCount, tvCartTotal;
    private TextView tabAllItems, tabMainCourse, tabSides, tabRolls;
    private TextView selectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_menu);

        // Get outlet info from intent
        String outletName = getIntent().getStringExtra("outlet_name");
        String outletCategories = getIntent().getStringExtra("outlet_categories");
        float outletRating = getIntent().getFloatExtra("outlet_rating", 4.5f);

        // Set outlet info
        TextView tvOutletName = findViewById(R.id.tvOutletName);
        TextView tvOutletSubtitle = findViewById(R.id.tvOutletSubtitle);
        TextView tvRating = findViewById(R.id.tvRating);

        tvOutletName.setText(outletName != null ? outletName : "Kathi Junction");
        tvOutletSubtitle.setText(outletCategories != null ? outletCategories : "Exquisite Rolls");
        tvRating.setText(String.format(Locale.getDefault(), "%.1f (500+)", outletRating));

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Cart bar
        cartBarLayout = findViewById(R.id.cartBarLayout);
        tvCartItemCount = findViewById(R.id.tvCartItemCount);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        TextView tvViewCart = findViewById(R.id.tvViewCart);

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
        allFoodItems = getDummyFoodItems();
        foodItemAdapter = new FoodItemAdapter(allFoodItems, this);
        rvFoodItems.setLayoutManager(new LinearLayoutManager(this));
        rvFoodItems.setAdapter(foodItemAdapter);

        updateCartBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBar();
    }

    private void setupTabs() {
        View.OnClickListener tabClickListener = v -> {
            // Reset previous tab
            if (selectedTab != null) {
                selectedTab.setTextColor(getResources().getColor(R.color.text_secondary, null));
            }

            // Set new tab
            selectedTab = (TextView) v;
            selectedTab.setTextColor(getResources().getColor(R.color.primary, null));

            // Filter items
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
                if (item.getCategory().equalsIgnoreCase(category)) {
                    filtered.add(item);
                }
            }
            foodItemAdapter.updateList(filtered.isEmpty() ? allFoodItems : filtered);
        }
    }

    private List<FoodItem> getDummyFoodItems() {
        List<FoodItem> items = new ArrayList<>();
        items.add(new FoodItem("Paneer Roll", "Roll with paneer and sauces.",
                80, R.drawable.placeholder_food, "Rolls"));
        items.add(new FoodItem("Peri Peri Paneer Roll", "Roll with paneer and sauces.",
                90, R.drawable.placeholder_food, "Rolls"));
        items.add(new FoodItem("Butter Chicken", "Chicken With Gravy",
                200, R.drawable.placeholder_food, "Main Course"));
        items.add(new FoodItem("Paneer Butter Masala", "Paneer with Gravy",
                250, R.drawable.placeholder_food, "Main Course"));
        items.add(new FoodItem("Chicken Burger", "Juicy chicken patty with cheese",
                100, R.drawable.placeholder_food, "Main Course"));
        items.add(new FoodItem("Cola", "Chilled soft drink",
                50, R.drawable.placeholder_food, "Sides & Extras"));
        items.add(new FoodItem("French Fries", "Crispy golden fries",
                70, R.drawable.placeholder_food, "Sides & Extras"));
        items.add(new FoodItem("Garlic Bread", "Toasted with garlic butter",
                80, R.drawable.placeholder_food, "Sides & Extras"));
        return items;
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
