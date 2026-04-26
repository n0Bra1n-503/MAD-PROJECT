package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qmanageapplication.adapters.OrderAdapter;
import com.example.qmanageapplication.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        RecyclerView rvOrders = view.findViewById(R.id.rvOrders);
        List<Order> orders = getDummyOrders();
        OrderAdapter adapter = new OrderAdapter(orders, this);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(adapter);

        return view;
    }

    private List<Order> getDummyOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("3421", "Kathi Junction",
                "Oct 24, 2023 • 12:30 PM", 150,
                "#B-42", "Ready", R.drawable.placeholder_food));
        orders.add(new Order("3308", "Burger Singh",
                "Oct 18, 2023 • 07:15 PM", 200,
                "#B-38", "Ready", R.drawable.placeholder_food));
        orders.add(new Order("3195", "Pizza Corner",
                "Oct 12, 2023 • 01:45 PM", 320,
                "#B-35", "Ready", R.drawable.placeholder_food));
        orders.add(new Order("3102", "Smoothie",
                "Oct 05, 2023 • 11:20 AM", 90,
                "#B-31", "Ready", R.drawable.placeholder_food));
        return orders;
    }

    @Override
    public void onOrderClick(Order order, int position) {
        Intent intent = new Intent(getActivity(), OrderTrackingActivity.class);
        intent.putExtra("token_number", order.getTokenNumber());
        intent.putExtra("prep_time", 8);
        startActivity(intent);
    }
}
