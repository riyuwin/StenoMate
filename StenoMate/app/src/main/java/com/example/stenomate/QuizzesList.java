package com.example.stenomate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public class QuizzesList extends AppCompatActivity {

    ImageView BackIcon;
    LinearLayout LessonId1, LessonId2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes_list);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        BackIcon = findViewById(R.id.backIcon);

        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(QuizzesList.this, QuizCategory.class);
            startActivity(intent);
        });


        Intent intent = getIntent();
        String lesson_type = intent.getStringExtra("lesson_type");


        if (lesson_type.equals("Short")){
            generateFoundationShortHandLessonsLinearLayout();
        } else if (lesson_type.equals("Advance")){
            generateAdvanceHandLessonsLinearLayout();
        } else if (lesson_type.equals("Specialized")){
            generateSpecializedHandLessonsLinearLayout();
        }


    }

    private void generateFoundationShortHandLessonsLinearLayout() {
        LinearLayout parentLayout = findViewById(R.id.parentLinearLayout); // Replace with your actual parent container

        for (int i = 1; i <= 15; i++) {
            // Outer container
            LinearLayout outerLayout = new LinearLayout(this);
            outerLayout.setId(View.generateViewId());
            outerLayout.setOrientation(LinearLayout.VERTICAL);
            outerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(120)
            ));
            outerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_rect_shape));
            outerLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            LinearLayout.LayoutParams outerParams = (LinearLayout.LayoutParams) outerLayout.getLayoutParams();
            outerParams.setMargins(0, 0, 0, dpToPx(20));
            outerLayout.setLayoutParams(outerParams);

            // Inner container
            LinearLayout innerLayout = new LinearLayout(this);
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            innerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.dirty_white_shape_1));
            innerLayout.setGravity(Gravity.CENTER);

            // Title TextView
            TextView title = new TextView(this);
            title.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            title.setText("Lesson " + i);
            title.setTypeface(ResourcesCompat.getFont(this, R.font.kanit_bold));
            title.setTextColor(Color.parseColor("#C5000000"));
            title.setLetterSpacing(0.01f);
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

            // Subtitle TextView
            TextView subtitle = new TextView(this);
            subtitle.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            subtitle.setText("Read now");
            subtitle.setTypeface(ResourcesCompat.getFont(this, R.font.kanit_regular));
            subtitle.setTextColor(Color.parseColor("#C5000000"));
            subtitle.setLetterSpacing(0.01f);
            subtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

            // Add title and subtitle to inner layout
            innerLayout.addView(title);
            innerLayout.addView(subtitle);

            // Add inner layout to outer layout
            outerLayout.addView(innerLayout);
            // Optional: add click listener
            int finalI = i;
            outerLayout.setOnClickListener(v -> {
                if (finalI == 1) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 1);
                    startActivity(intent);
                } if (finalI == 2) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 2);
                    startActivity(intent);
                } if (finalI == 3) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 3);
                    startActivity(intent);
                } if (finalI == 4) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 4);
                    startActivity(intent);
                } if (finalI == 5) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 5);
                    startActivity(intent);
                } if (finalI == 6) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 6);
                    startActivity(intent);
                } if (finalI == 7) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 7);
                    startActivity(intent);
                } if (finalI == 8) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 8);
                    startActivity(intent);
                } if (finalI == 9) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 9);
                    startActivity(intent);
                } if (finalI == 10) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 10);
                    startActivity(intent);
                } if (finalI == 11) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 11);
                    startActivity(intent);
                } if (finalI == 12) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 12);
                    startActivity(intent);
                } if (finalI == 13) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 13);
                    startActivity(intent);
                } if (finalI == 14) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 14);
                    startActivity(intent);
                } if (finalI == 15) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 15);
                    startActivity(intent);
                }
            });


            // Add to parent
            parentLayout.addView(outerLayout);
        }
    }

    private void generateAdvanceHandLessonsLinearLayout() {
        LinearLayout parentLayout = findViewById(R.id.parentLinearLayout); // Replace with your actual parent container

        for (int i = 16; i <= 30; i++) {
            // Outer container
            LinearLayout outerLayout = new LinearLayout(this);
            outerLayout.setId(View.generateViewId());
            outerLayout.setOrientation(LinearLayout.VERTICAL);
            outerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(120)
            ));
            outerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_rect_shape));
            outerLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            LinearLayout.LayoutParams outerParams = (LinearLayout.LayoutParams) outerLayout.getLayoutParams();
            outerParams.setMargins(0, 0, 0, dpToPx(20));
            outerLayout.setLayoutParams(outerParams);

            // Inner container
            LinearLayout innerLayout = new LinearLayout(this);
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            innerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.dirty_white_shape_1));
            innerLayout.setGravity(Gravity.CENTER);

            // Title TextView
            TextView title = new TextView(this);
            title.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            title.setText("Lesson " + i);
            title.setTypeface(ResourcesCompat.getFont(this, R.font.kanit_bold));
            title.setTextColor(Color.parseColor("#C5000000"));
            title.setLetterSpacing(0.01f);
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

            // Subtitle TextView
            TextView subtitle = new TextView(this);
            subtitle.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            subtitle.setText("Read now");
            subtitle.setTypeface(ResourcesCompat.getFont(this, R.font.kanit_regular));
            subtitle.setTextColor(Color.parseColor("#C5000000"));
            subtitle.setLetterSpacing(0.01f);
            subtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

            // Add title and subtitle to inner layout
            innerLayout.addView(title);
            innerLayout.addView(subtitle);

            // Add inner layout to outer layout
            outerLayout.addView(innerLayout);
            // Optional: add click listener
            int finalI = i;
            outerLayout.setOnClickListener(v -> {
                if (finalI == 16) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 16);
                    startActivity(intent);
                } if (finalI == 17) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 17);
                    startActivity(intent);
                } if (finalI == 18) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 18);
                    startActivity(intent);
                } if (finalI == 19) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 19);
                    startActivity(intent);
                } if (finalI == 20) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 20);
                    startActivity(intent);
                } if (finalI == 21) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 21);
                    startActivity(intent);
                } if (finalI == 22) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 22);
                    startActivity(intent);
                } if (finalI == 23) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 23);
                    startActivity(intent);
                } if (finalI == 24) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 24);
                    startActivity(intent);
                } if (finalI == 25) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 25);
                    startActivity(intent);
                } if (finalI == 26) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 26);
                    startActivity(intent);
                } if (finalI == 27) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 27);
                    startActivity(intent);
                } if (finalI == 28) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 28);
                    startActivity(intent);
                } if (finalI == 29) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 29);
                    startActivity(intent);
                } if (finalI == 30) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 30);
                    startActivity(intent);
                }
            });


            // Add to parent
            parentLayout.addView(outerLayout);
        }
    }


    private void generateSpecializedHandLessonsLinearLayout() {
        LinearLayout parentLayout = findViewById(R.id.parentLinearLayout); // Replace with your actual parent container

        for (int i = 31; i <= 45; i++) {
            // Outer container
            LinearLayout outerLayout = new LinearLayout(this);
            outerLayout.setId(View.generateViewId());
            outerLayout.setOrientation(LinearLayout.VERTICAL);
            outerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(120)
            ));
            outerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_rect_shape));
            outerLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            LinearLayout.LayoutParams outerParams = (LinearLayout.LayoutParams) outerLayout.getLayoutParams();
            outerParams.setMargins(0, 0, 0, dpToPx(20));
            outerLayout.setLayoutParams(outerParams);

            // Inner container
            LinearLayout innerLayout = new LinearLayout(this);
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            innerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.dirty_white_shape_1));
            innerLayout.setGravity(Gravity.CENTER);

            // Title TextView
            TextView title = new TextView(this);
            title.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            title.setText("Lesson " + i);
            title.setTypeface(ResourcesCompat.getFont(this, R.font.kanit_bold));
            title.setTextColor(Color.parseColor("#C5000000"));
            title.setLetterSpacing(0.01f);
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

            // Subtitle TextView
            TextView subtitle = new TextView(this);
            subtitle.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            subtitle.setText("Read now");
            subtitle.setTypeface(ResourcesCompat.getFont(this, R.font.kanit_regular));
            subtitle.setTextColor(Color.parseColor("#C5000000"));
            subtitle.setLetterSpacing(0.01f);
            subtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

            // Add title and subtitle to inner layout
            innerLayout.addView(title);
            innerLayout.addView(subtitle);

            // Add inner layout to outer layout
            outerLayout.addView(innerLayout);
            // Optional: add click listener
            int finalI = i;
            outerLayout.setOnClickListener(v -> {
                 if (finalI == 31) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 31);
                    startActivity(intent);
                } if (finalI == 32) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 32);
                    startActivity(intent);
                } if (finalI == 33) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 33);
                    startActivity(intent);
                } if (finalI == 34) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 34);
                    startActivity(intent);
                } if (finalI == 35) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 35);
                    startActivity(intent);
                } if (finalI == 36) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 36);
                    startActivity(intent);
                } if (finalI == 37) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 37);
                    startActivity(intent);
                } if (finalI == 38) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 38);
                    startActivity(intent);
                } if (finalI == 39) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 39);
                    startActivity(intent);
                } if (finalI == 40) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 40);
                    startActivity(intent);
                } if (finalI == 41) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 41);
                    startActivity(intent);
                } if (finalI == 42) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 42);
                    startActivity(intent);
                } if (finalI == 43) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 43);
                    startActivity(intent);
                } if (finalI == 44) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 44);
                    startActivity(intent);
                } if (finalI == 45) {
                    Intent intent = new Intent(QuizzesList.this, QuizzesActivity.class);
                    intent.putExtra("lesson_number", 45);
                    startActivity(intent);
                }
            });


            // Add to parent
            parentLayout.addView(outerLayout);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(QuizzesList.this, QuizCategory.class);
        startActivity(intent);
    }

}