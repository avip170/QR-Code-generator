package com.example.qrcodegenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.button.MaterialButton;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ScanHistoryAdapter adapter;
    private List<ScanHistoryItem> scanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.recycler_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scanList = new ArrayList<>();
        adapter = new ScanHistoryAdapter(scanList);
        recyclerView.setAdapter(adapter);
        loadHistory();

        MaterialButton btnDeleteHistory = findViewById(R.id.btn_delete_history);
        btnDeleteHistory.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("qr_scans", Context.MODE_PRIVATE);
            prefs.edit().remove("scan_history").apply();
            scanList.clear();
            adapter.notifyDataSetChanged();
        });
    }

    private void loadHistory() {
        SharedPreferences prefs = getSharedPreferences("qr_scans", Context.MODE_PRIVATE);
        String historyJson = prefs.getString("scan_history", "[]");
        scanList.clear();
        try {
            JSONArray arr = new JSONArray(historyJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String timestamp = obj.optString("timestamp");
                String name = obj.optString("name");
                String address = obj.optString("address");
                String zone = obj.optString("zone");
                String spot = obj.optString("spot");
                String qrImagePath = obj.optString("qrImagePath");
                scanList.add(new ScanHistoryItem(timestamp, name, address, zone, spot, qrImagePath));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    public static class ScanHistoryItem {
        public final String timestamp;
        public final String name;
        public final String address;
        public final String zone;
        public final String spot;
        public final String qrImagePath;
        public ScanHistoryItem(String timestamp, String name, String address, String zone, String spot, String qrImagePath) {
            this.timestamp = timestamp;
            this.name = name;
            this.address = address;
            this.zone = zone;
            this.spot = spot;
            this.qrImagePath = qrImagePath;
        }
    }
} 