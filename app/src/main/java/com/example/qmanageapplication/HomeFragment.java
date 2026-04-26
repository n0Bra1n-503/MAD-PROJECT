package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qmanageapplication.adapters.OutletAdapter;
import com.example.qmanageapplication.models.Outlet;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OutletAdapter.OnOutletClickListener {

    private RecyclerView rvOutlets;
    private OutletAdapter outletAdapter;
    private List<Outlet> allOutlets;
    private TextView chipAll, chipBurgers, chipPizza, chipHealthy;
    private TextView selectedChip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvOutlets = view.findViewById(R.id.rvOutlets);
        chipAll = view.findViewById(R.id.chipAll);
        chipBurgers = view.findViewById(R.id.chipBurgers);
        chipPizza = view.findViewById(R.id.chipPizza);
        chipHealthy = view.findViewById(R.id.chipHealthy);

        selectedChip = chipAll;

        setupChips();
        setupOutlets();

        return view;
    }

    private void setupChips() {
        View.OnClickListener chipClickListener = v -> {
            // Reset previous chip
            if (selectedChip != null) {
                selectedChip.setBackgroundResource(R.drawable.bg_chip_unselected);
                selectedChip.setTextColor(getResources().getColor(R.color.text_secondary, null));
            }

            // Set new selected chip
            selectedChip = (TextView) v;
            selectedChip.setBackgroundResource(R.drawable.bg_chip_selected);
            selectedChip.setTextColor(getResources().getColor(R.color.white, null));

            // Filter outlets
            filterOutlets(selectedChip.getText().toString());
        };

        chipAll.setOnClickListener(chipClickListener);
        chipBurgers.setOnClickListener(chipClickListener);
        chipPizza.setOnClickListener(chipClickListener);
        chipHealthy.setOnClickListener(chipClickListener);
    }

    private void setupOutlets() {
        allOutlets = getDummyOutlets();
        outletAdapter = new OutletAdapter(allOutlets, this);
        rvOutlets.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOutlets.setAdapter(outletAdapter);
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
            outletAdapter = new OutletAdapter(filtered.isEmpty() ? allOutlets : filtered, this);
        }
        rvOutlets.setAdapter(outletAdapter);
    }

    private List<Outlet> getDummyOutlets() {
        List<Outlet> outlets = new ArrayList<>();
        outlets.add(new Outlet("Kathi Junction", "Rolls • Indian • Fried",
                4.1f, "12 mins wait", "8 in queue",
                R.drawable.placeholder_food, true));
        outlets.add(new Outlet("Burger Singh", "Burgers • Fries • Shakes",
                4.5f, "12 mins wait", "8 in queue",
                R.drawable.placeholder_food, true));
        outlets.add(new Outlet("Maggi Point", "Maggie • Snacks • Sandwiches",
                4.3f, "40 mins wait", "19 in queue",
                R.drawable.placeholder_food, true));
        outlets.add(new Outlet("Smoothie", "Drinks • Juices • Fruit",
                4.5f, "1 min wait", "1 in queue",
                R.drawable.placeholder_food, true));
        outlets.add(new Outlet("Pizza Corner", "Pizza • Italian • Oven Fresh",
                4.2f, "15 mins wait", "5 in queue",
                R.drawable.placeholder_food, true));
        return outlets;
    }

    @Override
    public void onOutletClick(Outlet outlet, int position) {
        Intent intent = new Intent(getActivity(), OutletMenuActivity.class);
        intent.putExtra("outlet_name", outlet.getName());
        intent.putExtra("outlet_categories", outlet.getCategories());
        intent.putExtra("outlet_rating", outlet.getRating());
        startActivity(intent);
    }
}
