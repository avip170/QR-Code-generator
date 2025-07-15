package com.example.qrcodegenerator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        final EditText editNameHome = findViewById(R.id.editNameHome);
        final EditText editAddressHome = findViewById(R.id.editAddressHome);
        final EditText editZoneHome = findViewById(R.id.editZoneHome);
        final EditText editSpotHome = findViewById(R.id.editSpotHome);
        MaterialButton btnHistory = findViewById(R.id.btn_history);
        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoryActivity.class));
        });
        MaterialButton btnGenerateQr = findViewById(R.id.btn_generate_qr);
        btnGenerateQr.setOnClickListener(v -> {
            String name = editNameHome.getText().toString().trim();
            String address = editAddressHome.getText().toString().trim();
            String zone = editZoneHome.getText().toString().trim();
            String spot = editSpotHome.getText().toString().trim();
            Intent intent = new Intent(this, QrGeneratorActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("address", address);
            intent.putExtra("zone", zone);
            intent.putExtra("spot", spot);
            startActivity(intent);
        });
        MaterialButton btnHome = findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> {
            // Home button just refreshes MainActivity or does nothing
            recreate();
        });
    }
}