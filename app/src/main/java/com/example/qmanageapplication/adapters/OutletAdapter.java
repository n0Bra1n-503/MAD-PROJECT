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
import com.example.qmanageapplication.models.Outlet;
import com.example.qmanageapplication.network.ApiClient;

import java.util.List;

public class OutletAdapter extends RecyclerView.Adapter<OutletAdapter.OutletViewHolder> {

    private final List<Outlet> outlets;
    private final OnOutletClickListener listener;

    public interface OnOutletClickListener {
        void onOutletClick(Outlet outlet, int position);
    }

    public OutletAdapter(List<Outlet> outlets, OnOutletClickListener listener) {
        this.outlets = outlets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OutletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_outlet, parent, false);
        return new OutletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutletViewHolder holder, int position) {
        Outlet outlet = outlets.get(position);
        holder.bind(outlet);
    }

    @Override
    public int getItemCount() {
        return outlets.size();
    }

    class OutletViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgOutlet;
        private final TextView tvOpenBadge;
        private final TextView tvOutletName;
        private final TextView tvRating;
        private final TextView tvWaitTime;
        private final TextView tvQueueCount;
        private final TextView tvCategories;

        OutletViewHolder(@NonNull View itemView) {
            super(itemView);
            imgOutlet = itemView.findViewById(R.id.imgOutlet);
            tvOpenBadge = itemView.findViewById(R.id.tvOpenBadge);
            tvOutletName = itemView.findViewById(R.id.tvOutletName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvWaitTime = itemView.findViewById(R.id.tvWaitTime);
            tvQueueCount = itemView.findViewById(R.id.tvQueueCount);
            tvCategories = itemView.findViewById(R.id.tvCategories);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onOutletClick(outlets.get(pos), pos);
                }
            });
        }

        void bind(Outlet outlet) {
            String imageUrl = outlet.getImageUrl();
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Prepend base URL for images (removing /api/ suffix)
                String fullUrl = ApiClient.BASE_URL.replace("/api/", "") + imageUrl;
                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.placeholder_food)
                        .into(imgOutlet);
            } else {
                // Fallback to resource-based images
                int resId = itemView.getContext().getResources().getIdentifier(
                        outlet.getImageResName(), "drawable", itemView.getContext().getPackageName());
                if (resId != 0) {
                    imgOutlet.setImageResource(resId);
                } else {
                    imgOutlet.setImageResource(R.drawable.placeholder_food);
                }
            }

            tvOutletName.setText(outlet.getName());
            tvRating.setText(String.valueOf(outlet.getRating()));
            tvWaitTime.setText(outlet.getWaitTimeDisplay());
            tvQueueCount.setText(outlet.getQueueCountDisplay());
            tvCategories.setText(outlet.getCategories());

            tvOpenBadge.setVisibility(outlet.isOpen() ? View.VISIBLE : View.GONE);
        }
    }
}
