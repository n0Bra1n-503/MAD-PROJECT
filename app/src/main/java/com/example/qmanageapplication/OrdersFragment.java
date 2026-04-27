package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qmanageapplication.adapters.OrderAdapter;
import com.example.qmanageapplication.models.Order;
import com.example.qmanageapplication.network.ApiClient;
import com.example.qmanageapplication.network.SessionManager;
import com.example.qmanageapplication.network.responses.OrderListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private static final String TAG = "OrdersFragment";
    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private SessionManager sessionManager;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        sessionManager = new SessionManager(requireContext());
        rvOrders = view.findViewById(R.id.rvOrders);
        progressBar = view.findViewById(R.id.progressBar);

        adapter = new OrderAdapter(orderList, this);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(adapter);

        if (sessionManager.isLoggedIn()) {
            fetchOrders();
        } else {
            Toast.makeText(getContext(), "Please login to see orders", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchOrders() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        ApiClient.getApiService().getUserOrders(sessionManager.getUserId()).enqueue(new Callback<OrderListResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderListResponse> call, @NonNull Response<OrderListResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    orderList.clear();
                    orderList.addAll(response.body().getOrders());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderListResponse> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Fetch orders failed", t);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onOrderClick(Order order, int position) {
        Intent intent = new Intent(getActivity(), OrderTrackingActivity.class);
        intent.putExtra("order_id", order.getOrderId());
        intent.putExtra("token_number", order.getTokenNumber());
        intent.putExtra("prep_time", 8);
        startActivity(intent);
    }
}
