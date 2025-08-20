package com.example.stenomate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {

    LinearLayout LearningMaterialsId, DictionaryId, AssessmentId, QuizzesId;
    TextView AboutUsId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        LearningMaterialsId = findViewById(R.id.learningMaterialsId);
        DictionaryId = findViewById(R.id.dictionaryId);
        AssessmentId = findViewById(R.id.assessmentsId);
        QuizzesId = findViewById(R.id.quizId);


        AboutUsId = findViewById(R.id.aboutUsId);

        LearningMaterialsId.setOnClickListener(View -> {
            IntentManager(LessonHistoryBackground.class);
        });

        DictionaryId.setOnClickListener(View -> {
            IntentManager(DictionaryActivity.class);
        });

        AssessmentId.setOnClickListener(View -> {
            IntentManager(AssessmentList.class);
        });

        QuizzesId.setOnClickListener(View -> {
            IntentManager(QuizzesList.class);
        });

        AboutUsId.setOnClickListener(View -> {
            IntentManager(AboutUs.class);
        });
    }

    public void IntentManager(Class<?> activityName) {
        Intent intent = new Intent(MainMenu.this, activityName);
        startActivity(intent);
    }

}