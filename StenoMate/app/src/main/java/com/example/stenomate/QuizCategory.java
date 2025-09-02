package com.example.stenomate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuizCategory extends AppCompatActivity {

    LinearLayout FoundationMenu, AdvanceMenu;

    ImageView BackIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_category);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        BackIcon = findViewById(R.id.backIcon);

        FoundationMenu = findViewById(R.id.foundationMenu);
        AdvanceMenu = findViewById(R.id.advanceMenu);

        FoundationMenu.setOnClickListener(View -> {
            Intent intent = new Intent(QuizCategory.this, QuizzesList.class);
            intent.putExtra("lesson_type", "Short");
            startActivity(intent);
        });

        AdvanceMenu.setOnClickListener(View -> {
            Intent intent = new Intent(QuizCategory.this, QuizzesList.class);
            intent.putExtra("lesson_type", "Advance");
            startActivity(intent);
        });


        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(QuizCategory.this, MainMenu.class);
            startActivity(intent);
        });



    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QuizCategory.this, MainMenu.class);
        startActivity(intent);
    }
}