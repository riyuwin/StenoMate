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
import java.util.Arrays;
import java.util.List;

public class QuizzesActivity extends AppCompatActivity {

    Button NextBtn;
    ImageView ImageHolder;
    LinearLayout Answer1Id, Answer2Id, Answer3Id, Answer4Id;
    TextView QuestionNoText, RemarksText;

    List<AnswerListItem> answerItemList;
    List<AnswerListItem> lessonItemList;

    List<AnswerKeyListItems> answerKeyListItems;

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
        answerKeyListItems = new ArrayList<>();

        // Lesson 1
        ArrayList<String> LessonQuizList1 = new ArrayList<>(Arrays.asList(
                "say", "safe", "face", "safes", "save", "vase", "saves", "see", "sees", "ease",
                "vain", "seen", "sane", "knees", "may", "mean", "seem", "eat", "meet", "stay",
                "neat", "tea", "safety", "aid", "feed", "stayed", "made", "date", "deed", "need",
                "date", "saved", "period", "paragraph", "parenthesis", "?", "-", "-", "dave", "fay",
                "mae", "fee", "fees", "easy"
        ));

        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_3_say, 1, 1, "say"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_5_safe, 1, 2, "safe"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_6_face, 1, 3, "face"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_7_safes, 1, 4, "safes"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_8_save, 1, 5, "save"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_9_vase, 1, 6, "vase"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_10_saves, 1, 7, "saves"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_14_see, 1, 8, "see"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_15_sees, 1, 9, "sees"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_16_ease, 1, 10, "ease"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_20_vain, 1, 11, "vain"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_21_seen, 1, 12, "seen"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_22_sane, 1, 13, "sane"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_23_knee, 1, 14, "knees"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_24_may, 1, 15, "may"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_25_mean, 1, 16, "may"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_26_seem, 1, 17, "seem"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_28_eat, 1, 18, "eat"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_29_meet, 1, 19, "meet"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_30_stay, 1, 20, "stay"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_31_neat, 1, 21, "neat"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_32_tea, 1, 22, "tea"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_33_safety, 1, 23, "safety"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_34_aid, 1, 24, "aid"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_35_feed, 1, 25, "feed"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_36_stayed, 1, 26, "stayed"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_37_made, 1, 27, "made"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_38_day, 1, 28, "date"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_39_deed, 1, 29, "deed"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_40_need, 1, 30, "need"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_41_date, 1, 31, "date"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_42_saved, 1, 32, "saved"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_43_period, 1, 33, "period"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_44_paragraph, 1, 34, "paragraph"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_45_parentheses, 1, 35, "parenthesis"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_46_question_mark, 1, 36, "?"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_47_dash, 1, 37, "-"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_48_hyphen, 1, 38, "-"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_49_dave, 1, 39, "dave"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_50_fay, 1, 40, "fay"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_51_mae, 1, 41, "mae"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_52_fee, 1, 42, "fee"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_53_fees, 1, 43, "fees"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson1_asset_54_easy, 1, 44, "easy"));


        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_3_say, 1, 1, "Answer 2", "saves", "say", "seam", "sane"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_5_safe, 1, 2, "Answer 1", "safe", "saves", "say", "sane"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_6_face, 1, 3, "Answer 4", "fees", "fee", "fay", "face"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_7_safes, 1, 4, "Answer 1", "safes", "fee", "fay", "face"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_8_save, 1, 5, "Answer 1", "save", "vase", "saves", "vases"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_9_vase, 1, 6, "Answer 2", "save", "vase", "saves", "vases"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_10_saves, 1, 7, "Answer 3", "save", "vase", "saves", "vases"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_14_see, 1, 8, "Answer 1", "see", "fay", "ease", "seam"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_15_sees, 1, 9, "Answer 4", "see", "fay", "ease", "sees"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_16_ease, 1, 10, "Answer 3", "see", "fay", "ease", "sees"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_20_vain, 1, 11, "Answer 1", "vain", "vase", "saves", "vases"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_21_seen, 1, 12, "Answer 2", "vain", "seen", "seee", "vases"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_22_sane, 1, 13, "Answer 1", "sane", "seen", "seee", "sees"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_23_knee, 1, 14, "Answer 4", "save", "fay", "knee", "knees"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_24_may, 1, 15, "Answer 3", "make", "maze", "may", "vase"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_25_mean, 1, 16, "Answer 3", "make", "mean", "may", "meal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_26_seem, 1, 17, "Answer 1", "seem", "see", "saw", "seal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_28_eat, 1, 18, "Answer 2", "ate", "eat", "meal", "seal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_29_meet, 1, 19, "Answer 1", "meet", "meat", "meal", "seal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_30_stay, 1, 20, "Answer 1", "stay", "steal", "skate", "seal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_31_neat, 1, 21, "Answer 3", "meet", "knee", "neat", "seal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_32_tea, 1, 22, "Answer 2", "meet", "tea", "teal", "take"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_33_safety, 1, 23, "Answer 4", "sail", "seal", "safe", "safety"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_34_aid, 1, 24, "Answer 1", "aid", "maid", "ate", "eat"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_35_feed, 1, 25, "Answer 2", "feel", "feed", "feet", "fear"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_36_stayed, 1, 26, "Answer 3", "steal", "stay", "stayed", "fear"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_37_made, 1, 27, "Answer 1", "made", "meal", "meat", "male"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_38_day, 1, 28, "Answer 2", "day", "date", "deal", "dim"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_39_deed, 1, 29, "Answer 4", "day", "date", "dim", "deed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_40_need, 1, 30, "Answer 1", "need", "nail", "neat", "near"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_41_date, 1, 31, "Answer 1", "date", "dim", "deed", "day"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_42_saved, 1, 32, "Answer 2", "save", "saved", "saver", "seal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_43_period, 1, 33, "Answer 4", "pale", "pierce", "peel", "period"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_44_paragraph, 1, 34, "Answer 3", "para", "pierce", "paragraph", "parenthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_45_parentheses, 1, 35, "Answer 4", "para", "pierce", "paragraph", "parenthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_46_question_mark, 1, 36, "Answer 1", "?", "-", "+", "."));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_47_dash, 1, 37, "Answer 2", "?", "-", "+", "."));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_48_hyphen, 1, 38, "Answer 2", "?", "-", "+", "."));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_49_dave, 1, 39, "Answer 3", "deer", "dale", "dave", "dame"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_50_fay, 1, 40, "Answer 1", "fay", "faye", "fale", "fays"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_51_mae, 1, 41, "Answer 2", "mean", "mae", "man", "meek"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_52_fee, 1, 42, "Answer 4", "feel", "fate", "fear", "fee"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_53_fees, 1, 43, "Answer 3", "feel", "fate", "fees", "fees"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson1_asset_54_easy, 1, 44, "Answer 1", "easy", "ease", "east", "eat"));

        ArrayList<String> LessonQuizList2 = new ArrayList<>(Arrays.asList(
                "o", "r", "l", "no", "so", "own", "tow", "phone", "stone", "dough",
                "vote", "dome", "ear", "raid", "fear", "near", "trade", "fair", "mere", "or",
                "radio", "dear", "more", "freed", "all", "lay", "real", "mail", "late", "leave",
                "deal", "feel", "low", "steal", "fail", "floor", "freed", "he", "home", "whole",
                "hearing", "heating", "mailing", "i", "me", "sight", "hi", "might", "side", "try",
                "sign", "line", "tire", "evening", "writer", "season", "meter", "vital", "total",
                "dealer", "final", "heater"
        ));

        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_2_o, 2, 1, "o"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_3_r, 2, 2, "r"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_4_l, 2, 3, "l"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_5_no, 2, 4, "no"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_6_so, 2, 5, "so"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_7_own, 2, 6, "own"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_8_tow, 2, 7, "tow"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_9_phone, 2, 8, "phone"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_10_stone, 2, 9, "stone"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_11_dough, 2, 10, "dough"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_12_vote, 2, 11, "vote"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_13_dome, 2, 12, "dome"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_14_ear, 2, 13, "ear"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_15_raid, 2, 14, "raid"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_16_fear, 2, 15, "fear"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_17_near, 2, 16, "near"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_18_trade, 2, 17, "trade"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_19_fair, 2, 18, "fair"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_20_mere, 2, 19, "mere"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_21_or, 2, 20, "or"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_22_radio, 2, 21, "radio"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_23_dear, 2, 22, "dear"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_24_more, 2, 23, "more"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_25_freed, 2, 24, "freed"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_26_all, 2, 25, "all"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_27_lay, 2, 26, "lay"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_28_real, 2, 27, "real"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_29_mail, 2, 28, "mail"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_30_late, 2, 29, "late"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_31_leave, 2, 30, "leave"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_32_deal, 2, 31, "deal"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_33_feel, 2, 32, "feel"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_34_low, 2, 33, "low"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_35_steal, 2, 34, "steal"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_36_fail, 2, 35, "fail"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_37_floor, 2, 36, "floor"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_38_freed, 2, 37, "freed"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_40_he, 2, 38, "he"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_41_home, 2, 39, "home"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_42_whole, 2, 40, "whole"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_43_hearing, 2, 41, "hearing"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_44_heating, 2, 42, "heating"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_45_mailing, 2, 43, "mailing"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_46_i, 2, 44, "i"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_47_my, 2, 45, "my"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_48_sight, 2, 46, "sight"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_49_hi, 2, 47, "hi"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_50_might, 2, 48, "might"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_51_side, 2, 49, "side"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_52_try, 2, 50, "try"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_53_sign, 2, 51, "sign"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_54_line, 2, 52, "line"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_55_tire, 2, 53, "tire"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_56_evening, 2, 54, "evening"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_57_writer, 2, 55, "writer"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_58_season, 2, 56, "season"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_59_meter, 2, 57, "meter"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_60_vital, 2, 58, "vital"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_61_total, 2, 59, "total"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_62_dealer, 2, 60, "dealer"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_63_final, 2, 61, "final"));
        answerKeyListItems.add(new AnswerKeyListItems(R.drawable.lesson2_asset_64_heater, 2, 62, "heater"));


        // Lesson 2
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_2_o, 2, 1, "Answer 3", "orl", "r", "o", "l"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_3_r, 2, 2, "Answer 2", "orl", "r", "o", "l"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_4_l, 2, 3, "Answer 4", "orl", "r", "o", "l"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_5_no, 2, 4, "Answer 2", "yes", "no", "or", "so"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_6_so, 2, 5, "Answer 4", "yes", "no", "or", "so"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_7_own, 2, 6, "Answer 1", "own", "tow", "no", "not"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_8_tow, 2, 7, "Answer 2", "own", "tow", "no", "phone"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_9_phone, 2, 8, "Answer 4", "own", "tow", "stone", "phone"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_10_stone, 2, 9, "Answer 3", "own", "tow", "stone", "phone"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_11_dough, 2, 10, "Answer 1", "dough", "vote", "doughnut", "dome"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_12_vote, 2, 11, "Answer 2", "dough", "vote", "doughnut", "dome"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_13_dome, 2, 12, "Answer 4", "dough", "ear", "doughnut", "dome"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_14_ear, 2, 13, "Answer 2", "dough", "ear", "doughnut", "dome"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_15_raid, 2, 14, "Answer 1", "raid", "ear", "fear", "dome"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_16_fear, 2, 15, "Answer 3", "raid", "ear", "fear", "dome"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_17_near, 2, 16, "Answer 4", "raid", "ear", "fear", "near"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_18_trade, 2, 17, "Answer 1", "trade", "fair", "fear", "near"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_19_fair, 2, 18, "Answer 2", "trade", "fair", "fear", "near"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_20_mere, 2, 19, "Answer 3", "trade", "fair", "mere", "near"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_21_or, 2, 20, "Answer 2", "orl", "or", "o", "l"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_22_radio, 2, 21, "Answer 1", "radio", "tv", "rad", "l"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_23_dear, 2, 22, "Answer 4", "deer", "dare", "date", "dear"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_24_more, 2, 23, "Answer 3", "deer", "most", "more", "dear"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_25_freed, 2, 24, "Answer 1", "freed", "fried", "fry", "free"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_26_all, 2, 25, "Answer 1", "all", "art", "ate", "alt"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_27_lay, 2, 26, "Answer 2", "lad", "lay", "lat", "let"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_28_real, 2, 27, "Answer 4", "rail", "rate", "reel", "real"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_29_mail, 2, 28, "Answer 3", "late", "male", "mail", "meat"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_30_late, 2, 29, "Answer 1", "late", "male", "mail", "meat"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_31_leave, 2, 30, "Answer 2", "left", "leave", "mail", "meat"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_32_deal, 2, 31, "Answer 3", "date", "deed", "deal", "dim"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_33_feel, 2, 32, "Answer 1", "feel", "felt", "fail", "feed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_34_low, 2, 33, "Answer 4", "leg", "high", "let", "low"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_35_steal, 2, 34, "Answer 1", "steal", "steel", "state", "stunt"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_36_fail, 2, 35, "Answer 3", "freed", "floor", "fail", "faint"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_37_floor, 2, 36, "Answer 2", "freed", "floor", "fail", "faint"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_38_freed, 2, 37, "Answer 1", "freed", "floor", "fail", "faint"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_40_he, 2, 38, "Answer 1", "he", "home", "her", "his"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_41_home, 2, 39, "Answer 2", "he", "home", "whole", "his"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_42_whole, 2, 40, "Answer 3", "he", "home", "whole", "hearing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_43_hearing, 2, 41, "Answer 3", "he", "home", "whole", "hearing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_44_heating, 2, 42, "Answer 3", "heating", "mailing", "beating", "hearing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_45_mailing, 2, 43, "Answer 2", "heating", "mailing", "beating", "hearing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_46_i, 2, 44, "Answer 3", "a", "e", "i", "o"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_47_my, 2, 45, "Answer 1", "me", "my", "he", "she"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_48_sight, 2, 46, "Answer 1", "sight", "tight", "height", "weight"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_49_hi, 2, 47, "Answer 2", "she", "hi", "her", "he"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_50_might, 2, 48, "Answer 1", "might", "night", "sight", "he"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_51_side, 2, 49, "Answer 3", "hide", "night", "side", "he"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_52_try, 2, 50, "Answer 2", "tried", "try", "side", "tire"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_53_sign, 2, 51, "Answer 4", "tried", "try", "side", "sign"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_54_line, 2, 52, "Answer 2", "mine", "line", "nine", "sine"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_55_tire, 2, 53, "Answer 1", "tire", "tired", "tried", "try"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_56_evening, 2, 54, "Answer 3", "tom", "eve", "evening", "morning"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_57_writer, 2, 55, "Answer 2", "write", "writer", "wrote", "read"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_58_season, 2, 56, "Answer 4", "send", "sail", "seed", "season"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_59_meter, 2, 57, "Answer 1", "meter", "mail", "meet", "meal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_60_vital, 2, 58, "Answer 1", "vital", "virtual", "vale", "valid"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_61_total, 2, 59, "Answer 2", "totaled", "total", "totality", "valid"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_62_dealer, 2, 60, "Answer 3", "date", "dart", "dealer", "deal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_63_final, 2, 61, "Answer 1", "final", "feet", "faith", "fail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson2_asset_64_heater, 2, 62, "Answer 1", "heater", "heat", "heats", "hail"));

        // Lesson 3
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_3_have, 3, 1, "Answer 1", "have", "has", "it", "am"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_4_it, 3, 2, "Answer 3", "have", "has", "it", "am"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_5_am, 3, 3, "Answer 4", "have", "has", "it", "am"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_6_will, 3, 4, "Answer 1", "will", "well", "in", "am"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_7_in, 3, 5, "Answer 3", "will", "well", "in", "mr"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_8_mr, 3, 6, "Answer 4", "will", "well", "in", "mr"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_9_an, 3, 7, "Answer 1", "an", "in", "on", "and"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_10_are, 3, 8, "Answer 1", "are", "is", "there", "and"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_11_indeed, 3, 9, "Answer 3", "will", "well", "indeed", "and"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_12_inside, 3, 10, "Answer 2", "will", "inside", "indeed", "invite"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_13_invite, 3, 11, "Answer 4", "will", "inside", "indeed", "invite"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_14_ihave, 3, 12, "Answer 1", "i have", "i has", "i had", "i will have"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_15_iwillhave, 3, 13, "Answer 4", "i have", "i am", "i had", "i will have"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_16_iam, 3, 14, "Answer 2", "i have", "i am", "i have not", "i will have"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_17_ihavenot, 3, 15, "Answer 3", "i have", "i am", "i have not", "i will have"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_18_hewill, 3, 16, "Answer 1", "he will", "she will", "i will", "in our"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_19_inour, 3, 17, "Answer 4", "he will", "she will", "i will", "in our"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_20_iwill, 3, 18, "Answer 3", "he will", "she will", "i will", "in our"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_21_hewillnot, 3, 19, "Answer 1", "he will not", "she will", "i will", "in our"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_22_itwill, 3, 20, "Answer 1", "he will not", "i will", "it will", "in our"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_24_needs, 3, 21, "Answer 4", "require", "required", "need", "needs"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_25_least, 3, 22, "Answer 3", "lead", "leader", "least", "atleast"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_26_series, 3, 23, "Answer 3", "lead", "leader", "least", "atleast"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_27_names, 3, 24, "Answer 1", "names", "name", "least", "atleast"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_28_writes, 3, 25, "Answer 4", "right", "wrote", "write", "writes"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_29_seal, 3, 26, "Answer 3", "reals", "real", "seal", "sealed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_30_rise, 3, 27, "Answer 2", "rail", "rise", "raid", "raise"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_31_mails, 3, 28, "Answer 1", "mails", "mail", "mill", "mills"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_32_sales, 3, 29, "Answer 1", "sales", "sale", "sail", "sailor"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_33_most, 3, 30, "Answer 4", "mall", "mode", "more", "most"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_34_seems, 3, 31, "Answer 2", "seem", "seems", "same", "sail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_35_homes, 3, 32, "Answer 1", "homes", "home", "hail", "fame"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_37_hope, 3, 33, "Answer 3", "homes", "home", "hope", "hopes"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_38_space, 3, 34, "Answer 2", "pace", "spaces", "space", "pacing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_39_paper, 3, 35, "Answer 2", "pace", "paper", "papers", "pacing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_40_open, 3, 36, "Answer 1", "open", "opener", "papers", "pacing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_41_please, 3, 37, "Answer 4", "provide", "papers", "pace", "please"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_42_provide, 3, 38, "Answer 3", "home", "papers", "provide", "please"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_43_pay, 3, 39, "Answer 3", "home", "paid", "pay", "please"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_44_place, 3, 40, "Answer 1", "peel", "paid", "pay", "place"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_45_prepare, 3, 41, "Answer 2", "prepared", "prepare", "pay", "place"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_47_price, 3, 42, "Answer 2", "pricey", "price", "pride", "prides"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_48_tip, 3, 43, "Answer 1", "tip", "top", "tap", "tape"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_49_bay, 3, 44, "Answer 1", "bay", "bat", "bad", "bell"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_50_buy, 3, 45, "Answer 2", "bad", "buy", "bought", "bed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_51_able, 3, 46, "Answer 3", "led", "table", "able", "bed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_52_base, 3, 47, "Answer 1", "base", "based", "bare", "biased"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_53_bare, 3, 48, "Answer 3", "base", "based", "bare", "biased"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_54_labor, 3, 49, "Answer 4", "base", "based", "bare", "labor"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_55_blame, 3, 50, "Answer 2", "label", "blame", "flame", "flare"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_56_label, 3, 51, "Answer 1", "label", "blame", "flame", "flare"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_57_boat, 3, 52, "Answer 1", "boat", "boar", "flame", "flare"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_58_blame, 3, 53, "Answer 4", "boat", "boar", "label", "blame"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_59_label, 3, 54, "Answer 4", "boat", "boar", "flame", "label"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_61_bright, 3, 55, "Answer 2", "right", "bright", "flame", "label"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson3_asset_62_neighbor, 3, 56, "Answer 4", "right", "bright", "flame", "neighbor"));


        // Lesson 4
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_3_to, 4, 1, "Answer 1", "to", "at", "in", "on"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_4_suit, 4, 2, "Answer 2", "to", "suit", "sue", "on"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_5_produce, 4, 3, "Answer 4", "prove", "provided", "provide", "produce"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_6_do, 4, 4, "Answer 1", "do", "to", "provide", "produce"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_7_room, 4, 5, "Answer 3", "role", "roll", "room", "road"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_8_new, 4, 5, "Answer 3", "role", "need", "new", "now"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_9_who, 4, 6, "Answer 3", "where", "whom", "who", "whose"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_10_poor, 4, 7, "Answer 1", "poor", "pool", "pole", "pat"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_11_noon, 4, 8, "Answer 1", "noon", "food", "mole", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_12_food, 4, 9, "Answer 2", "noon", "food", "mole", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_13_true, 4, 10, "Answer 4", "noon", "food", "false", "true"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_14_move, 4, 11, "Answer 1", "move", "more", "mode", "mole"));answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_15_we, 4, 15, "Answer 1", "we", "ido", "swear", "scale"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_16_wade, 4, 16, "Answer 3", "legal", "swear", "wade", "wheel"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_17_sweet, 4, 17, "Answer 3", "wehave", "make", "sweet", "wear"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_18_way, 4, 18, "Answer 2", "week", "way", "ido", "gate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_19_wear, 4, 19, "Answer 3", "weare", "case", "wear", "goal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_20_sway, 4, 20, "Answer 4", "came", "wife", "wheel", "sway"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_21_wait, 4, 21, "Answer 3", "clean", "great", "wait", "grade"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_22_wife, 4, 22, "Answer 1", "wife", "cake", "take", "ido"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_23_swear, 4, 23, "Answer 3", "goal", "we", "swear", "wewill"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_24_why, 4, 24, "Answer 3", "white", "cool", "why", "glue"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_25_white, 4, 25, "Answer 1", "white", "whale", "case", "make"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_26_whale, 4, 26, "Answer 3", "weare", "wade", "whale", "cool"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_27_wheel, 4, 27, "Answer 3", "wheel", "game", "goal", "gate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_28_wheat, 4, 28, "Answer 2", "cake", "wheat", "legal", "wade"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_29_weare, 4, 29, "Answer 3", "week", "whowill", "weare", "claim"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_30_wemay, 4, 30, "Answer 3", "take", "case", "wemay", "clean"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_31_whowillnot, 4, 31, "Answer 4", "glue", "claim", "cake", "whowillnot"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_32_wewill, 4, 32, "Answer 1", "wewill", "scale", "wheat", "ido"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_33_whoare, 4, 33, "Answer 3", "came", "take", "whoare", "make"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_34_ido, 4, 34, "Answer 3", "white", "wheel", "ido", "cake"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_35_wehave, 4, 35, "Answer 1", "wehave", "case", "cake", "cool"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_36_whowill, 4, 36, "Answer 4", "whowill", "glue", "goal", "whowill"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_37_idonot, 4, 37, "Answer 1", "idonot", "glue", "white", "week"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_39_take, 4, 38, "Answer 1", "take", "scale", "goal", "glue"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_40_week, 4, 39, "Answer 1", "week", "claim", "make", "cool"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_41_clear, 4, 40, "Answer 1", "clear", "case", "cake", "goal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_42_cake, 4, 41, "Answer 1", "cake", "go", "claim", "goal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_43_cool, 4, 42, "Answer 1", "cool", "we", "came", "scale"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_44_increase, 4, 43, "Answer 4", "wade", "case", "goal", "increase"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_45_make, 4, 44, "Answer 1", "make", "cake", "week", "goal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_46_case, 4, 45, "Answer 1", "case", "take", "cake", "glue"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_47_claim, 4, 46, "Answer 1", "claim", "scale", "make", "clear"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_48_came, 4, 47, "Answer 3", "take", "cake", "came", "make"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_49_scale, 4, 48, "Answer 4", "claim", "gate", "cool", "scale"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_50_clean, 4, 49, "Answer 2", "clear", "clean", "scale", "make"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_51_gain, 4, 50, "Answer 1", "gain", "cool", "we", "scale"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_52_go, 4, 51, "Answer 2", "make", "go", "clear", "cake"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_53_gale, 4, 52, "Answer 2", "case", "gale", "scale", "goal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_54_game, 4, 53, "Answer 2", "glue", "game", "goal", "cake"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_55_goal, 4, 54, "Answer 4", "came", "claim", "cake", "goal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_56_glue, 4, 55, "Answer 3", "scale", "case", "glue", "make"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_57_gate, 4, 56, "Answer 3", "scale", "cake", "gate", "goal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_58_great, 4, 57, "Answer 2", "goal", "great", "scale", "gate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_59_legal, 4, 58, "Answer 4", "cake", "clear", "cool", "legal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_60_gave, 4, 59, "Answer 2", "gate", "gave", "go", "game"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_61_grade, 4, 60, "Answer 1", "grade", "gate", "cake", "glue"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson4_asset_62_gleam, 4, 61, "Answer 3", "goal", "scale", "gleam", "glue"));

        // Lesson 5
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_2_as, 5, 1, "Answer 1", "as", "man", "past", "has"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_3_man, 5, 1, "Answer 2", "as", "man", "past", "has"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_4_past, 5, 1, "Answer 3", "as", "man", "past", "has"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_5_has, 5, 1, "Answer 4", "as", "man", "past", "has"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_6_back, 5, 1, "Answer 3", "plan", "black", "back", "act"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_7_plan, 5, 1, "Answer 1", "plan", "black", "back", "act"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_8_act, 5, 1, "Answer 4", "plan", "black", "back", "act"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_9_matter, 5, 1, "Answer 3", "plan", "butter", "matter", "act"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_10_swarm, 5, 1, "Answer 1", "swarm", "swap", "map", "rap"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_11_arm, 5, 1, "Answer 4", "male", "man", "chair", "arm"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_12_car, 5, 1, "Answer 1", "car", "rack", "rat", "cat"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_13_start, 5, 1, "Answer 1", "start", "stat", "state", "stack"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_14_once, 5, 1, "Answer 4", "nice", "ice", "on", "once"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_15_far, 5, 1, "Answer 1", "far", "few", "near", "ago"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_16_dark, 5, 1, "Answer 2", "bark", "dark", "dog", "start"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_17_heart, 5, 1, "Answer 4", "hat", "heal", "heat", "heart"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_18_farm, 5, 1, "Answer 4", "fast", "fail", "fat", "farm"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_19_park, 5, 1, "Answer 1", "park", "pale", "pat", "pair"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_20_let, 5, 1, "Answer 4", "lame", "lane", "let", "led"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_21_get, 5, 1, "Answer 3", "gale", "gaze", "get", "got"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_22_said, 5, 1, "Answer 4", "set", "sail", "sale", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_23_letter, 5, 1, "Answer 2", "let", "letter", "better", "bail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_24_head, 5, 1, "Answer 4", "heat", "hat", "hot", "head"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_25_sell, 5, 1, "Answer 3", "sale", "sat", "sell", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_27_help, 5, 1, "Answer 1", "as", "to", "in", "on"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_29_best, 5, 1, "Answer 2", "bait", "best", "better", "more"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_30_less, 5, 1, "Answer 4", "nest", "sell", "few", "less"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_31_test, 5, 1, "Answer 2", "tale", "test", "tail", "tame"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_32_him, 5, 1, "Answer 4", "her", "she", "had", "him"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_33_did, 5, 1, "Answer 2", "do", "did", "dig", "had"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_34_if, 5, 1, "Answer 4", "fade", "fail", "fit", "if"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_35_bill, 5, 1, "Answer 3", "bed", "bet", "bill", "bad"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_36_fill, 5, 1, "Answer 1", "fill", "filled", "failed", "fail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_37_list, 5, 1, "Answer 1", "list", "listed", "last", "lasted"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_38_big, 5, 1, "Answer 2", "small", "smaller", "big", "bigger"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_39_give, 5, 1, "Answer 3", "given", "giver", "give", "gave"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_40_simple, 5, 1, "Answer 4", "maple", "ample", "sample", "simple"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_41_her, 5, 1, "Answer 1", "her", "she", "his", "he"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_42_clerk, 5, 1, "Answer 1", "clerk", "club", "claim", "cake"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_43_answer, 5, 1, "Answer 2", "ant", "answer", "about", "and"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_44_hurt, 5, 1, "Answer 4", "hat", "heed", "hale", "hurt"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_45_serve, 5, 1, "Answer 2", "save", "serve", "said", "sail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_46_insert, 5, 1, "Answer 4", "inside", "outside", "over", "insert"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_47_earn, 5, 1, "Answer 4", "yield", "govern", "learn", "earn"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_48_learn, 5, 1, "Answer 3", "yield", "govern", "learn", "earn"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_49_infer, 5, 1, "Answer 4", "teeth", "theater", "these", "infer"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_50_these, 5, 1, "Answer 3", "teeth", "theater", "these", "infer"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_51_theather, 5, 1, "Answer 2", "teeth", "theater", "these", "infer"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_52_teeth, 5, 1, "Answer 1", "teeth", "theater", "these", "infer"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_53_then, 5, 1, "Answer 2", "thick", "then", "faith", "theme"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_54_thick, 5, 1, "Answer 1", "thick", "then", "faith", "theme"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_55_faith, 5, 1, "Answer 3", "thick", "then", "faith", "theme"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_56_theme, 5, 1, "Answer 4", "thief", "then", "faith", "theme"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_57_thief, 5, 1, "Answer 1", "thief", "then", "faith", "theme"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_58_both, 5, 1, "Answer 1", "both", "health", "those", "birth"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_59_health, 5, 1, "Answer 2", "both", "health", "those", "birth"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_60_those, 5, 1, "Answer 3", "both", "health", "those", "birth"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_61_birth, 5, 1, "Answer 4", "both", "health", "those", "birth"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_62_thorough, 5, 1, "Answer 2", "thought", "thorough", "though", "route"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_63_three, 5, 1, "Answer 1", "three", "teeth", "meet", "earth"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_64_earth, 5, 1, "Answer 4", "three", "teeth", "meet", "earth"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_65_through, 5, 1, "Answer 1", "through", "teeth", "meet", "butter"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_66_you, 5, 1, "Answer 1", "you", "i", "he", "she"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_67_is, 5, 1, "Answer 2", "as", "is", "in", "on"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_68_that, 5, 1, "Answer 3", "these", "this", "that", "those"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_69_can, 5, 1, "Answer 1", "can", "mrs", "of", "with"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_70_mrs, 5, 1, "Answer 2", "can", "mrs", "of", "with"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_71_with, 5, 1, "Answer 4", "can", "mrs", "of", "with"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_72_of, 5, 1, "Answer 3", "can", "mrs", "of", "with"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_73_but, 5, 1, "Answer 1", "but", "bad", "ball", "bald"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_74_inthe, 5, 1, "Answer 4", "as", "to", "in", "in the"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_75_itis, 5, 1, "Answer 3", "as", "to", "it is", "it"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_76_inthat, 5, 1, "Answer 1", "in that", "to", "in", "on"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_77_ican, 5, 1, "Answer 1", "i can", "to", "in", "on"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_78_inhis, 5, 1, "Answer 2", "as", "in his", "in", "on"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_79_youare, 5, 1, "Answer 2", "as", "you are", "at", "on"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_80_icannot, 5, 1, "Answer 3", "as", "to", "i cannot", "on"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson5_asset_81_withhis, 5, 1, "Answer 4", "as", "to", "in", "with this"));


        // Lesson 6
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_2_appeal, 6, 1, "Answer 3", "same", "late", "appeal", "give"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_3_late, 6, 2, "Answer 2", "same", "late", "appeal", "give"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_4_same, 6, 3, "Answer 1", "same", "late", "appeal", "give"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_5_give, 6, 4, "Answer 4", "same", "late", "appeal", "give"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_6_relief, 6, 5, "Answer 4", "same", "late", "appeal", "relief"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_9_may, 6, 6, "Answer 1", "may", "date", "stayed", "name"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_10_date, 6, 7, "Answer 2", "may", "date", "stayed", "name"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_11_aim, 6, 8, "Answer 2", "may", "aim", "stayed", "man"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_12_man, 6, 9, "Answer 4", "may", "date", "stayed", "man"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_13_stayed, 6, 10, "Answer 3", "may", "date", "stayed", "man"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_14_name, 6, 11, "Answer 4", "may", "date", "stayed", "name"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_15_care, 6, 12, "Answer 1", "care", "can", "car", "creed"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_16_gear, 6, 13, "Answer 2", "gale", "gear", "give", "gave"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_17_vapor, 6, 14, "Answer 3", "vast", "valid", "vapor", "vital"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_18_rock, 6, 15, "Answer 4", "own", "pave", "lake", "rock"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_19_lake, 6, 16, "Answer 3", "own", "pave", "lake", "rock"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_20_pave, 6, 17, "Answer 2", "own", "pave", "lake", "rock"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_21_own, 6, 18, "Answer 1", "own", "pave", "lake", "rock"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_22_stone, 6, 19, "Answer 4", "bone", "loan", "phone", "stone"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_23_loan, 6, 20, "Answer 2", "bone", "loan", "phone", "stone"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_24_phone, 6, 21, "Answer 3", "bone", "lone", "phone", "stone"));;
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_25_bone, 6, 22, "Answer 1", "bone", "lone", "phone", "stone"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_26_zone, 6, 23, "Answer 4", "bone", "news", "phone", "zone"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_27_news, 6, 24, "Answer 2", "bone", "news", "phone", "zone"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_28_noon, 6, 25, "Answer 2", "nail", "noon", "neon", "need"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_29_moved, 6, 26, "Answer 4", "move", "mode", "mole", "moved"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_30_though, 6, 27, "Answer 1", "though", "through", "health", "these"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_31_through, 6, 28, "Answer 2", "though", "through", "health", "these"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_32_health, 6, 29, "Answer 3", "though", "through", "health", "these"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_33_these, 6, 30, "Answer 4", "thick", "then", "that", "these"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_34_thick, 6, 31, "Answer 1", "thick", "then", "that", "these"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson6_asset_35_then, 6, 32, "Answer 2", "thick", "then", "that", "these"));

        // Lesson 7
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_2_she, 7, 1, "Answer 3", "her", "he", "she", "his"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_3_share, 7, 2, "Answer 1", "share", "ship", "shown", "issue"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_4_ship, 7, 3, "Answer 2", "share", "ship", "shown", "issue"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_5_shown, 7, 4, "Answer 3", "share", "ship", "shown", "issue"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_6_issue, 7, 5, "Answer 4", "share", "ship", "shown", "issue"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_7_insure, 7, 6, "Answer 1", "insure", "jury", "check", "chair"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_8_check, 7, 7, "Answer 4", "insure", "jury", "she", "check"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_9_chair, 7, 8, "Answer 1", "chair", "search", "choose", "teach"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_10_search, 7, 9, "Answer 2", "chair", "search", "choose", "teach"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_11_choose, 7, 10, "Answer 3", "chair", "search", "choose", "teach"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_12_teach, 7, 11, "Answer 4", "chair", "search", "choose", "teach"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_13_church, 7, 12, "Answer 1", "church", "chain", "age", "change"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_14_age, 7, 13, "Answer 3", "church", "chain", "age", "change"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_15_change, 7, 14, "Answer 4", "church", "chain", "age", "change"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_16_jury, 7, 15, "Answer 2", "insure", "jury", "check", "chair"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_17_wages, 7, 16, "Answer 3", "insure", "jury", "wages", "chair"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_18_large, 7, 17, "Answer 1", "large", "jewels", "hot", "jewels"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_19_jewels, 7, 18, "Answer 4", "large", "jewels", "hot", "jewels"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_20_hot, 7, 19, "Answer 3", "large", "jewels", "hot", "jewels"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_21_office, 7, 20, "Answer 1", "office", "copy", "sorry", "policy"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_22_sorry, 7, 21, "Answer 3", "office", "copy", "sorry", "policy"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_23_copy, 7, 22, "Answer 2", "office", "copy", "sorry", "policy"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_24_policy, 7, 23, "Answer 4", "office", "copy", "sorry", "policy"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_25_stop, 7, 24,"Answer 1", "stop", "watch", "job", "stock"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_26_job, 7, 25, "Answer 3", "stop", "watch", "job", "stock"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_27_stock, 7, 26, "Answer 4", "stop", "watch", "job", "stock"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_28_watch, 7, 27, "Answer 2", "stop", "watch", "job", "stock"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_29_dearsir, 7, 28, "Answer 1", "dear sir", "dear ma'am", "yours very truly", "dear madam"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_30_yoursverytruly, 7, 29, "Answer 3", "dear sir", "dear ma'am", "yours very truly", "dear madam"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_31_dearmadam, 7, 30, "Answer 4", "dear sir", "dear ma'am", "yours very truly", "dear madam"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_32_sincerelyyours, 7, 31, "Answer 4", "dear sir", "very truly", "yours very truly", "sincerely yours"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson7_asset_33_verytruly, 7, 32, "Answer 2", "dear sir", "very truly", "yours very truly", "sincerely yours"));

        // Lesson 8
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_1_would, 8, 1, "Answer 3", "hold", "should", "would", "could"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_2_this, 8, 2, "Answer 1", "this", "them", "good", "for"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_3_them, 8, 3, "Answer 2", "this", "them", "good", "for"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_4_for, 8, 4, "Answer 4", "this", "them", "good", "for"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_5_good, 8, 5, "Answer 3", "this", "them", "good", "for"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_6_which, 8, 6, "Answer 1", "which", "what", "where", "who"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_7_there, 8, 7, "Answer 1", "there", "they", "be", "believe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_8_they, 8, 8, "Answer 2", "there", "they", "be", "believe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_9_be, 8, 9, "Answer 3", "there", "they", "be", "believe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_10_believe, 8, 10, "Answer 4", "there", "they", "be", "believe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_11_because, 8, 11, "Answer 4", "mostly", "lately", "mainly", "because"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_12_lately, 8, 12, "Answer 2", "mostly", "lately", "mainly", "because"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_13_mainly, 8, 13, "Answer 3", "mostly", "lately", "mainly", "because"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_14_mostly, 8, 14, "Answer 1", "mostly", "lately", "mainly", "because"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_15_nearly, 8, 15, "Answer 2", "plainly", "nearly", "only", "badly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_16_plainly, 8, 16, "Answer 1", "plainly", "nearly", "only", "badly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_17_only, 8, 17, "Answer 3", "plainly", "nearly", "only", "badly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_18_badly, 8, 18, "Answer 4", "plainly", "nearly", "only", "badly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_20_highly, 8, 20, "Answer 3", "hold", "should", "would", "could"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_19_briefly, 8, 19, "Answer 2", "namely", "briefly", "highly", "costly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_21_costly, 8, 21, "Answer 4", "namely", "briefly", "highly", "costly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_22_namely, 8, 22, "Answer 1", "namely", "briefly", "highly", "costly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_23_daily, 8, 23, "Answer 1", "daily", "monthly", "weekly", "annually"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_24_600, 8, 24, "Answer 1", "600", "700", "800", "900"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_25_12, 8, 25, "Answer 3", "10", "11", "12", "13"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_28_3000, 8, 26, "Answer 3", "2000", "1000", "3000", "4000"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_29_8, 8, 27, "Answer 4", "5", "6", "7", "8"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_30_800000, 8, 28, "Answer 2", "700000", "800000", "900000", "1000000"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_31_700000, 8, 29, "Answer 1", "700000", "800000", "900000", "1000000"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson8_asset_32_7oclock, 8, 30, "Answer 1","7 o clock", "8 o clock", "9 o clock", "10 o clock"));

        // Lesson 9
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_1_action, 9, 1, "Answer 2", "moved", "action", "actions", "move"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_2_operation, 9, 2, "Answer 4", "system", "operate", "process", "operation"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_3_national, 9, 3, "Answer 3", "country", "flag", "national", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_4_occassions, 9, 4, "Answer 1", "occassions", "events", "happenings", "parties"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_5_physician, 9, 5, "Answer 4", "doctor", "surgeon", "nurse", "physician"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_6_nationally, 9, 6, "Answer 2", "globally", "nationally", "locally", "internationally"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_7_election, 9, 7, "Answer 3", "voting", "campaign", "election", "candidate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_8_fashion, 9, 8, "Answer 4", "style", "dress", "trend", "fashion"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_9_cautioned, 9, 9, "Answer 1", "cautioned", "warned", "alerted", "notified"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_10_position, 9, 10, "Answer 3", "rank", "job", "position", "placement"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_11_nations, 9, 11, "Answer 1", "nations", "countries", "states", "unions"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_12_inflation, 9, 12, "Answer 2", "growth", "inflation", "interest", "money"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_13_patient, 9, 13, "Answer 3", "doctor", "hospital", "patient", "clinic"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_14_ancient, 9, 14, "Answer 1", "ancient", "old", "classic", "primitive"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_15_efficiency, 9, 15, "Answer 3", "speed", "accurate", "efficiency", "result"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_16_patiently, 9, 16, "Answer 2", "calmly", "patiently", "slowly", "gently"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_17_efficient, 9, 17, "Answer 4", "productive", "smart", "fast", "efficient"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_18_proficiency, 9, 18, "Answer 2", "expertise", "proficiency", "ability", "skill"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_19_tohave, 9, 19, "Answer 1", "to have", "to give", "tobe", "to take"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_20_tocheck, 9, 20, "Answer 3", "to see", "to repair", "to check", "to look"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_21_tobe, 9, 21, "Answer 2", "to seem", "tobe", "to have", "to go"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_22_tobuy, 9, 22, "Answer 4", "to sell", "to trade", "to own", "to buy"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_23_toserve, 9, 23, "Answer 1", "to serve", "to help", "to lead", "to guide"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_24_tosay, 9, 24, "Answer 3", "to speak", "to tell", "to say", "to hear"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_26_tocharge, 9, 25, "Answer 2", "to pay", "to charge", "to purchase", "to owe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_27_tosee, 9, 26, "Answer 4", "to look", "to view", "to watch", "to see"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_28_toplace, 9, 27, "Answer 3", "to set", "to put", "top lace", "to lay"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_28_chemical, 9, 28, "Answer 1", "chemical", "lab", "element", "compound"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson9_asset_29_tochange, 9, 29, "Answer 2", "to fix", "to change", "to replace", "to edit"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_29_logical, 9, 30, "Answer 4", "smart", "mental", "rational", "logical"));

        // Lesson 10
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_2_sign, 10, 1, "Answer 3", "friend", "kind", "sign", "signed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_3_signed, 10, 2, "Answer 2", "action", "signed", "move", "actions"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_5_friend, 10, 3, "Answer 1", "friend", "kind", "signed", "sign"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_6_kind, 10, 4, "Answer 4", "signed", "friend", "sign", "kind"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_7_planned, 10, 5, "Answer 2", "action", "planned", "signed", "move"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_8_spend, 10, 6, "Answer 2", "signed", "spend", "kind", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_9_mind, 10, 7, "Answer 4", "move", "signed", "friend", "mind"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_10_trained, 10, 8, "Answer 1", "trained", "kind", "signed", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_11_happened, 10, 9, "Answer 2", "planned", "happened", "kind", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_13_errand, 10, 10, "Answer 1", "errand", "signed", "planned", "move"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_14_brand, 10, 11, "Answer 1", "brand", "friend", "signed", "planned"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_15_endorse, 10, 12, "Answer 2", "signed", "endorse", "friend", "move"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_16_printed, 10, 13, "Answer 1", "printed", "signed", "friend", "move"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_17_agent, 10, 14, "Answer 3", "friend", "signed", "agent", "kind"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_18_rent, 10, 15, "Answer 4", "planned", "kind", "signed", "rent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_19_painted, 10, 16, "Answer 1", "painted", "planned", "move", "signed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_20_vacant, 10, 17, "Answer 1", "vacant", "kind", "signed", "move"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_21_prevent, 10, 18, "Answer 3", "friend", "planned", "prevent", "move"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_22_planted, 10, 19, "Answer 4", "signed", "planned", "move", "planted"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_23_into, 10, 20, "Answer 1", "into", "planned", "signed", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_24_current, 10, 21, "Answer 3", "planned", "friend", "current", "signed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_25_parents, 10, 22, "Answer 2", "friend", "parents", "planned", "signed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_26_entire, 10, 23, "Answer 1", "entire", "signed", "planned", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_27_sense, 10, 24, "Answer 1", "sense", "signed", "planned", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_28_senses, 10, 25, "Answer 4", "signed", "planned", "friend", "senses"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_29_face, 10, 26, "Answer 2", "planned", "face", "signed", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_30_faces, 10, 27, "Answer 2", "friend", "faces", "planned", "signed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_31_places, 10, 28, "Answer 1", "places", "planned", "friend", "signed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_32_causes, 10, 29, "Answer 4", "signed", "planned", "friend", "causes"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_33_reduces, 10, 30, "Answer 2", "planned", "reduces", "signed", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_34_prices, 10, 31, "Answer 1", "prices", "planned", "friend", "signed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_35_chances, 10, 32, "Answer 2", "friend", "chances", "planned", "signed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_36_produces, 10, 33, "Answer 4", "signed", "planned", "friend", "produces"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_37_addresses, 10, 34, "Answer 2", "planned", "addresses", "signed", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_38_increases, 10, 35, "Answer 1", "increases", "friend", "signed", "planned"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_39_cases, 10, 36, "Answer 2", "friend", "cases", "planned", "signed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_40_glasses, 10, 37, "Answer 2", "planned", "glasses", "signed", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_41_necessary, 10, 38, "Answer 2", "signed", "necessary", "planned", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_42_services, 10, 39, "Answer 1", "services", "signed", "friend", "planned"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_43_sister, 10, 40, "Answer 1", "sister", "signed", "planned", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_44_assist, 10, 41, "Answer 2", "friend", "assist", "planned", "signed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_45_analysis, 10, 42, "Answer 3", "signed", "planned", "analysis", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_46_basis, 10, 43, "Answer 1", "basis", "signed", "friend", "planned"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_47_insist, 10, 44, "Answer 2", "planned", "insist", "signed", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson10_asset_48_versus, 10, 45, "Answer 2", "friend", "versus", "planned", "signed"));

        // Lesson 11 -- WIP
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_1_when, 11, 1, "Answer 2", "why", "when", "where", "what"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_2_after, 11, 2, "Answer 1", "after", "why", "when", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_3_and, 11, 3, "Answer 4", "why", "where", "what", "and"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_4_were, 11, 4, "Answer 1", "were", "why", "what", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_5_could, 11, 5, "Answer 3", "what", "where", "could", "why"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_6_from, 11, 6, "Answer 2", "why", "from", "what", "when"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_7_send, 11, 7, "Answer 4", "why", "what", "where", "send"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_8_should, 11, 8, "Answer 2", "why", "should", "when", "what"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_9_street, 11, 9, "Answer 1", "street", "what", "why", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_10_assure, 11, 10, "Answer 3", "what", "why", "assure", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_11_assured, 11, 12, "Answer 2", "what", "assured", "why", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_12_assured, 11, 13, "Answer 4", "why", "what", "where", "assured"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_13_toward, 11, 14, "Answer 3", "why", "where", "toward", "what"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_14_record, 11, 15, "Answer 4", "what", "why", "where", "record"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_15_hired, 11, 16, "Answer 1", "hired", "why", "what", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_16_guard, 11, 17, "Answer 2", "why", "guard", "when", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_17_hardest, 11, 18, "Answer 3", "why", "what", "hardest", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_18_insured, 11, 19, "Answer 4", "why", "what", "where", "insured"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_19_guarded, 11, 20, "Answer 2", "what", "guarded", "where", "why"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_20_heard, 11, 21, "Answer 1", "heard", "what", "why", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_21_mail, 11, 22, "Answer 1", "mail", "why", "what", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_22_mailed, 11, 23, "Answer 3", "why", "what", "mailed", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_23_mailed, 11, 24, "Answer 2", "what", "mailed", "why", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_24_failed, 11, 25, "Answer 3", "why", "where", "failed", "what"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_25_build, 11, 26, "Answer 4", "what", "where", "why", "build"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_26_old, 11, 27, "Answer 2", "what", "old", "why", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_27_filled, 11, 28, "Answer 4", "why", "where", "what", "filled"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_28_builder, 11, 29, "Answer 3", "what", "why", "builder", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_29_sold, 11, 30, "Answer 2", "why", "sold", "what", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_30_told, 11, 31, "Answer 1", "told", "what", "why", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_31_folded, 11, 32, "Answer 3", "what", "where", "folded", "why"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_32_havebeen, 11, 33, "Answer 4", "why", "what", "where", "have been"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_33_hadbeen, 11, 34, "Answer 1", "had been", "why", "what", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_34_icouldhavebeen, 11, 35, "Answer 2", "why", "i could have been", "what", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_35_ihavebeen, 11, 36, "Answer 4", "why", "what", "where", "i have been"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_36_ihavenotbeen, 11, 37, "Answer 3", "what", "where", "i have not been", "why"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_37_ishouldhavebeen, 11, 38, "Answer 1", "i should have been", "what", "why", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_38_youhavebeen, 11, 39, "Answer 4", "why", "what", "where", "you have been"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_39_ithasbeen, 11, 40, "Answer 2", "what", "it has been", "where", "why"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_40_hadnotbeen, 11, 41, "Answer 3", "where", "why", "had not been", "what"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_41_ihavebeenable, 11, 42, "Answer 2", "why", "i have been able", "where", "what"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_42_ihavenotbeenable, 11, 43, "Answer 4", "what", "why", "where", "i have not been able"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_43_youwillbeable, 11, 44, "Answer 1", "you will be able", "why", "what", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_44_youhavebeenable, 11, 45, "Answer 3", "why", "what", "you have been able", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_45_youhavenotbeenable, 11, 46, "Answer 2", "what", "you have not been able", "why", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson11_asset_46_imaybeable, 11, 47, "Answer 4", "what", "why", "where", "i may be able"));


        // Lesson 12 -- WIP
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_1_saves, 12, 1, "Answer 3", "rags", "shape", "saves", "these"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_2_sips, 12, 2, "Answer 1", "sips", "shape", "save", "chief"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_3_series, 12, 3, "Answer 2", "stage", "series", "rag", "share"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_4_seeks, 12, 4, "Answer 1", "seeks", "series", "need", "session"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_5_globes, 12, 5, "Answer 4", "seeks", "shape", "chief", "globes"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_6_rags, 12, 6, "Answer 4", "dealer", "seek", "say", "rags"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_7_stones, 12, 7, "Answer 2", "share", "stones", "need", "sips"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_8_solos, 12, 8, "Answer 3", "need", "reach", "solos", "safe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_9_needs, 12, 9, "Answer 2", "series", "needs", "safe", "shape"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_10_sessions, 12, 10, "Answer 4", "shape", "say", "seek", "sessions"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_11_reaches, 12, 11, "Answer 4", "save", "shape", "chief", "reaches"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_12_stages, 12, 12, "Answer 1", "stages", "dealer", "space", "solo"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_13_say, 12, 13, "Answer 1", "say", "shape", "reach", "need"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_14_these, 12, 14, "Answer 3", "stone", "safe", "these", "session"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_15_seethe, 12, 15, "Answer 3", "shape", "session", "seethe", "sips"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_16_name, 12, 16, "Answer 2", "save", "name", "stage", "dealer"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_17_safe, 12, 17, "Answer 2", "4", "safe", "stone", "session"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_18_pace, 12, 18, "Answer 4", "stone", "seeks", "space", "pace"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_19_dealer, 12, 19, "Answer 3", "shape", "stage", "dealer", "safe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_20_chief, 12, 20, "Answer 1", "chief", "say", "reach", "solo"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_21_space, 12, 21, "Answer 1", "space", "share", "stone", "safe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_23_4, 12, 22, "Answer 4", "10", "stage", "5", "4"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_24_5, 12, 23, "Answer 3", "session", "space", "5", "series"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_25_6, 12, 24, "Answer 3", "dealer", "stage", "6", "say"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_26_7, 12, 25, "Answer 3", "shape", "session", "7", "stage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_27_8, 12, 26, "Answer 3", "shape", "series", "8", "session"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_28_9, 12, 27, "Answer 2", "need", "9", "dealer", "stage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_29_10, 12, 28, "Answer 3", "say", "share", "10", "space"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_30_11, 12, 29, "Answer 1", "11", "seethe", "reach", "safe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson12_asset_31_12, 12, 30, "Answer 4", "space", "say", "chief", "12"));


        // Lesson 13 -- WIP
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_1_work, 13, 1, "Answer 3", "wall", "well", "work", "works"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_2_circular, 13, 2, "Answer 4", "wall", "well", "works", "circular"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_3_enclose, 13, 3, "Answer 1", "enclose", "wall", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_4_yesterday, 13, 4, "Answer 2", "wall", "yesterday", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_5_order, 13, 5, "Answer 1", "order", "wall", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_6_was, 13, 6, "Answer 3", "wall", "well", "was", "works"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_7_glad, 13, 7, "Answer 4", "wall", "well", "works", "glad"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_8_soon, 13, 8, "Answer 1", "soon", "wall", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_9_thank, 13, 9, "Answer 2", "wall", "thank", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_10_thanks, 13, 10, "Answer 3", "wall", "well", "thanks", "works"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_11_gladly, 13, 11, "Answer 1", "gladly", "wall", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_12_thankyou, 13, 12, "Answer 4", "wall", "well", "works", "thank you"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_13_worked, 13, 13, "Answer 3", "wall", "well", "worked", "works"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_14_ordered, 13, 14, "Answer 2", "wall", "ordered", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_15_thankyoufor, 13, 15, "Answer 4", "wall", "well", "works", "thank you for"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_16_does, 13, 16, "Answer 2", "wall", "does", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_17_none, 13, 17, "Answer 3", "wall", "well", "none", "works"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_18_us, 13, 18, "Answer 2", "wall", "us", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_19_drug, 13, 19, "Answer 4", "wall", "well", "works", "drug"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_20_number, 13, 20, "Answer 1", "number", "wall", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_21_just, 13, 21, "Answer 3", "wall", "well", "just", "works"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_22_up, 13, 22, "Answer 1", "up", "wall", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_23_enough, 13, 23, "Answer 2", "wall", "enough", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_24_adjust, 13, 24, "Answer 4", "wall", "well", "works", "adjust"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_25_product, 13, 25, "Answer 2", "wall", "product", "works", "well"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_26_must, 13, 26, "Answer 3", "wall", "well", "must", "works"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_27_precious, 13, 27, "Answer 4", "wall", "well", "works", "precious"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_28_book, 13, 28, "Answer 3", "wall", "well", "book", "works"));


        // Lesson 14 -- WIP
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_1_quick, 14, 1, "Answer 2", "quail", "quick", "quad", "quartz"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_2_between, 14, 2, "Answer 3", "before", "behind", "between", "beyond"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_3_quality, 14, 3, "Answer 3", "quench", "qualify", "quality", "quantity"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_4_quote, 14, 4, "Answer 1", "quote", "quiet", "quite", "quota"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_5_square, 14, 5, "Answer 1", "square", "squad", "squash", "squeeze"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_6_hardware, 14, 6, "Answer 1", "hardware", "hardship", "hardly", "hardtop"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_7_quit, 14, 7, "Answer 2", "quiz", "quit", "quickly", "quill"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_8_twice, 14, 8, "Answer 2", "twin", "twice", "twist", "twelve"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_9_roadway, 14, 9, "Answer 3", "road", "roadhouse", "roadway", "roadmap"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_10_equip, 14, 10, "Answer 4", "equal", "equity", "equate", "equip"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_11_twine, 14, 11, "Answer 1", "twine", "twist", "twilight", "twin"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_12_always, 14, 12, "Answer 2", "almost", "always", "alike", "alone"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_22_ted, 14, 13, "Answer 1", "ted", "tea", "ten", "tend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_23_heat, 14, 14, "Answer 1", "heat", "head", "heap", "hear"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_24_heed, 14, 15, "Answer 2", "heal", "heed", "heel", "heap"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_25_heated, 14, 16, "Answer 2", "heats", "heated", "heater", "heave"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_26_listed, 14, 17, "Answer 3", "list", "listen", "listed", "listing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_27_accepted, 14, 18, "Answer 2", "accepts", "accepted", "accepting", "access"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_28_adopted, 14, 19, "Answer 4", "adopt", "adopts", "adopting", "adopted"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_29_acted, 14, 20, "Answer 4", "act", "acts", "acting", "acted"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_30_rested, 14, 21, "Answer 1", "rested", "rests", "resting", "rest"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_31_located, 14, 22, "Answer 2", "locates", "located", "locating", "location"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_32_tested, 14, 23, "Answer 3", "tests", "testing", "tested", "tester"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_33_dated, 14, 24, "Answer 3", "date", "dating", "dated", "dates"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_34_steady, 14, 25, "Answer 1", "steady", "steal", "steer", "steam"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_35_quoted, 14, 26, "Answer 4", "quote", "quotes", "quoting", "quoted"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_36_visited, 14, 27, "Answer 4", "visit", "visits", "visiting", "visited"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_37_today, 14, 28, "Answer 2", "tonight", "today", "tomorrow", "together"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_38_guided, 14, 29, "Answer 4", "guide", "guides", "guiding", "guided"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_39_graded, 14, 30, "Answer 4", "grade", "grades", "grading", "graded"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_40_deduct, 14, 31, "Answer 2", "deduce", "deduct", "deducts", "deduction"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_41_needed, 14, 32, "Answer 1", "needed", "needs", "needing", "need"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_42_added, 14, 33, "Answer 1", "added", "adds", "adding", "add"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_43_deduction, 14, 34, "Answer 2", "deduce", "deduction", "deductive", "deductions"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_44_detail, 14, 35, "Answer 3", "detain", "detour", "detail", "determine"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_45_credit, 14, 36, "Answer 1", "credit", "credits", "credited", "creditor"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_46_edit, 14, 37, "Answer 2", "edition", "edit", "editing", "editor"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_47_debtor, 14, 38, "Answer 2", "debut", "debtor", "debt", "debone"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_48_creditor, 14, 39, "Answer 2", "credits", "creditor", "credited", "crediting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson14_asset_49_audited, 14, 40, "Answer 1", "audited", "audits", "auditing", "auditor"));


        // Lesson 15 -- WIP
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_1_business, 15, 1, "Answer 2", "busy", "business", "holiday", "market"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_2_what, 15, 2, "Answer 4", "when", "where", "why", "what"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_3_value, 15, 3, "Answer 1", "value", "cost", "price", "worth"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_4_doctor, 15, 4, "Answer 3", "teacher", "nurse", "doctor", "patient"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_5_about, 15, 5, "Answer 1", "about", "above", "around", "after"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_6_than, 15, 6, "Answer 2", "then", "than", "that", "thus"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_7_any, 15, 7, "Answer 3", "all", "some", "any", "none"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_8_thing, 15, 8, "Answer 4", "stuff", "object", "item", "thing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_9_one, 15, 9, "Answer 1", "one", "two", "three", "four"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_10_things, 15, 10, "Answer 2", "stuff", "things", "items", "objects"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_11_anything, 15, 11, "Answer 4", "nothing", "something", "everything", "anything"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_12_businesses, 15, 12, "Answer 1", "businesses", "shops", "markets", "stores"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_13_thinking, 15, 13, "Answer 2", "planning", "thinking", "working", "dreaming"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_14_once, 15, 14, "Answer 3", "twice", "thrice", "once", "never"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_15_businessman, 15, 15, "Answer 2", "farmer", "businessman", "teacher", "driver"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_16_possible, 15, 16, "Answer 4", "impossible", "maybe", "uncertain", "possible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_17_terrible, 15, 17, "Answer 3", "great", "wonderful", "terrible", "excellent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_18_double, 15, 18, "Answer 1", "double", "single", "half", "triple"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_19_availble, 15, 19, "Answer 4", "busy", "taken", "full", "availble"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_20_valuable, 15, 20, "Answer 2", "cheap", "valuable", "useless", "common"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_21_table, 15, 21, "Answer 1", "table", "chair", "desk", "bench"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_22_reliable, 15, 22, "Answer 3", "fake", "unreliable", "reliable", "false"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_23_favorable, 15, 23, "Answer 4", "bad", "unlucky", "poor", "favorable"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_24_tabled, 15, 24, "Answer 2", "open", "tabled", "removed", "cleared"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_25_capable, 15, 25, "Answer 4", "weak", "unable", "small", "capable"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_26_sensible, 15, 26, "Answer 1", "sensible", "silly", "foolish", "crazy"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_27_cables, 15, 27, "Answer 3", "wires", "ropes", "cables", "cords"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_28_receive, 15, 28, "Answer 2", "send", "receive", "deliver", "forward"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_29_revise, 15, 29, "Answer 4", "forget", "ignore", "skip", "revise"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_30_reasonable, 15, 30, "Answer 3", "unfair", "wrong", "reasonable", "false"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_31_reply, 15, 31, "Answer 1", "reply", "ignore", "ask", "question"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_32_repair, 15, 32, "Answer 2", "break", "repair", "destroy", "ruin"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_33_reappear, 15, 33, "Answer 4", "disappear", "vanish", "hide", "reappear"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_34_research, 15, 34, "Answer 3", "guess", "assume", "research", "estimate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_35_reception, 15, 35, "Answer 4", "departure", "exit", "leave", "reception"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_36_rearrange, 15, 36, "Answer 2", "arrange", "rearrange", "order", "sort"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_37_replace, 15, 37, "Answer 3", "keep", "stay", "replace", "hold"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_38_receipt, 15, 38, "Answer 1", "receipt", "bill", "invoice", "check"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson15_asset_39_reopen, 15, 39, "Answer 2", "close", "reopen", "stop", "cancel"));

        // Lesson 16 -- No data pa

        // Lesson 17 -- WIP
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_1_gentlemen, 17, 1, "Answer 2", "gentleman", "gentlemen", "gentle", "gent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_2_company, 17, 2, "Answer 4", "gent", "man", "men", "company"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_3_short, 17, 3, "Answer 1", "short", "tall", "long", "wide"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_4_morning, 17, 4, "Answer 3", "night", "evening", "morning", "noon"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_5_manufacture, 17, 5, "Answer 3", "product", "factory", "manufacture", "machine"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_6_important, 17, 6, "Answer 4", "tiny", "small", "big", "important"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_7_where, 17, 7, "Answer 1", "where", "when", "what", "how"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_8_next, 17, 8, "Answer 2", "previous", "next", "last", "first"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_9_person, 17, 9, "Answer 1", "person", "people", "man", "woman"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_10_perfect, 17, 10, "Answer 1", "perfect", "flawed", "good", "bad"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_11_persist, 17, 11, "Answer 1", "persist", "quit", "stop", "pause"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_12_personal, 17, 12, "Answer 2", "public", "personal", "private", "shared"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_13_permanent, 17, 13, "Answer 3", "temporary", "short", "permanent", "changeable"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_14_permit, 17, 14, "Answer 4", "deny", "ban", "reject", "permit"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_15_perhaps, 17, 15, "Answer 4", "sure", "definitely", "certain", "perhaps"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_16_personnel, 17, 16, "Answer 4", "equipment", "tools", "machines", "personnel"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_17_persuade, 17, 17, "Answer 4", "discourage", "dissuade", "ignore", "persuade"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_18_purchase, 17, 18, "Answer 1", "purchase", "sell", "trade", "give"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_19_purple, 17, 19, "Answer 1", "purple", "red", "blue", "green"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_20_purpose, 17, 20, "Answer 2", "reason", "purpose", "goal", "aim"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_21_delay, 17, 21, "Answer 2", "advance", "delay", "hurry", "rush"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_22_deposit, 17, 22, "Answer 3", "withdraw", "loan", "deposit", "spend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_23_decide, 17, 23, "Answer 3", "hesitate", "delay", "decide", "doubt"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_24_deserve, 17, 24, "Answer 2", "earn", "deserve", "take", "steal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_25_deliver, 17, 25, "Answer 1", "deliver", "take", "ship", "receive"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_26_decision, 17, 26, "Answer 1", "decision", "choice", "option", "selection"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_27_desirable, 17, 27, "Answer 2", "unwanted", "desirable", "bad", "awful"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_28_depended, 17, 28, "Answer 2", "independent", "depended", "free", "solo"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_29_design, 17, 29, "Answer 2", "destroy", "design", "ruin", "wreck"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_30_direct, 17, 30, "Answer 2", "indirect", "direct", "roundabout", "detour"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_31_direction, 17, 31, "Answer 2", "confusion", "direction", "lost", "wander"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson17_asset_32_directly, 17, 32, "Answer 3", "slowly", "indirectly", "directly", "gradually"));

        // Lesson 26 -- WIP
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_1_signs, 26, 1, "Answer 2", "symbols", "signs", "alerts", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_2_science, 26, 2, "Answer 4", "math", "history", "art", "science"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_3_compliance, 26, 3, "Answer 1", "compliance", "resistance", "avoidance", "disobedience"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_4_dial, 26, 4, "Answer 3", "phone", "rotate", "dial", "ring"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_5_client, 26, 5, "Answer 2", "customer", "client", "manager", "vendor"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_6_reliance, 26, 6, "Answer 4", "independence", "distrust", "avoidance", "reliance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_7_prior, 26, 7, "Answer 1", "prior", "later", "next", "after"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_8_quiet, 26, 8, "Answer 2", "loud", "quiet", "noisy", "shouting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_9_appliances, 26, 9, "Answer 4", "furniture", "gadgets", "devices", "appliances"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset__10_enjoy, 26, 10, "Answer 1", "enjoy", "hate", "avoid", "reject"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_11_engagement, 26, 11, "Answer 2", "marriage", "engagement", "friendship", "dating"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_12_endanger, 26, 12, "Answer 3", "protect", "guard", "endanger", "secure"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_13_engrave, 26, 13, "Answer 1", "engrave", "erase", "scratch", "remove"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_14_engine, 26, 14, "Answer 4", "gear", "wheel", "machine", "engine"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_15_encourage, 26, 15, "Answer 3", "discourage", "avoid", "encourage", "reject"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_16_endeavor, 26, 16, "Answer 1", "endeavor", "quit", "stop", "pause"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_17_enlarge, 26, 17, "Answer 2", "shrink", "enlarge", "reduce", "minimize"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_18_encouragement, 26, 18, "Answer 4", "criticism", "avoidance", "rejection", "encouragement"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_19_until, 26, 19, "Answer 2", "after", "until", "since", "before"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_20_unfair, 26, 20, "Answer 1", "unfair", "equal", "just", "fair"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_21_unfilled, 26, 21, "Answer 3", "full", "occupied", "unfilled", "packed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_22_unless, 26, 22, "Answer 4", "if", "since", "because", "unless"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_23_unpaid, 26, 23, "Answer 2", "paid", "unpaid", "settled", "given"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_24_unreasonable, 26, 24, "Answer 1", "unreasonable", "fair", "just", "reasonable"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_25_undue, 26, 25, "Answer 2", "proper", "undue", "appropriate", "expected"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_26_uncertain, 26, 26, "Answer 3", "sure", "certain", "uncertain", "clear"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_27_undoubtedly, 26, 27, "Answer 1", "undoubtedly", "maybe", "perhaps", "possibly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_28_innovation, 26, 28, "Answer 2", "tradition", "innovation", "habit", "custom"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_29_enact, 26, 29, "Answer 4", "repeal", "stop", "pause", "enact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_30_unable, 26, 30, "Answer 1", "unable", "capable", "able", "fit"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_31_morethan, 26, 31, "Answer 3", "less than", "equal to", "more than", "exactly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_32_tous, 26, 32, "Answer 4", "for them", "for you", "for me", "to us"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_33_yourorder, 26, 33, "Answer 1", "your order", "my order", "the order", "our order"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_34_wehope, 26, 34, "Answer 2", "they hope", "we hope", "i hope", "you hope"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_35_letme, 26, 35, "Answer 4", "help me", "call me", "tell me", "let me"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_36_youordered, 26, 36, "Answer 3", "they ordered", "she ordered", "you ordered", "we ordered"));

        // Lesson 29 -- WIP
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_1_responsible, 29, 1, "Answer 2", "responsive", "responsible", "report", "responsibility"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_2_publish, 29, 2, "Answer 4", "responsive", "responsible", "report", "publish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_3_usual, 29, 3, "Answer 3", "report", "responsive", "usual", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_4_worth, 29, 4, "Answer 1", "worth", "report", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_5_ordinary, 29, 5, "Answer 2", "report", "ordinary", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_6_world, 29, 6, "Answer 4", "report", "responsive", "responsible", "world"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_7_public, 29, 7, "Answer 3", "responsive", "responsible", "public", "report"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_8_experience, 29, 8, "Answer 1", "experience", "report", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_9_recognize, 29, 9, "Answer 4", "report", "responsive", "responsible", "recognize"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_10_extra, 29, 10, "Answer 2", "report", "extra", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_11_expenses, 29, 11, "Answer 3", "report", "responsive", "expenses", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_12_excuse, 29, 12, "Answer 4", "report", "responsive", "responsible", "excuse"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_13_examine, 29, 13, "Answer 2", "report", "examine", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_14_expert, 29, 14, "Answer 4", "report", "responsive", "responsible", "expert"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_15_extensive, 29, 15, "Answer 3", "report", "responsive", "extensive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_16_extremely, 29, 16, "Answer 2", "report", "extremely", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_17_excellent, 29, 17, "Answer 3", "report", "responsive", "excellent", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_18_exception, 29, 18, "Answer 4", "report", "responsive", "responsible", "exception"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_19_careful, 29, 19, "Answer 2", "report", "careful", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_20_useful, 29, 20, "Answer 1", "useful", "report", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_21_helpful, 29, 21, "Answer 2", "report", "helpful", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_22_doubtful, 29, 22, "Answer 3", "report", "responsive", "doubtful", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_23_thoughtful, 29, 23, "Answer 4", "report", "responsive", "responsible", "thoughtful"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_24_helpfully, 29, 24, "Answer 1", "helpfully", "report", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_25_grateful, 29, 25, "Answer 3", "report", "responsive", "grateful", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_26_beautiful, 29, 26, "Answer 2", "report", "beautiful", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_27_helpfulness, 29, 27, "Answer 1", "helpfulness", "report", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_28_chemical, 29, 28, "Answer 3", "report", "responsive", "chemical", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_29_logical, 29, 29, "Answer 2", "report", "logical", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_30_technical, 29, 30, "Answer 4", "report", "responsive", "responsible", "technical"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_31_medical, 29, 31, "Answer 1", "medical", "report", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_32_political, 29, 32, "Answer 3", "report", "responsive", "political", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_33_physically, 29, 33, "Answer 2", "report", "physically", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_34_identical, 29, 34, "Answer 1", "identical", "report", "responsive", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_35_typical, 29, 35, "Answer 3", "report", "responsive", "typical", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson29_asset_36_articles, 29, 36, "Answer 4", "report", "responsive", "responsible", "articles"));

        // Lesson 32 -- WIP
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_1_steady, 32, 1, "Answer 3", "stay", "steel", "steady", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_2_steadily, 32, 2, "Answer 1", "steadily", "steel", "steady", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_3_readily, 32, 3, "Answer 4", "steady", "stay", "state", "readily"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_4_temporarily, 32, 4, "Answer 2", "steel", "temporarily", "steady", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_5_family, 32, 5, "Answer 2", "steel", "family", "steady", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_6_easily, 32, 6, "Answer 4", "steady", "stay", "steel", "easily"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_7_hastily, 32, 7, "Answer 3", "steady", "steel", "hastily", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_8_families, 32, 8, "Answer 1", "families", "steel", "steady", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_9_alter, 32, 9, "Answer 3", "steady", "steel", "alter", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_10_altogether, 32, 10, "Answer 4", "steady", "stay", "steel", "altogether"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_11_also, 32, 11, "Answer 3", "steady", "steel", "also", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_12_altered, 32, 12, "Answer 4", "steady", "stay", "steel", "altered"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_13_almost, 32, 13, "Answer 1", "almost", "stay", "steel", "steady"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_14_almanac, 32, 14, "Answer 3", "steady", "steel", "almanac", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_15_discuss, 32, 15, "Answer 1", "discuss", "stay", "steel", "steady"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_16_discourage, 32, 16, "Answer 4", "steady", "stay", "steel", "discourage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_17_distance, 32, 17, "Answer 1", "distance", "stay", "steel", "steady"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_18_discover, 32, 18, "Answer 4", "steady", "stay", "steel", "discover"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_19_dismiss, 32, 19, "Answer 3", "steady", "steel", "dismiss", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_20_dispose, 32, 20, "Answer 1", "dispose", "stay", "steel", "steady"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_21_describe, 32, 21, "Answer 2", "steady", "describe", "steel", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_22_despite, 32, 22, "Answer 1", "despite", "stay", "steel", "steady"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_23_desperate, 32, 23, "Answer 3", "steady", "steel", "desperate", "state"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_24_description, 32, 24, "Answer 4", "steady", "stay", "steel", "description"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_25_destroy, 32, 25, "Answer 2", "steady", "destroy", "steel", "state"));

        // Lesson 41 -- WIP
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_1_photograph, 41, 1, "Answer 3", "graphic", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_2_stenographer, 41, 2, "Answer 1", "stenographer", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_3_paragraph, 41, 3, "Answer 4", "photo", "photograph", "photosynthesis", "paragraph"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_4_photographic, 41, 4, "Answer 2", "photo", "photographic", "photosynthesis", "graphic"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_5_autographed, 41, 5, "Answer 1", "autographed", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_6_paragraphed, 41, 6, "Answer 3", "graphic", "photo", "paragraphed", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_7_photographically, 41, 7, "Answer 2", "photo", "photographically", "photosynthesis", "graphic"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_8_typographical, 41, 8, "Answer 4", "photo", "photograph", "photosynthesis", "typographical"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_9_telegraphing, 41, 9, "Answer 1", "telegraphing", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_10_anniversary, 41, 10, "Answer 3", "photo", "anniversary", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_11_significant, 41, 12, "Answer 4", "photo", "photograph", "photosynthesis", "significant"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_12_reluctant, 41, 13, "Answer 1", "reluctant", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_13_memorandum, 41, 14, "Answer 3", "photo", "memorandum", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_14_statistic, 41, 15, "Answer 2", "statistic", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_15_privilege, 41, 16, "Answer 4", "photo", "photograph", "photosynthesis", "privilege"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_16_convenient, 41, 17, "Answer 1", "convenient", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_17_statistics, 41, 18, "Answer 2", "photo", "statistics", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_18_privileged, 41, 19, "Answer 4", "photo", "photograph", "photosynthesis", "privileged"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_19_equivalent, 41, 20, "Answer 1", "equivalent", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_20_statistical, 41, 21, "Answer 3", "photo", "statistical", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_21_privileges, 41, 22, "Answer 2", "photo", "privileges", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_22_transmit, 41, 23, "Answer 1", "transmit", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_23_transport, 41, 24, "Answer 3", "photo", "transport", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_24_translation, 41, 25, "Answer 4", "photo", "photograph", "photosynthesis", "translation"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_25_transact, 41, 26, "Answer 2", "photo", "transact", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_26_transportation, 41, 27, "Answer 3", "photo", "photograph", "transportation", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_27_transit, 41, 28, "Answer 1", "transit", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_28_transaction, 41, 29, "Answer 4", "photo", "photograph", "photosynthesis", "transaction"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_29_transfer, 41, 30, "Answer 2", "transfer", "photo", "photograph", "photosynthesis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson41_asset_30_transistor, 41, 31, "Answer 3", "photo", "transistor", "photograph", "photosynthesis"));
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