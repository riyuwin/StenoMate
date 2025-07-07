package com.example.stenomate;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class QuizzesActivity extends AppCompatActivity {

    Button NextBtn;
    ImageView ImageHolder;
    LinearLayout Answer1Id, Answer2Id, Answer3Id, Answer4Id;
    TextView QuestionNoText, RemarksText;

    List<AnswerListItem> answerItemList;
    List<AnswerListItem> lessonItemList;
    int LessonNumber;
    int CurrentQuestionNumber = 1;
    int NumberCorrectAnswer = 0;
    int TotalQuestionNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes);
        LessonNumber = getIntent().getIntExtra("lesson_number", 0);

        ImageHolder = findViewById(R.id.stenoImageHolder);
        Answer1Id = findViewById(R.id.answer1Id);
        Answer2Id = findViewById(R.id.answer2Id);
        Answer3Id = findViewById(R.id.answer3Id);
        Answer4Id = findViewById(R.id.answer4Id);
        NextBtn = findViewById(R.id.nextBtn);

        QuestionNoText = findViewById(R.id.questionNoId);
        RemarksText = findViewById(R.id.remarksId);

        PopulateDictionary();
        AssessmentIntiliazer(LessonNumber);
        showConfirmationDialog();
    }

    public void AssessmentIntiliazer(int lessonNumber) {
        ImageHolder.setOnClickListener(v -> {
            Dialog dialog = new Dialog(QuizzesActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_fullscreen_image);

            ImageView fullImage = dialog.findViewById(R.id.fullscreenImage);
            fullImage.setImageDrawable(ImageHolder.getDrawable());

            fullImage.setOnClickListener(view -> dialog.dismiss());
            dialog.show();
        });

        lessonItemList = new ArrayList<>();
        for (AnswerListItem item : answerItemList) {
            if (item.getLessonNumber() == lessonNumber) {
                lessonItemList.add(item);
            }
        }

        TotalQuestionNumber = lessonItemList.size();

        if (!lessonItemList.isEmpty() && (CurrentQuestionNumber - 1) < TotalQuestionNumber) {
            AnswerListItem currentItem = lessonItemList.get(CurrentQuestionNumber - 1);

            ImageHolder.setImageResource(currentItem.getImageResId());
            ((TextView) Answer1Id.getChildAt(0)).setText(currentItem.getChoiceA());
            ((TextView) Answer2Id.getChildAt(0)).setText(currentItem.getChoiceB());
            ((TextView) Answer3Id.getChildAt(0)).setText(currentItem.getChoiceC());
            ((TextView) Answer4Id.getChildAt(0)).setText(currentItem.getChoiceD());

            QuestionNoText.setText("Question " + CurrentQuestionNumber + " of " + TotalQuestionNumber);
        } else {
            // Quiz complete
            showResultDialog();
            return;


            //Toast.makeText(this, "Assessment complete! Correct answers: " + NumberCorrectAnswer + "/" + TotalQuestionNumber, Toast.LENGTH_LONG).show();
            //finish(); // or go to another screen
            //return;
        }

        Answer1Id.setOnClickListener(View -> AnswerChecker(lessonNumber, CurrentQuestionNumber, "Answer 1"));
        Answer2Id.setOnClickListener(View -> AnswerChecker(lessonNumber, CurrentQuestionNumber, "Answer 2"));
        Answer3Id.setOnClickListener(View -> AnswerChecker(lessonNumber, CurrentQuestionNumber, "Answer 3"));
        Answer4Id.setOnClickListener(View -> AnswerChecker(lessonNumber, CurrentQuestionNumber, "Answer 4"));
    }

    public void AnswerChecker(int lessonNumber, int currentPosition, String answerName) {
        for (AnswerListItem item : answerItemList) {
            if (item.getLessonNumber() == lessonNumber && item.getQuestionNumber() == currentPosition) {
                if (item.getAnswerKey().equals(answerName)) {
                    RemarksText.setText("Correct Answer");
                    highlightAnswer(item.getAnswerKey(), R.drawable.correct_answer_container);
                    NumberCorrectAnswer++;
                } else {
                    RemarksText.setText("Wrong Answer");
                    highlightAnswer(answerName, R.drawable.wrong_answer_container);
                    highlightAnswer(item.getAnswerKey(), R.drawable.correct_answer_container);
                }
                break;
            }
        }

        DisableButtons();
        NextBtn.setVisibility(View.VISIBLE);

        NextBtn.setOnClickListener(view -> {
            CurrentQuestionNumber++;
            ResetElements();
            EnableButtons();
            AssessmentIntiliazer(LessonNumber);
            NextBtn.setVisibility(View.GONE);
        });
    }

    private void highlightAnswer(String answerKey, int backgroundRes) {
        switch (answerKey) {
            case "Answer 1":
                Answer1Id.setBackgroundResource(backgroundRes);
                break;
            case "Answer 2":
                Answer2Id.setBackgroundResource(backgroundRes);
                break;
            case "Answer 3":
                Answer3Id.setBackgroundResource(backgroundRes);
                break;
            case "Answer 4":
                Answer4Id.setBackgroundResource(backgroundRes);
                break;
        }
    }

    public void ResetElements() {
        NextBtn.setVisibility(View.INVISIBLE);
        RemarksText.setText("");
        Answer1Id.setBackgroundResource(R.drawable.answer_container);
        Answer2Id.setBackgroundResource(R.drawable.answer_container);
        Answer3Id.setBackgroundResource(R.drawable.answer_container);
        Answer4Id.setBackgroundResource(R.drawable.answer_container);
    }

    public void DisableButtons() {
        Answer1Id.setEnabled(false);
        Answer2Id.setEnabled(false);
        Answer3Id.setEnabled(false);
        Answer4Id.setEnabled(false);
    }

    public void EnableButtons() {
        Answer1Id.setEnabled(true);
        Answer2Id.setEnabled(true);
        Answer3Id.setEnabled(true);
        Answer4Id.setEnabled(true);
    }

    public void PopulateDictionary() {
        answerItemList = new ArrayList<>();
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_3_say, 1, 1, "Answer 2", "saves", "say", "seam", "sane"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_5_safe, 1, 2, "Answer 1", "safe", "saves", "say", "sane"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_6_face, 1, 3, "Answer 4", "fees", "fee", "fay", "face"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_50_fay, 1, 4, "Answer 3", "face", "fee", "fees", "fay"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_8_save, 1, 5, "Answer 2", "save", "vase", "saves", "vases"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_3_say, 2, 1, "Answer 2", "saves123", "say123", "seam123", "sane123"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_5_safe, 2, 2, "Answer 1", "safe123", "saves123", "say123", "sane123"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_6_face, 2, 3, "Answer 4", "fees", "fee", "fay", "face"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_50_fay, 2, 4, "Answer 3", "face", "fee", "fees", "fay"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_8_save, 2, 5, "Answer 2", "save", "vase", "saves", "vases"));
    }

    private void showConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Start Assessment")
                .setMessage("Are you ready to start the quiz for Lesson " + LessonNumber + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    AssessmentIntiliazer(LessonNumber); // Start the quiz
                })
                .setNegativeButton("No", (dialog, which) -> {
                    finish(); // Exit the activity
                })
                .show();
    }

    private void showResultDialog() {
        int wrongAnswers = TotalQuestionNumber - NumberCorrectAnswer;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Assessment Complete!")
                .setMessage("Your Score:\nCorrect: " + NumberCorrectAnswer + "\nWrong: " + wrongAnswers)
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> {
                    // Reset values and restart assessment
                    CurrentQuestionNumber = 1;
                    NumberCorrectAnswer = 0;
                    AssessmentIntiliazer(LessonNumber);
                })
                .setNegativeButton("Back", (dialog, which) -> {
                    finish(); // Exit the activity
                })
                .show();
    }


    @Override
    public void onBackPressed() {
        // Reset all variables
        CurrentQuestionNumber = 1;
        NumberCorrectAnswer = 0;
        LessonNumber = 0;

        super.onBackPressed();
    }
}