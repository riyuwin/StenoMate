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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stenomate.Lessons.Lesson10Activity;
import com.example.stenomate.Lessons.Lesson11Activity;
import com.example.stenomate.Lessons.Lesson12Activity;
import com.example.stenomate.Lessons.Lesson13Activity;
import com.example.stenomate.Lessons.Lesson14Activity;
import com.example.stenomate.Lessons.Lesson15Activity;
import com.example.stenomate.Lessons.Lesson16Activity;
import com.example.stenomate.Lessons.Lesson17Activity;
import com.example.stenomate.Lessons.Lesson18Activity;
import com.example.stenomate.Lessons.Lesson19Activity;
import com.example.stenomate.Lessons.Lesson1Activity;
import com.example.stenomate.Lessons.Lesson20Activity;
import com.example.stenomate.Lessons.Lesson21Activity;
import com.example.stenomate.Lessons.Lesson22Activity;
import com.example.stenomate.Lessons.Lesson24Activity;
import com.example.stenomate.Lessons.Lesson25Activity;
import com.example.stenomate.Lessons.Lesson26Activity;
import com.example.stenomate.Lessons.Lesson27Activity;
import com.example.stenomate.Lessons.Lesson28Activity;
import com.example.stenomate.Lessons.Lesson29Activity;
import com.example.stenomate.Lessons.Lesson2Activity;
import com.example.stenomate.Lessons.Lesson30Activity;
import com.example.stenomate.Lessons.Lesson32Activity;
import com.example.stenomate.Lessons.Lesson33Activity;
import com.example.stenomate.Lessons.Lesson34Activity;
import com.example.stenomate.Lessons.Lesson36Activity;
import com.example.stenomate.Lessons.Lesson37Activity;
import com.example.stenomate.Lessons.Lesson38Activity;
import com.example.stenomate.Lessons.Lesson39Activity;
import com.example.stenomate.Lessons.Lesson3Activity;
import com.example.stenomate.Lessons.Lesson40Activity;
import com.example.stenomate.Lessons.Lesson41Activity;
import com.example.stenomate.Lessons.Lesson42Activity;
import com.example.stenomate.Lessons.Lesson43Activity;
import com.example.stenomate.Lessons.Lesson4Activity;
import com.example.stenomate.Lessons.Lesson5Activity;
import com.example.stenomate.Lessons.Lesson6Activity;
import com.example.stenomate.Lessons.Lesson7Activity;
import com.example.stenomate.Lessons.Lesson8Activity;
import com.example.stenomate.Lessons.Lesson9Activity;

public class LessonMenu extends AppCompatActivity {

