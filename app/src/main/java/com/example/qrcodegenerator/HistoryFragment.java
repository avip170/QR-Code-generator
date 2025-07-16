package com.example.qrcodegenerator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // For now, just launch the existing HistoryActivity
        startActivity(new Intent(getActivity(), HistoryActivity.class));
        // Return an empty view
        return new View(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set Home tab as selected in BottomNavigationView
        if (getActivity() != null) {
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_home);
            }
        }
    }
} 