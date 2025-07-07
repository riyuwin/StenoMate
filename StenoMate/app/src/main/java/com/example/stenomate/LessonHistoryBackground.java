package com.example.stenomate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LessonHistoryBackground extends AppCompatActivity {
    LinearLayout LinearContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_history_background);

        LinearContainer = findViewById(R.id.linearContainer);

        LinearContainer.setOnClickListener(View -> {
            Intent intent = new Intent(LessonHistoryBackground.this, LearningMaterialsActivity.class);
            startActivity(intent);
        });

    }
}