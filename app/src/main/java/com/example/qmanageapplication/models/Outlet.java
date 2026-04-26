package com.example.qmanageapplication.models;

public class Outlet {
    private String name;
    private String categories; // e.g. "Rolls • Indian • Fried"
    private float rating;
    private String waitTime;    // e.g. "12 mins wait"
    private String queueCount;  // e.g. "8 in queue"
    private int imageResId;
    private boolean isOpen;

    public Outlet(String name, String categories, float rating, String waitTime,
                  String queueCount, int imageResId, boolean isOpen) {
        this.name = name;
        this.categories = categories;
        this.rating = rating;
        this.waitTime = waitTime;
        this.queueCount = queueCount;
        this.imageResId = imageResId;
        this.isOpen = isOpen;
    }

    public String getName() { return name; }
    public String getCategories() { return categories; }
    public float getRating() { return rating; }
    public String getWaitTime() { return waitTime; }
    public String getQueueCount() { return queueCount; }
    public int getImageResId() { return imageResId; }
    public boolean isOpen() { return isOpen; }
}