    ImageView BackIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_menu);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        BackIcon = findViewById(R.id.backIcon);

        Intent intent = getIntent();
        String lesson_type = intent.getStringExtra("lesson_type");


        if (lesson_type.equals("Short")){
            generateFoundationShortHandLessonsLinearLayout();
        } else if (lesson_type.equals("Advance")){
            generateAdvancedShortHandLessonsLinearLayout();
        } else if (lesson_type.equals("Specialized")){
            generateSpecializedHandLessonsLinearLayout();
        }

        BackIcon.setOnClickListener(v -> {
            Intent nav_intent = new Intent(LessonMenu.this, LearningMaterialsActivity.class);
            startActivity(nav_intent);
        });
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
                    Intent intent = new Intent(LessonMenu.this, Lesson1Activity.class);
                    startActivity(intent);
                } if (finalI == 2) {
                    Intent intent = new Intent(LessonMenu.this, Lesson2Activity.class);
                    startActivity(intent);
                } if (finalI == 3) {
                    Intent intent = new Intent(LessonMenu.this, Lesson3Activity.class);
                    startActivity(intent);
                } if (finalI == 4) {
                    Intent intent = new Intent(LessonMenu.this, Lesson4Activity.class);
                    startActivity(intent);
                } if (finalI == 5) {
                    Intent intent = new Intent(LessonMenu.this, Lesson5Activity.class);
                    startActivity(intent);
                } if (finalI == 6) {
                    Intent intent = new Intent(LessonMenu.this, Lesson6Activity.class);
                    startActivity(intent);
                } if (finalI == 7) {
                    Intent intent = new Intent(LessonMenu.this, Lesson7Activity.class);
                    startActivity(intent);
                } if (finalI == 8) {
                    Intent intent = new Intent(LessonMenu.this, Lesson8Activity.class);
                    startActivity(intent);
                } if (finalI == 9) {
                    Intent intent = new Intent(LessonMenu.this, Lesson9Activity.class);
                    startActivity(intent);
                } if (finalI == 10) {
                    Intent intent = new Intent(LessonMenu.this, Lesson10Activity.class);
                    startActivity(intent);
                } if (finalI == 11) {
                    Intent intent = new Intent(LessonMenu.this, Lesson11Activity.class);
                    startActivity(intent);
                } if (finalI == 12) {
                    Intent intent = new Intent(LessonMenu.this, Lesson12Activity.class);
                    startActivity(intent);
                } if (finalI == 13) {
                    Intent intent = new Intent(LessonMenu.this, Lesson13Activity.class);
                    startActivity(intent);
                } if (finalI == 14) {
                    Intent intent = new Intent(LessonMenu.this, Lesson14Activity.class);
                    startActivity(intent);
                } if (finalI == 15) {
                    Intent intent = new Intent(LessonMenu.this, Lesson15Activity.class);
                    startActivity(intent);
                }
            });


            // Add to parent
            parentLayout.addView(outerLayout);
        }
    }

    private void generateAdvancedShortHandLessonsLinearLayout() {
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
                    Intent intent = new Intent(LessonMenu.this, Lesson16Activity.class);
                    startActivity(intent);
                } if (finalI == 17) {
                    Intent intent = new Intent(LessonMenu.this, Lesson17Activity.class);
                    startActivity(intent);
                } if (finalI == 18) {
                    Intent intent = new Intent(LessonMenu.this, Lesson18Activity.class);
                    startActivity(intent);
                } if (finalI == 19) {
                    Intent intent = new Intent(LessonMenu.this, Lesson19Activity.class);
                    startActivity(intent);
                } if (finalI == 20) {
                    Intent intent = new Intent(LessonMenu.this, Lesson20Activity.class);
                    startActivity(intent);
                } if (finalI == 21) {
                    Intent intent = new Intent(LessonMenu.this, Lesson21Activity.class);
                    startActivity(intent);
                } if (finalI == 22) {
                    Intent intent = new Intent(LessonMenu.this, Lesson22Activity.class);
                    startActivity(intent);
                } if (finalI == 23) {
                    Intent intent = new Intent(LessonMenu.this, Lesson32Activity.class);
                    startActivity(intent);
                } if (finalI == 24) {
                    Intent intent = new Intent(LessonMenu.this, Lesson24Activity.class);
                    startActivity(intent);
                } if (finalI == 25) {
                    Intent intent = new Intent(LessonMenu.this, Lesson25Activity.class);
                    startActivity(intent);
                } if (finalI == 26) {
                    Intent intent = new Intent(LessonMenu.this, Lesson26Activity.class);
                    startActivity(intent);
                } if (finalI == 27) {
                    Intent intent = new Intent(LessonMenu.this, Lesson27Activity.class);
                    startActivity(intent);
                } if (finalI == 28) {
                    Intent intent = new Intent(LessonMenu.this, Lesson28Activity.class);
                    startActivity(intent);
                } if (finalI == 29) {
                    Intent intent = new Intent(LessonMenu.this, Lesson29Activity.class);
                    startActivity(intent);
                } if (finalI == 30) {
                    Intent intent = new Intent(LessonMenu.this, Lesson30Activity.class);
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
                    Intent intent = new Intent(LessonMenu.this, Lesson13Activity.class);
                    startActivity(intent);
                } if (finalI == 32) {
                    Intent intent = new Intent(LessonMenu.this, Lesson32Activity.class);
                    startActivity(intent);
                } if (finalI == 33) {
                    Intent intent = new Intent(LessonMenu.this, Lesson33Activity.class);
                    startActivity(intent);
                } if (finalI == 34) {
                    Intent intent = new Intent(LessonMenu.this, Lesson34Activity.class);
                    startActivity(intent);
                } if (finalI == 35) {
                    Intent intent = new Intent(LessonMenu.this, Lesson19Activity.class);
                    startActivity(intent);
                } if (finalI == 36) {
                    Intent intent = new Intent(LessonMenu.this, Lesson36Activity.class);
                    startActivity(intent);
                } if (finalI == 37) {
                    Intent intent = new Intent(LessonMenu.this, Lesson37Activity.class);
                    startActivity(intent);
                } if (finalI == 38) {
                    Intent intent = new Intent(LessonMenu.this, Lesson38Activity.class);
                    startActivity(intent);
                } if (finalI == 39) {
                    Intent intent = new Intent(LessonMenu.this, Lesson39Activity.class);
                    startActivity(intent);
                } if (finalI == 40) {
                    Intent intent = new Intent(LessonMenu.this, Lesson40Activity.class);
                    startActivity(intent);
                } if (finalI == 41) {
                    Intent intent = new Intent(LessonMenu.this, Lesson41Activity.class);
                    startActivity(intent);
                } if (finalI == 42) {
                    Intent intent = new Intent(LessonMenu.this, Lesson42Activity.class);
                    startActivity(intent);
                } if (finalI == 43) {
                    Intent intent = new Intent(LessonMenu.this, Lesson43Activity.class);
                    startActivity(intent);
                } if (finalI == 44) {
                    // wala pa
                } if (finalI == 45) {
                    // wala pa
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
        Intent nav_intent = new Intent(LessonMenu.this, LearningMaterialsActivity.class);
        startActivity(nav_intent);
    }


}