package com.example.qrcodegenerator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        final EditText editNameHome = view.findViewById(R.id.editNameHome);
        final EditText editAddressHome = view.findViewById(R.id.editAddressHome);
        final EditText editZoneHome = view.findViewById(R.id.editZoneHome);
        final EditText editSpotHome = view.findViewById(R.id.editSpotHome);
        MaterialButton btnGenerateQr = view.findViewById(R.id.btn_generate_qr);
        btnGenerateQr.setOnClickListener(v -> {
            String name = editNameHome.getText().toString().trim();
            String address = editAddressHome.getText().toString().trim();
            String zone = editZoneHome.getText().toString().trim();
            String spot = editSpotHome.getText().toString().trim();
            Intent intent = new Intent(getActivity(), QrGeneratorActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("address", address);
            intent.putExtra("zone", zone);
            intent.putExtra("spot", spot);
            startActivity(intent);
        });
        return view;
    }
} 