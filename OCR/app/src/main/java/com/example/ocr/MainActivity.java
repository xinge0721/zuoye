package com.example.ocr;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
            if (OpenCVLoader.initDebug()) {
                Log.d("opencv","ok");
            } else {
                // Handle initialization error
                Toast.makeText(MainActivity.this, "Error loading OpenCV", Toast.LENGTH_SHORT).show();
            }
        }
}