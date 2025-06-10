package com.example.stenomate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    LinearLayout LinearContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearContainer = findViewById(R.id.linearContainer);

        LinearContainer.setOnClickListener(View -> {
            Intent intent = new Intent(MainActivity.this, MainMenu.class);
            startActivity(intent);
        });


    }
}