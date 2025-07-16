package com.example.qrcodegenerator;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class QrImageViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        setContentView(imageView);
        String imagePath = getIntent().getStringExtra("qrImagePath");
        if (imagePath != null && !imagePath.isEmpty()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }
} 