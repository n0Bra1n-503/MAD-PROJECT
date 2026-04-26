package com.example.qmanageapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Menu item clicks
        LinearLayout menuOrders = view.findViewById(R.id.menuOrders);
        LinearLayout menuSettings = view.findViewById(R.id.menuSettings);
        LinearLayout menuHelp = view.findViewById(R.id.menuHelp);
        TextView btnLogout = view.findViewById(R.id.btnLogout);

        menuOrders.setOnClickListener(v -> {
            // Switch to orders tab
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).findViewById(R.id.bottom_navigation)
                        .findViewById(R.id.nav_orders).performClick();
            }
        });

        menuSettings.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Settings coming soon", Toast.LENGTH_SHORT).show();
        });

        menuHelp.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Help & Support coming soon", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}
