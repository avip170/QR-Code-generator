package com.example.qrcodegenerator;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class QrScannerActivity extends AppCompatActivity {
    private DecoratedBarcodeView barcodeView;
    private TextView textResult;
    private SharedPreferences sharedPreferences;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    barcodeView.resume();
                } else {
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        barcodeView = findViewById(R.id.barcode_scanner);
        textResult = findViewById(R.id.text_result);
        sharedPreferences = getSharedPreferences("qr_scans", Context.MODE_PRIVATE);

        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    barcodeView.pause();
                    handleResult(result.getText());
                }
            }
        });

        checkCameraPermission();
        showLastScan();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void handleResult(String qrText) {
        // Example: "Name:John;Address:123 Main St;Zone:B;Spot:17"
        String name = "";
        String address = "";
        String zone = "";
        String spot = "";
        for (String part : qrText.split(";")) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                if (keyValue[0].equalsIgnoreCase("Name")) {
                    name = keyValue[1];
                } else if (keyValue[0].equalsIgnoreCase("Address")) {
                    address = keyValue[1];
                } else if (keyValue[0].equalsIgnoreCase("Zone")) {
                    zone = keyValue[1];
                } else if (keyValue[0].equalsIgnoreCase("Spot")) {
                    spot = keyValue[1];
                }
            }
        }
        String display = "Name: " + name + "\n" +
                         "Address: " + address + "\n" +
                         "Zone: " + zone + "\n" +
                         "Spot: " + spot;
        textResult.setText(display);

        // Save to SharedPreferences
        sharedPreferences.edit()
                .putString("last_name", name)
                .putString("last_address", address)
                .putString("last_zone", zone)
                .putString("last_spot", spot)
                .apply();

        // Save to history as JSON array
        try {
            String historyJson = sharedPreferences.getString("scan_history", "[]");
            JSONArray historyArray = new JSONArray(historyJson);
            JSONObject scanObj = new JSONObject();
            scanObj.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            scanObj.put("name", name);
            scanObj.put("address", address);
            scanObj.put("zone", zone);
            scanObj.put("spot", spot);
            // Re-generate QR image from scanned text
            String fileName = "qr_" + System.currentTimeMillis() + ".png";
            java.io.File dir = new java.io.File(QrScannerActivity.this.getFilesDir(), "qr_history");
            if (!dir.exists()) dir.mkdirs();
            java.io.File file = new java.io.File(dir, fileName);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                android.graphics.Bitmap qrBitmap = barcodeEncoder.encodeBitmap(qrText, BarcodeFormat.QR_CODE, 400, 400);
                if (qrBitmap != null) {
                    qrBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
            fos.close();
            scanObj.put("qrImagePath", file.getAbsolutePath());
            historyArray.put(scanObj);
            sharedPreferences.edit().putString("scan_history", historyArray.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
    }

    private void showLastScan() {
        String zone = sharedPreferences.getString("last_zone", "");
        String spot = sharedPreferences.getString("last_spot", "");
        if (!zone.isEmpty() && !spot.isEmpty()) {
            textResult.setText("Zone: " + zone + "\nSpot: " + spot);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
} 