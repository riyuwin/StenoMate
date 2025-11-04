package com.example.stenomate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stenomate.Sqlite.MyDatabaseHelper;

public class QuizzesGuide extends AppCompatActivity {

    LinearLayout LinearContainer;
    public int indexNumber = 0;

    ImageView GuideImageHolder;
    TextView HeaderText, ContentText;

    int lesson_number, assessment_list_group_name;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes_guide);


        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


        dbHelper = new MyDatabaseHelper(this);
        /*boolean isInserted = dbHelper.insertAssessment(3, 75, "Hello World");
        if (isInserted) {
            Toast.makeText(this, "Assessment added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add Assessment", Toast.LENGTH_SHORT).show();
        }*/

        Intent intent = getIntent();
        lesson_number = intent.getIntExtra("lesson_number", 0);

        LinearContainer = findViewById(R.id.linearContainer);

        GuideImageHolder = findViewById(R.id.imageView);
        HeaderText = findViewById(R.id.headerText);
        ContentText = findViewById(R.id.contentText);

        LinearContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexNumber == 0) {
                    indexNumber++;
                    HeaderText.setText("Practice Time! ⏳");
                    ContentText.setText("You have unlimited time to answer the questions, please select the correct answer in the given choices.");
                    GuideImageHolder.setImageResource(R.drawable.quizzes_no_bg_stroke);

                } else if (indexNumber == 1) {
                    indexNumber++;

                    HeaderText.setText("Best of luck! \uD83D\uDE80");
                    ContentText.setText("When you're ready, hit the button to begin the challenge. Remember, you cannot go back once the quiz has started. Good luck!");
                    GuideImageHolder.setImageResource(R.drawable.loading_steno_dictation_no_bg_stroke);

                } else if (indexNumber == 2) {
                    Intent intent = new Intent(QuizzesGuide.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", lesson_number);
                    startActivity(intent);
                }
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }
}