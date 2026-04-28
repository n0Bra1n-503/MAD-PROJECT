package com.example.qmanageapplication.network;

import com.example.qmanageapplication.network.responses.AuthResponse;
import com.example.qmanageapplication.network.responses.OrderResponse;
import com.example.qmanageapplication.network.responses.OrderListResponse;
import com.example.qmanageapplication.network.responses.OutletResponse;
import com.example.qmanageapplication.network.responses.MenuResponse;
import com.example.qmanageapplication.network.responses.SingleOrderResponse;
import com.example.qmanageapplication.network.UserRequest;
import com.example.qmanageapplication.network.OrderRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @GET("outlets")
    Call<OutletResponse> getAllOutlets();

    @GET("outlets/{id}/menu")
    Call<MenuResponse> getOutletMenu(@Path("id") int outletId);

    @POST("users/register")
    Call<AuthResponse> register(@Body UserRequest request);

    @POST("users/login")
    Call<AuthResponse> login(@Body UserRequest request);

    @POST("orders")
    Call<OrderResponse> placeOrder(@Body OrderRequest request);

    @GET("orders/user/{userId}")
    Call<OrderListResponse> getUserOrders(@Path("userId") int userId);

    @GET("orders/{orderId}")
    Call<SingleOrderResponse> getOrderById(@Path("orderId") int orderId);

    @POST("users/google-login")
    Call<AuthResponse> googleLogin(@Body java.util.Map<String, String> body);

    @POST("reviews")
    Call<Void> addReview(@Body java.util.Map<String, Object> review);

    @GET("reviews/outlet/{id}")
    Call<java.util.Map<String, Object>> getOutletReviews(@Path("id") int outletId);
}

