package com.example.qrcodegenerator;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QrGeneratorActivity extends AppCompatActivity {

    MaterialButton btnSaveQr, btnShareQr;
    ImageView qrImage;
    Bitmap lastBitmap;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generator);

        btnSaveQr = findViewById(R.id.btnSaveQr);
        btnShareQr = findViewById(R.id.btnShareQr);
        qrImage = findViewById(R.id.qrImage);

        // Get name, address, zone, and spot from Intent
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        String zone = getIntent().getStringExtra("zone");
        String spot = getIntent().getStringExtra("spot");
        if (name == null) name = "";
        if (address == null) address = "";
        if (zone == null) zone = "";
        if (spot == null) spot = "";
        String qrData = "Name:" + name + ";Address:" + address + ";Zone:" + zone + ";Spot:" + spot;
        generateAndShowQr(qrData);

        btnSaveQr.setOnClickListener(v -> {
            if (lastBitmap == null) {
                Toast.makeText(this, "Generate a QR code first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                saveQrToGallery();
            }
        });

        btnShareQr.setOnClickListener(v -> {
            if (lastBitmap == null) {
                Toast.makeText(this, "Generate a QR code first", Toast.LENGTH_SHORT).show();
                return;
            }
            shareQrImage();
        });
    }

    private void generateAndShowQr(String qrData) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            lastBitmap = barcodeEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 400, 400);
            qrImage.setImageBitmap(lastBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveQrToGallery() {
        String fileName = "QR_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".png";
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRGenerator");
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri == null) throw new IOException("Failed to create new MediaStore record.");
                fos = getContentResolver().openOutputStream(uri);
            } else {
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "QRGenerator");
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, fileName);
                fos = new FileOutputStream(file);
                // Notify gallery
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                sendBroadcast(intent);
            }
            if (lastBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
                Toast.makeText(this, "QR saved to gallery", Toast.LENGTH_SHORT).show();
            }
            if (fos != null) fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareQrImage() {
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "qr_share.png");
            FileOutputStream stream = new FileOutputStream(file);
            lastBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to share QR", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveQrToGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
} 