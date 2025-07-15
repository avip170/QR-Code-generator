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
                String zone = obj.optString("zone");
                String spot = obj.optString("spot");
                scanList.add(new ScanHistoryItem(timestamp, zone, spot));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    public static class ScanHistoryItem {
        public final String timestamp;
        public final String zone;
        public final String spot;
        public ScanHistoryItem(String timestamp, String zone, String spot) {
            this.timestamp = timestamp;
            this.zone = zone;
            this.spot = spot;
        }
    }
} 