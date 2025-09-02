package com.example.stenomate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
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

public class DictationListItemGroup extends AppCompatActivity {

    MyDatabaseHelper dbHelper;

    ImageView BackIcon;
    TextView LessonNumberText;
    int lesson_number;
    ArrayList<Integer> GroupItemNumberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation_list_item_group);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        PopulateAssessmentItemGroup();

        BackIcon = findViewById(R.id.backIcon);
        LessonNumberText = findViewById(R.id.assessmentLessonText);

        Intent get_intent = getIntent();
        lesson_number = get_intent.getIntExtra("lesson_number", -1);

        LessonNumberText.setText("Dictation No. " + lesson_number);


        BackIcon.setOnClickListener(view -> {
            Intent intent = new Intent(DictationListItemGroup.this, DictationList.class);
            if (lesson_number >= 1 && lesson_number <= 23){
                intent.putExtra("lesson_type", "Short");
            } else if (lesson_number >= 24 && lesson_number <= 45){
                intent.putExtra("lesson_type", "Advance");
            }
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
        Cursor cursor = dbHelper.getAllDictations();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int lessonNumber = cursor.getInt(cursor.getColumnIndexOrThrow("dictation_lesson_number"));
                int lesson_group_number = cursor.getInt(cursor.getColumnIndexOrThrow("dictation_lesson_group_number"));

                if (lessonNumber == lesson_number) {
                    passedLessons.add(lesson_group_number + 1);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        return passedLessons;
    }

    private void generateLessonsLinearLayout(Set<Integer> passedLessons) {
        LinearLayout parentLayout = findViewById(R.id.parentLinearLayout);
        int total_num_list = GroupItemNumberList.get(lesson_number - 1);

        for (int i = 1; i <= total_num_list; i++) {
            // Check if lesson is passed
            boolean isPassed = passedLessons.contains(i);
            boolean isEnabled = (i == 1) || isPassed;

            // Outer container
            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.HORIZONTAL);
            container.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            LinearLayout.LayoutParams containerParams = (LinearLayout.LayoutParams) container.getLayoutParams();
            containerParams.setMargins(0, dpToPx(10), 0, 0);
            container.setLayoutParams(containerParams);
            container.setBackground(ContextCompat.getDrawable(this,
                    isEnabled ? R.drawable.assessment_contianer : R.drawable.gray_disabled_shape));

            // Left vertical layout
            LinearLayout leftLayout = new LinearLayout(this);
            leftLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            leftParams.setMargins(dpToPx(30), dpToPx(30), 0, dpToPx(16));
            leftLayout.setLayoutParams(leftParams);

            TextView title = new TextView(this);
            title.setText("Group " + (char) ('A' + i - 1));
            title.setTypeface(ResourcesCompat.getFont(this, R.font.kanit_semibold));
            title.setTextColor(Color.BLACK);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            TextView subtitle = new TextView(this);
            subtitle.setText("Duration: 1 min");
            subtitle.setTypeface(ResourcesCompat.getFont(this, R.font.kanit_light));
            subtitle.setTextColor(Color.BLACK);
            subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

            leftLayout.addView(title);
            leftLayout.addView(subtitle);

            // Right image layout
            LinearLayout rightLayout = new LinearLayout(this);
            rightLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rightParams.setMargins(0, dpToPx(8), dpToPx(20), dpToPx(8));
            rightLayout.setLayoutParams(rightParams);

            ImageView image = new ImageView(this);
            image.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(100), dpToPx(100)));
            image.setPadding(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
            image.setImageResource(R.drawable.attempt_now_btn);
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            rightLayout.addView(image);

            // Add both sides to container
            container.addView(leftLayout);
            container.addView(rightLayout);

            if (isEnabled) {
                int finalI = i;
                container.setOnClickListener(v -> {
                    Intent intent = new Intent(DictationListItemGroup.this, DictationActivity.class);
                    intent.putExtra("lesson_number", lesson_number);
                    intent.putExtra("dictation_list_group_name", finalI);
                    startActivity(intent);
                });
            } else {
                container.setAlpha(0.5f);
                container.setEnabled(false);
            }

            parentLayout.addView(container);
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
        Intent intent = new Intent(DictationListItemGroup.this, DictationList.class);
        if (lesson_number >= 1 && lesson_number <= 23){
            intent.putExtra("lesson_type", "Short");
        } else if (lesson_number >= 24 && lesson_number <= 45){
            intent.putExtra("lesson_type", "Advance");
        }
        startActivity(intent);
    }
}