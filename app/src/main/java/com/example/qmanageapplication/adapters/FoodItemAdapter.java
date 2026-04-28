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
import com.example.qmanageapplication.models.FoodItem;
import com.example.qmanageapplication.network.ApiClient;

import java.util.List;
import java.util.Locale;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder> {

    private List<FoodItem> foodItems;
    private final OnAddClickListener listener;

    public interface OnAddClickListener {
        void onAddClick(FoodItem foodItem, int position);
    }

    public FoodItemAdapter(List<FoodItem> foodItems, OnAddClickListener listener) {
        this.foodItems = foodItems;
        this.listener = listener;
    }

    public void updateList(List<FoodItem> newList) {
        this.foodItems = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFoodName;
        private final TextView tvFoodDescription;
        private final TextView tvFoodPrice;
        private final TextView btnAdd;
        private final ImageView imgFood;

        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvFoodDescription = itemView.findViewById(R.id.tvFoodDescription);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPrice);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            imgFood = itemView.findViewById(R.id.imgFood);

            btnAdd.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAddClick(foodItems.get(pos), pos);
                }
            });
        }

        void bind(FoodItem item) {
            tvFoodName.setText(item.getName());
            tvFoodDescription.setText(item.getDescription());
            tvFoodPrice.setText(String.format(Locale.getDefault(), "Rs. %.0f", item.getPrice()));

            String imageUrl = item.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String fullUrl = ApiClient.BASE_URL.replace("/api/", "") + imageUrl;
                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.placeholder_food)
                        .into(imgFood);
            } else {
                int resId = itemView.getContext().getResources().getIdentifier(
                        item.getImageResName(), "drawable", itemView.getContext().getPackageName());
                if (resId != 0) {
                    imgFood.setImageResource(resId);
                } else {
                    imgFood.setImageResource(R.drawable.placeholder_food);
                }
            }
        }
    }
}
