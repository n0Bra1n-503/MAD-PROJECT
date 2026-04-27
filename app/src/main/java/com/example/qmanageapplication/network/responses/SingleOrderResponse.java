package com.example.qmanageapplication.network.responses;

import com.example.qmanageapplication.models.Order;
import com.google.gson.annotations.SerializedName;

public class SingleOrderResponse {
    private boolean success;
    private String message;
    
    @SerializedName("order")
    private Order order;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Order getOrder() { return order; }
}
