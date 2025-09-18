package com.example.stenomate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stenomate.Sqlite.MyDatabaseHelper;

public class AssessmentGuide extends AppCompatActivity {
    LinearLayout LinearContainer;
    public int indexNumber = 0;

    ImageView GuideImageHolder;
    TextView HeaderText, ContentText;

    int lesson_number, assessment_list_group_name;
    MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_guide);
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
        assessment_list_group_name = intent.getIntExtra("assessment_list_group_name", 0);

        LinearContainer = findViewById(R.id.linearContainer);

        GuideImageHolder = findViewById(R.id.imageView);
        HeaderText = findViewById(R.id.headerText);
        ContentText = findViewById(R.id.contentText);

        LinearContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexNumber == 0) {
                    indexNumber++;

                    HeaderText.setText("Your Time is Ticking! ⏳");
                    ContentText.setText("Once you start the assessment, you will be given a time limit to complete it. When the time is up, the image will be hidden, and you will need to type your answer in the provided field.");
                    GuideImageHolder.setImageResource(R.drawable.assessment_element);

                } else if (indexNumber == 1) {
                    indexNumber++;

                    HeaderText.setText("Best of luck! \uD83D\uDE80");
                    ContentText.setText("When you're ready, hit the button and let the challenge begin. Goodluck!");
                    GuideImageHolder.setImageResource(R.drawable.dictation_element);

                } else if (indexNumber == 2) {
                    Intent intent = new Intent(AssessmentGuide.this, AssessmentActivity.class);
                    intent.putExtra("lesson_number", lesson_number);
                    intent.putExtra("assessment_list_group_name", assessment_list_group_name);
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
