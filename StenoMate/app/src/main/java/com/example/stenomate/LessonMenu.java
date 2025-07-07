package com.example.stenomate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

import com.example.stenomate.Lessons.Lesson1Activity;
import com.example.stenomate.Lessons.Lesson2Activity;
import com.example.stenomate.Lessons.Lesson3Activity;

public class LessonMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_menu);

        generateLessonsLinearLayout();

    }

    private void generateLessonsLinearLayout() {
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
            innerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.dirty_white_shape));
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


}