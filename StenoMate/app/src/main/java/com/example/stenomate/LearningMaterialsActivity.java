package com.example.stenomate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stenomate.Lessons.Lesson1Activity;
import com.example.stenomate.Lessons.Lesson2Activity;
import com.example.stenomate.Lessons.Lesson3Activity;

public class LearningMaterialsActivity extends AppCompatActivity {

    LinearLayout FoundationMenu, AdvanceMenu, HistoryMenu, SpecializedMenu;

    ImageView BackIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_materials);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        BackIcon = findViewById(R.id.backIcon);

        FoundationMenu = findViewById(R.id.foundationMenu);
        AdvanceMenu = findViewById(R.id.advanceMenu);
        HistoryMenu = findViewById(R.id.historyMenu);
        SpecializedMenu = findViewById(R.id.specializedMenu);

        HistoryMenu.setOnClickListener(View -> {
            Intent intent = new Intent(LearningMaterialsActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        FoundationMenu.setOnClickListener(View -> {
            Intent intent = new Intent(LearningMaterialsActivity.this, LessonMenu.class);
            intent.putExtra("lesson_type", "Short");
            startActivity(intent);
        });

        AdvanceMenu.setOnClickListener(View -> {
            Intent intent = new Intent(LearningMaterialsActivity.this, LessonMenu.class);
            intent.putExtra("lesson_type", "Advance");
            startActivity(intent);
        });

        SpecializedMenu.setOnClickListener(View -> {
            Intent intent = new Intent(LearningMaterialsActivity.this, LessonMenu.class);
            intent.putExtra("lesson_type", "Specialized");
            startActivity(intent);
        });


        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(LearningMaterialsActivity.this, MainMenu.class);
            startActivity(intent);
        });



    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LearningMaterialsActivity.this, MainMenu.class);
        startActivity(intent);
    }

}