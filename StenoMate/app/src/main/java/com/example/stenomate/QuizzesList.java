package com.example.stenomate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuizzesList extends AppCompatActivity {

    LinearLayout LessonId1, LessonId2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes_list);

        LessonId1 = findViewById(R.id.lessonId1);
        LessonId2 = findViewById(R.id.lessonId2);

        LessonId1.setOnClickListener(View -> {
            Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
            intent.putExtra("lesson_number", 1);
            startActivity(intent);
        });

        LessonId2.setOnClickListener(View -> {
            Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
            intent.putExtra("lesson_number", 2);
            startActivity(intent);
        });


    }
}