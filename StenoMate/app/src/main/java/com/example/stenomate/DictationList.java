package com.example.stenomate;

import android.annotation.SuppressLint;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stenomate.Sqlite.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DictationList extends AppCompatActivity {

    MyDatabaseHelper dbHelper;
    ImageView BackIcon;
    ArrayList<Integer> GroupItemNumberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation_list);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        Intent get_intent = getIntent();
        String lesson_type = get_intent.getStringExtra("lesson_type");


        PopulateAssessmentItemGroup();

        BackIcon = findViewById(R.id.backIcon);

        BackIcon.setOnClickListener(view -> {
            Intent intent = new Intent(DictationList.this, DictationCategory.class);
            startActivity(intent);
        });

        dbHelper = new MyDatabaseHelper(this);

        // Get lessons with percentage >= 75
        Set<Integer> passedLessons = getPassedLessonNumbers();


        if (lesson_type.equals("Short")){
            // Generate the lesson cards
            generateLessonsLinearLayout(passedLessons, 1, 23);
        } else if (lesson_type.equals("Advance")){
            // Generate the lesson cards
            generateLessonsLinearLayout(passedLessons, 24, 45);
        }

    }

    private Set<Integer> getPassedLessonNumbers() {
        Set<Integer> passedLessons = new HashSet<>();
        Cursor cursor = dbHelper.getAllDictations();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int lessonNumber = cursor.getInt(cursor.getColumnIndexOrThrow("dictation_lesson_number"));
                int lesson_group_number = cursor.getInt(cursor.getColumnIndexOrThrow("dictation_lesson_group_number"));

                //Toast.makeText(this, "LessonNumber: " + lessonNumber + "LessonGroupNumber: " + lesson_group_number, Toast.LENGTH_SHORT).show();

                if (GroupItemNumberList.get(lessonNumber - 1) == lesson_group_number) {
                    passedLessons.add(lessonNumber + 1);
                }
//                if (lessonNumber == lesson_number) {
//                    passedLessons.add(lesson_group_number + 1);
//                }
                //passedLessons.add(lessonNumber + 1);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return passedLessons;
    }

    private void generateLessonsLinearLayout(Set<Integer> passedLessons, int startIndex, int endIndex) {
        LinearLayout parentLayout = findViewById(R.id.parentLinearLayout);

        for (int i = startIndex; i <= endIndex; i++) {
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
            innerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.dirty_white_shape_1));
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
                    Intent intent = new Intent(DictationList.this, DictationListItemGroup.class);
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DictationList.this, DictationCategory.class);
        startActivity(intent);
    }
}