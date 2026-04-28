package com.example.qmanageapplication.models;

import com.google.gson.annotations.SerializedName;

public class Outlet {
    private int id;
    private String name;
    private String categories;
    private float rating;

    @SerializedName("wait_time_minutes")
    private int waitTimeMinutes;

    @SerializedName("queue_count")
    private int queueCount;

    @SerializedName("image_res_name")
    private String imageResName;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("is_open")
    private boolean isOpen;

    public Outlet(int id, String name, String categories, float rating, int waitTimeMinutes,
                  int queueCount, String imageResName, String imageUrl, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.rating = rating;
        this.waitTimeMinutes = waitTimeMinutes;
        this.queueCount = queueCount;
        this.imageResName = imageResName;
        this.imageUrl = imageUrl;
        this.isOpen = isOpen;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategories() { return categories; }
    public float getRating() { return rating; }
    public int getWaitTimeMinutes() { return waitTimeMinutes; }
    public int getQueueCount() { return queueCount; }
    public String getImageResName() { return imageResName; }
    public String getImageUrl() { return imageUrl; }
    public boolean isOpen() { return isOpen; }

    // Helper for display
    public String getWaitTimeDisplay() {
        return waitTimeMinutes + " mins wait";
    }

    public String getQueueCountDisplay() {
        return queueCount + " in queue";
    }
}
