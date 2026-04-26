package com.example.qmanageapplication.models;

public class Order {
    private String orderId;
    private String outletName;
    private String date;
    private double amount;
    private String tokenNumber;
    private String status; // "Received", "Preparing", "Ready"
    private int outletImageResId;

    public Order(String orderId, String outletName, String date, double amount,
                 String tokenNumber, String status, int outletImageResId) {
        this.orderId = orderId;
        this.outletName = outletName;
        this.date = date;
        this.amount = amount;
        this.tokenNumber = tokenNumber;
        this.status = status;
        this.outletImageResId = outletImageResId;
    }

    public String getOrderId() { return orderId; }
    public String getOutletName() { return outletName; }
    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public String getTokenNumber() { return tokenNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getOutletImageResId() { return outletImageResId; }
}
