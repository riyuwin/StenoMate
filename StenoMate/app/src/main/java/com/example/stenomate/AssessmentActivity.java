package com.example.stenomate;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stenomate.Sqlite.MyDatabaseHelper;

import java.sql.Time;
import java.util.ArrayList;

public class AssessmentActivity extends AppCompatActivity {

    ImageView ImageHolder;
    private Dialog countdownDialog;
    private TextView countdownTextView;
    TextView TimerNoId;
    private CountDownTimer countDownTimer;
    LinearLayout TranslationKeyLinear, AnswerLinear;
    Button SubmitBtn;
    EditText AnswerEditText;
    ArrayList<String> answersList;
    int lesson_number;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        dbHelper = new MyDatabaseHelper(this); // ✅ Initialize dbHelper here

        Intent intent = getIntent();
        lesson_number = intent.getIntExtra("lesson_number", 0);

        Toast.makeText(this, "Lesson Number: " + lesson_number, Toast.LENGTH_SHORT).show();

        TranslationKeyLinear = findViewById(R.id.translationKeyLinear);
        AnswerLinear = findViewById(R.id.answerLinear);
        ImageHolder = findViewById(R.id.stenoImageHolder);
        TimerNoId = findViewById(R.id.timerNoId);
        SubmitBtn = findViewById(R.id.submitBtn);
        AnswerEditText = findViewById(R.id.answerEditText);

        if (lesson_number == 1){
            ImageHolder.setImageResource(R.drawable.dictionary_asset);
        } else if (lesson_number == 2) {
            ImageHolder.setImageResource(R.drawable.assessment_asset);
        }

        ImageHolder.setOnClickListener(v -> {
            Dialog dialog = new Dialog(AssessmentActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_fullscreen_image);

            ImageView fullImage = dialog.findViewById(R.id.fullscreenImage);
            fullImage.setImageDrawable(ImageHolder.getDrawable());

            fullImage.setOnClickListener(view -> dialog.dismiss());
            dialog.show();
        });

        SubmitBtn.setOnClickListener(v -> {
            String answer = String.valueOf(AnswerEditText.getText());
            AnswerChecker(lesson_number, answer);
        });

        showTimerDialog();
        AnswerListGenerator();
    }

    private void showConfirmationDialog(float percentage, String answer) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to submit? This action cannot be undone.")
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    insertAssessment(percentage, answer);
                })
                .setNegativeButton("Back", (dialog, which) -> {
                    //finish(); // Exit the activity
                })
                .show();
    }

    public void insertAssessment(float percentage, String answer){
        boolean isInserted = dbHelper.insertAssessment(lesson_number, percentage, answer);
        if (isInserted) {
            Toast.makeText(this, "Assessment added in " + lesson_number, Toast.LENGTH_SHORT).show();
            showResultDialog(percentage);
        } else {
            Toast.makeText(this, "Failed to add Assessment", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResultDialog(float percentage) {
        String remarks = (percentage >= 75) ? "Status: ✅ Passed" : "Status: ❌ Failed";
        String message = "Similarity: " + percentage + "%\n" + remarks;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Assessment Complete!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("View Attempts", (dialog, which) -> {
                    // TODO: View Attempts values and restart assessment
                    showAttemptsDialog();
                })
                .setNegativeButton("Back", (dialog, which) -> {
                    finish(); // Exit the activity
                })
                .show();
    }


    private void showAttemptsDialog() {
        Cursor cursor = dbHelper.getAllAssessments(); // Get all assessments
        StringBuilder messageBuilder = new StringBuilder();

        int attemptIndex = 1;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int lessonNum = cursor.getInt(cursor.getColumnIndexOrThrow("lesson_number"));
                if (lessonNum == lesson_number) {
                    float percentage = cursor.getFloat(cursor.getColumnIndexOrThrow("percentage"));
                    String datetime = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));

                    messageBuilder.append("Attempt ").append(attemptIndex).append(":\n");
                    messageBuilder.append("• Percentage: ").append(percentage).append("%\n");
                    messageBuilder.append("• Date: ").append(datetime).append("\n\n");

                    attemptIndex++;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (attemptIndex == 1) {
            messageBuilder.append("No attempts found for this lesson.");
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("List of Attempts for Lesson " + lesson_number)
                .setMessage(messageBuilder.toString())
                .setCancelable(false)
                .setNegativeButton("Back", (dialog, which) -> {
                    Intent intent = new Intent(AssessmentActivity.this, AssessmentList.class);
                    startActivity(intent);
                    finish(); // optional: close the current activity
                })
                .show();

    }



    private void showTimerDialog() {
        countdownDialog = new Dialog(this);
        countdownDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        countdownDialog.setContentView(R.layout.dialog_countdown); // create this layout
        countdownDialog.setCancelable(false); // prevent closing manually

        countdownTextView = countdownDialog.findViewById(R.id.countdownTextView);
        countdownDialog.show();

        // Start 3-second countdown
        new CountDownTimer(3000, 1000) {
            int secondsLeft = 3;

            @Override
            public void onTick(long millisUntilFinished) {
                countdownTextView.setText("Starting in: " + secondsLeft);
                secondsLeft--;
            }

            @Override
            public void onFinish() {
                countdownDialog.dismiss();
                // Optionally start something here (e.g., start quiz)
                startTimer(1);
            }
        }.start();
    }

    private void startTimer(int minutes) {
        long millisInFuture = minutes * 60 * 1000; // Convert minutes to milliseconds

        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutesLeft = seconds / 60;
                int secondsLeft = seconds % 60;
                String timeFormatted = String.format("%02d:%02d", minutesLeft, secondsLeft);
                TimerNoId.setText("\uD83D\uDD52 " + timeFormatted);
            }

            @Override
            public void onFinish() {
                TimerNoId.setText("\uD83D\uDD52 00:00");
                // Optionally do something when timer ends
                TranslationKeyLinear.setVisibility(View.GONE);
                AnswerLinear.setVisibility(View.VISIBLE);

            }
        }.start();
    }

    public int calculateSimilarityPercentage(String input, String correctAnswer) {
        input = input.trim().toLowerCase();
        correctAnswer = correctAnswer.trim().toLowerCase();

        int maxLength = Math.max(input.length(), correctAnswer.length());
        if (maxLength == 0) return 100;

        int distance = levenshteinDistance(input, correctAnswer);
        int similarity = (int) ((1 - (double) distance / maxLength) * 100);
        return similarity;
    }

    public int levenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public void AnswerChecker(int lessonNumber, String answer) {
        int index = lessonNumber - 1;

        if (index >= 0 && index < answersList.size()) {
            String correctAnswer = answersList.get(index);
            int similarity = calculateSimilarityPercentage(answer, correctAnswer);

            if (similarity >= 90) { // you can set threshold
                //Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show();
                showConfirmationDialog(similarity, answer);
            } else {
                //Toast.makeText(this, "Wrong Answer!", Toast.LENGTH_SHORT).show();
                showConfirmationDialog(similarity, answer);
            }
        } else {
            Toast.makeText(this, "No answer found for lesson " + lessonNumber, Toast.LENGTH_SHORT).show();
        }
    }


    public void AnswerListGenerator(){
        answersList = new ArrayList<>();
        answersList.add("Hello World!");
        answersList.add("Hello World12!");
    }
}
