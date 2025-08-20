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
import com.example.stenomate.Lessons.Lesson2Activity;
import com.example.stenomate.Lessons.Lesson3Activity;

public class LearningMaterialsActivity extends AppCompatActivity {

    LinearLayout FoundationMenu, AdvanceMenu, HistoryMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_materials);

        FoundationMenu = findViewById(R.id.foundationMenu);
        AdvanceMenu = findViewById(R.id.advanceMenu);

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

        /*LLessonTwoId = findViewById(R.id.lessonTwoId);
        //LessonThreeId = findViewById(R.id.lessonThreeId);

        LessonOneId.setOnClickListener(View -> {
            Intent intent = new Intent(LearningMaterialsActivity.this, Lesson1Activity.class);
            startActivity(intent);
        });

        LessonTwoId.setOnClickListener(View -> {
            Intent intent = new Intent(LearningMaterialsActivity.this, Lesson2Activity.class);
            startActivity(intent);
        });*/

        /*LessonThreeId.setOnClickListener(View -> {
            Intent intent = new Intent(LearningMaterialsActivity.this, Lesson3Activity.class);
            startActivity(intent);
        });*/



    }
}