package com.example.qmanageapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qmanageapplication.network.ApiClient;
import com.example.qmanageapplication.network.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etComment;
    private Button btnSubmit;
    private TextView tvOutletName, tvSkip;
    private int orderId, outletId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        sessionManager = new SessionManager(this);
        
        orderId = getIntent().getIntExtra("order_id", -1);
        outletId = getIntent().getIntExtra("outlet_id", -1);
        String outletName = getIntent().getStringExtra("outlet_name");

        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmitReview);
        tvOutletName = findViewById(R.id.tvOutletName);
        tvSkip = findViewById(R.id.tvSkip);

        if (outletName != null) {
            tvOutletName.setText(outletName);
        }

        btnSubmit.setOnClickListener(v -> submitReview());
        tvSkip.setOnClickListener(v -> finish());
    }

    private void submitReview() {
        int rating = (int) ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("userId", sessionManager.getUserId());
        reviewData.put("outletId", outletId);
        reviewData.put("rating", rating);
        reviewData.put("comment", comment);

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Submitting...");

        ApiClient.getApiService().addReview(reviewData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ReviewActivity.this, "Thank you for your feedback!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit Review");
                    String errorMsg = "Failed to submit review (Error " + response.code() + ")";
                    Toast.makeText(ReviewActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit Review");
                Toast.makeText(ReviewActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
