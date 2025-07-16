package com.example.qrcodegenerator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.content.Intent;
import android.view.View;
import android.content.Context;

public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ViewHolder> {
    private final List<HistoryActivity.ScanHistoryItem> scanList;

    public ScanHistoryAdapter(List<HistoryActivity.ScanHistoryItem> scanList) {
        this.scanList = scanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryActivity.ScanHistoryItem item = scanList.get(position);
        holder.textTimestamp.setText(item.timestamp);
        holder.textZone.setText("Zone: " + item.zone);
        holder.textSpot.setText("Spot: " + item.spot);
        holder.textName.setText("Name: " + item.name);
        holder.textAddress.setText("Address: " + item.address);
        if (item.qrImagePath != null && !item.qrImagePath.isEmpty()) {
            holder.imageQr.setImageBitmap(BitmapFactory.decodeFile(item.qrImagePath));
            holder.imageQr.setOnClickListener(v -> {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, QrImageViewActivity.class);
                intent.putExtra("qrImagePath", item.qrImagePath);
                context.startActivity(intent);
            });
        } else {
            holder.imageQr.setImageResource(android.R.color.darker_gray);
            holder.imageQr.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return scanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTimestamp, textZone, textSpot, textName, textAddress;
        ImageView imageQr;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
            textZone = itemView.findViewById(R.id.text_zone);
            textSpot = itemView.findViewById(R.id.text_spot);
            textName = itemView.findViewById(R.id.text_name);
            textAddress = itemView.findViewById(R.id.text_address);
            imageQr = itemView.findViewById(R.id.image_qr);
        }
    }
} 