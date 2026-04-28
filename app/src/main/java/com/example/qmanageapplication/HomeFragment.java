package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qmanageapplication.adapters.OutletAdapter;
import com.example.qmanageapplication.models.Outlet;
import com.example.qmanageapplication.network.ApiClient;
import com.example.qmanageapplication.network.SessionManager;
import com.example.qmanageapplication.network.responses.OutletResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OutletAdapter.OnOutletClickListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView rvOutlets;
    private OutletAdapter outletAdapter;
    private List<Outlet> allOutlets = new ArrayList<>();
    private TextView chipAll, chipBurgers, chipPizza, chipHealthy;
    private TextView selectedChip;
    private ProgressBar progressBar;

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(requireContext());
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        if (sessionManager.isLoggedIn()) {
            String firstName = sessionManager.getUserName().split(" ")[0];
            tvGreeting.setText("Hello, " + firstName + "!");
        }

        rvOutlets = view.findViewById(R.id.rvOutlets);
        chipAll = view.findViewById(R.id.chipAll);
        chipBurgers = view.findViewById(R.id.chipBurgers);
        chipPizza = view.findViewById(R.id.chipPizza);
        chipHealthy = view.findViewById(R.id.chipHealthy);
        progressBar = view.findViewById(R.id.progressBar);

        selectedChip = chipAll;

        setupChips();
        setupOutlets();
        fetchOutlets();

        return view;
    }

    private void setupChips() {
        View.OnClickListener chipClickListener = v -> {
            if (selectedChip != null) {
                selectedChip.setBackgroundResource(R.drawable.bg_chip_unselected);
                selectedChip.setTextColor(getResources().getColor(R.color.text_secondary, null));
            }

            selectedChip = (TextView) v;
            selectedChip.setBackgroundResource(R.drawable.bg_chip_selected);
            selectedChip.setTextColor(getResources().getColor(R.color.white, null));

            filterOutlets(selectedChip.getText().toString());
        };

        chipAll.setOnClickListener(chipClickListener);
        chipBurgers.setOnClickListener(chipClickListener);
        chipPizza.setOnClickListener(chipClickListener);
        chipHealthy.setOnClickListener(chipClickListener);
    }

    private void setupOutlets() {
        outletAdapter = new OutletAdapter(allOutlets, this);
        rvOutlets.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOutlets.setAdapter(outletAdapter);
    }

    private void fetchOutlets() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        ApiClient.getApiService().getAllOutlets().enqueue(new Callback<OutletResponse>() {
            @Override
            public void onResponse(@NonNull Call<OutletResponse> call, @NonNull Response<OutletResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    allOutlets = response.body().getOutlets();
                    outletAdapter = new OutletAdapter(allOutlets, HomeFragment.this);
                    rvOutlets.setAdapter(outletAdapter);
                } else {
                    Toast.makeText(getContext(), "Failed to load outlets", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OutletResponse> call, @NonNull Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOutlets(String category) {
        if (category.equals("All")) {
            outletAdapter = new OutletAdapter(allOutlets, this);
        } else {
            List<Outlet> filtered = new ArrayList<>();
            for (Outlet outlet : allOutlets) {
                if (outlet.getCategories().toLowerCase().contains(category.toLowerCase())) {
                    filtered.add(outlet);
                }
            }
            outletAdapter = new OutletAdapter(filtered, this);
        }
        rvOutlets.setAdapter(outletAdapter);
    }

    @Override
    public void onOutletClick(Outlet outlet, int position) {
        Intent intent = new Intent(getActivity(), OutletMenuActivity.class);
        intent.putExtra("outlet_id", outlet.getId());
        intent.putExtra("outlet_name", outlet.getName());
        intent.putExtra("outlet_categories", outlet.getCategories());
        intent.putExtra("outlet_rating", outlet.getRating());
        intent.putExtra("outlet_image_url", outlet.getImageUrl());
        intent.putExtra("outlet_image_res", outlet.getImageResName());
        startActivity(intent);
    }
}
