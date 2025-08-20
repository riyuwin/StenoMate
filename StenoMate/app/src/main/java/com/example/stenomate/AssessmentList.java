package com.example.stenomate;

import android.content.Intent;
import android.database.Cursor;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.stenomate.Sqlite.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AssessmentList extends AppCompatActivity {

    MyDatabaseHelper dbHelper;
    ImageView BackIcon;
    ArrayList<Integer> GroupItemNumberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);

        PopulateAssessmentItemGroup();

        BackIcon = findViewById(R.id.backIcon);

        BackIcon.setOnClickListener(view -> {
            Intent intent = new Intent(AssessmentList.this, MainMenu.class);
            startActivity(intent);
        });

        dbHelper = new MyDatabaseHelper(this);

        // Get lessons with percentage >= 75
        Set<Integer> passedLessons = getPassedLessonNumbers();

        // Generate the lesson cards
        generateLessonsLinearLayout(passedLessons);
    }

    private Set<Integer> getPassedLessonNumbers() {
        Set<Integer> passedLessons = new HashSet<>();
        Cursor cursor = dbHelper.getAllLessonPassed();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int lessonNumber = cursor.getInt(cursor.getColumnIndexOrThrow("lesson_number"));
                int lesson_group_number = cursor.getInt(cursor.getColumnIndexOrThrow("lesson_group_number"));
                float percentage = cursor.getFloat(cursor.getColumnIndexOrThrow("percentage"));

                //Toast.makeText(this, "LessonNumber: " + lessonNumber + "LessonGroupNumber: " + lesson_group_number, Toast.LENGTH_SHORT).show();

                if (GroupItemNumberList.get(lessonNumber - 1) == lesson_group_number && percentage >= 75) {
                    passedLessons.add(lessonNumber + 1);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        return passedLessons;
    }

    private void generateLessonsLinearLayout(Set<Integer> passedLessons) {
        LinearLayout parentLayout = findViewById(R.id.parentLinearLayout);

        for (int i = 1; i <= 15; i++) {
            LinearLayout outerLayout = new LinearLayout(this);
            outerLayout.setId(View.generateViewId());
            outerLayout.setOrientation(LinearLayout.VERTICAL);
            outerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(120)
            ));
            outerLayout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
            LinearLayout.LayoutParams outerParams = (LinearLayout.LayoutParams) outerLayout.getLayoutParams();
            outerParams.setMargins(0, 0, 0, dpToPx(20));
            outerLayout.setLayoutParams(outerParams);

            // Check if lesson is passed
            boolean isPassed = passedLessons.contains(i);
            boolean isEnabled = (i == 1) || isPassed;

            // Set background depending on whether it's enabled or not
            outerLayout.setBackground(ContextCompat.getDrawable(this,
                    isEnabled ? R.drawable.gray_rect_shape : R.drawable.gray_disabled_shape));

            // Inner layout
            LinearLayout innerLayout = new LinearLayout(this);
            innerLayout.setOrientation(LinearLayout.VERTICAL);
            innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            innerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.dirty_white_shape));
            innerLayout.setGravity(Gravity.CENTER);

            // Title
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

            // Subtitle
            TextView subtitle = new TextView(this);
            subtitle.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            subtitle.setText(isEnabled ? "Read now" : "Locked");
            subtitle.setTypeface(ResourcesCompat.getFont(this, R.font.kanit_regular));
            subtitle.setTextColor(Color.parseColor("#C5000000"));
            subtitle.setLetterSpacing(0.01f);
            subtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

            // Add views
            innerLayout.addView(title);
            innerLayout.addView(subtitle);
            outerLayout.addView(innerLayout);

            // Handle enabling or disabling
            if (isEnabled) {
                int finalI = i;
                outerLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(AssessmentList.this, AssessmentListItemGroup.class);
                    intent.putExtra("lesson_number", finalI);
                    startActivity(intent);
                });
            } else {
                outerLayout.setAlpha(0.5f); // Dim locked
                outerLayout.setEnabled(false);
            }

            parentLayout.addView(outerLayout);
        }
    }


    // ✅ Converts dp to px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public void PopulateAssessmentItemGroup(){
        GroupItemNumberList = new ArrayList<>(Arrays.asList(3, 5, 1, 1, 6, 1, 1, 5, 1, 1, 5, 1, 1, 6, 1, 1, 6, 1, 1, 5, 1, 1, 6, 1, 1, 5, 1, 1, 6, 1, 1, 5, 1, 1, 6, 1, 1, 4, 1, 1, 5, 1, 1, 6));
    }

}
