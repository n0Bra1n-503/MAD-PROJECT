package com.example.qmanageapplication.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // For Emulator use: http://10.0.2.2:8080/api/
    // For Physical Device use: http://<YOUR_MAC_IP>:8080/api/
    // Your Mac's current Wi-Fi IP: 10.7.33.119
    public static final String BASE_URL = "https://uninfectiously-rancid-tianna.ngrok-free.dev/api/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
