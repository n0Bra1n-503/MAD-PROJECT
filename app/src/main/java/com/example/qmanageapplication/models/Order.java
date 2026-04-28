package com.example.qmanageapplication.models;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("id")
    private int orderId;

    @SerializedName("outletName")
    private String outletName;

    @SerializedName("created_at")
    private String date;

    @SerializedName("total_amount")
    private double amount;

    @SerializedName("token_number")
    private String tokenNumber;

    private String status; // "Received", "Preparing", "Ready"

    @SerializedName("outlet_id")
    private int outletId;

    @SerializedName("outletImage")
    private String outletImage;

    public Order(int orderId, int outletId, String outletName, String date, double amount,
                 String tokenNumber, String status, String outletImage) {
        this.orderId = orderId;
        this.outletId = outletId;
        this.outletName = outletName;
        this.date = date;
        this.amount = amount;
        this.tokenNumber = tokenNumber;
        this.status = status;
        this.outletImage = outletImage;
    }

    public int getOrderId() { return orderId; }
    public int getOutletId() { return outletId; }
    public String getOutletName() { return outletName; }

    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public String getTokenNumber() { return tokenNumber; }
    public String getStatus() { return status != null ? status : "Received"; }
    public void setStatus(String status) { this.status = status; }
    public String getOutletImage() { return outletImage != null ? outletImage : "placeholder_food"; }
}
