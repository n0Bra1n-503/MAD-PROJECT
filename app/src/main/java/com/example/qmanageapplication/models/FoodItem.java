package com.example.qmanageapplication.models;

public class FoodItem {
    private String name;
    private String description;
    private double price;
    private int imageResId;
    private String category; // "Popular Items", "Main Course", "Sides & Extras", "Rolls"

    public FoodItem(String name, String description, double price, int imageResId, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.category = category;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getCategory() { return category; }
}
