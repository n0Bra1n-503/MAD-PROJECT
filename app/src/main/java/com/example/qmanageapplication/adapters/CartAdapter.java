package com.example.qmanageapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qmanageapplication.R;
import com.example.qmanageapplication.models.CartItem;
import com.example.qmanageapplication.network.ApiClient;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgFood;
        private final TextView tvFoodName;
        private final TextView tvQuantity;
        private final TextView tvPrice;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

        void bind(CartItem item) {
            tvFoodName.setText(item.getFoodItem().getName());
            tvQuantity.setText(String.format(Locale.getDefault(), "Qty: %d", item.getQuantity()));
            tvPrice.setText(String.format(Locale.getDefault(), "Rs. %.0f", item.getTotalPrice()));

            String imageUrl = item.getFoodItem().getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String fullUrl = ApiClient.BASE_URL.replace("/api/", "") + imageUrl;
                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.placeholder_food)
                        .into(imgFood);
            } else {
                int resId = itemView.getContext().getResources().getIdentifier(
                        item.getFoodItem().getImageResName(), "drawable", itemView.getContext().getPackageName());
                if (resId != 0) {
                    imgFood.setImageResource(resId);
                } else {
                    imgFood.setImageResource(R.drawable.placeholder_food);
                }
            }
        }
    }
}
