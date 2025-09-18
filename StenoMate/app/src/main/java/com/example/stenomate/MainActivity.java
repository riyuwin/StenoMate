package com.example.stenomate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    LinearLayout LinearContainer;
    ImageView Logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        LinearContainer = findViewById(R.id.linearContainer);
        Logo = findViewById(R.id.imageView);

        LinearContainer.setOnClickListener(View -> {
            Intent intent = new Intent(MainActivity.this, MainMenu.class);
            startActivity(intent);
        });

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.trans_fade_in);
        Logo.startAnimation(fadeIn);

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

}