package com.example.stenomate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stenomate.Lessons.Lesson1Activity;

public class LearningMaterialsActivity extends AppCompatActivity {

    LinearLayout LessonOneId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_materials);

        LessonOneId = findViewById(R.id.lessonOneId);

        LessonOneId.setOnClickListener(View -> {
            Intent intent = new Intent(LearningMaterialsActivity.this, Lesson1Activity.class);
            startActivity(intent);
        });





    }
}