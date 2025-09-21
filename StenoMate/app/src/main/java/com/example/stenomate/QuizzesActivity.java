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
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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

        // Lesson 11
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

        // Lesson 12
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

        // Lesson 13
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
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_29_put, 13, 29, "Answer 1", "put", "pot", "book", "works"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_30_pull, 13, 30, "Answer 2", "put", "pull", "push", "cook"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_31_cook, 13, 31, "Answer 4", "put", "pull", "push", "cook"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_32_push, 13, 32, "Answer 3", "put", "pull", "push", "cook"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_33_stood, 13, 33, "Answer 1", "stood", "look", "foot", "sugar"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_34_look, 13, 34, "Answer 2", "stood", "look", "foot", "sugar"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_35_foot, 13, 35, "Answer 3", "stood", "look", "foot", "sugar"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_36_sugar, 13, 36, "Answer 4", "stood", "look", "foot", "sugar"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_37_took, 13, 37, "Answer 3", "took", "full", "wood", "cook"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_38_full, 13, 38, "Answer 2", "took", "full", "wood", "cook"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson13_asset_39_wood, 13, 39, "Answer 3", "took", "full", "wood", "cook"));

        // Lesson 14
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

        // Lesson 15
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

        // Lesson 16
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_1_boy, 16, 1, "Answer 3", "girl", "toy", "boy", "oil"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_2_oil, 16, 2, "Answer 4", "boy", "toy", "girl", "oil"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_3_annoy, 16, 3, "Answer 2", "toy", "annoy", "oil", "boy"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_4_toy, 16, 4, "Answer 1", "toy", "oil", "boy", "girl"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_5_spoil, 16, 5, "Answer 2", "join", "spoil", "toy", "boy"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_6_point, 16, 6, "Answer 3", "oil", "annoy", "point", "men"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_7_join, 16, 7, "Answer 4", "men", "toy", "boil", "join"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_8_boil, 16, 8, "Answer 1", "boil", "toy", "men", "join"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_9_appoint, 16, 9, "Answer 2", "join", "appoint", "spoil", "oil"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_10_men, 16, 10, "Answer 3", "oil", "toy", "men", "point"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_11_knee, 16, 11, "Answer 2", "men", "knee", "me", "many"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_12_me, 16, 12, "Answer 4", "many", "men", "knee", "me"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_13_many, 16, 13, "Answer 1", "many", "me", "knee", "men"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_14_men, 16, 14, "Answer 2", "many", "men", "mentioned", "me"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_15_mentioned, 16, 15, "Answer 3", "me", "men", "mentioned", "many"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_16_businessmen, 16, 16, "Answer 4", "many", "me", "men", "businessmen"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_17_meant, 16, 17, "Answer 1", "meant", "men", "many", "me"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_18_salesman, 16, 18, "Answer 2", "meant", "salesman", "businessmen", "men"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_19_immense, 16, 19, "Answer 3", "money", "month", "immense", "manage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_20_mental, 16, 20, "Answer 1", "mental", "minimum", "immense", "men"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_21_month, 16, 21, "Answer 2", "minimum", "month", "money", "meant"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_22_immense, 16, 22, "Answer 3", "men", "mental", "immense", "me"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_23_minimum, 16, 23, "Answer 4", "month", "money", "mental", "minimum"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_24_money, 16, 24, "Answer 1", "money", "month", "mental", "minimum"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_25_manage, 16, 25, "Answer 2", "mental", "manage", "minimum", "money"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_26_minimum, 16, 26, "Answer 3", "manage", "money", "minimum", "month"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_27_money, 16, 27, "Answer 4", "mental", "minimum", "manage", "money"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_28_manage, 16, 28, "Answer 1", "manage", "money", "month", "minimum"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_29_year, 16, 29, "Answer 2", "yellow", "year", "yarn", "yard"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_30_yellow, 16, 30, "Answer 3", "year", "yard", "yellow", "yield"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_31_yield, 16, 31, "Answer 1", "yield", "yard", "yarn", "yale"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_32_yard, 16, 32, "Answer 2", "yield", "yard", "yellow", "year"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_33_yarn, 16, 33, "Answer 4", "yellow", "yard", "yale", "yarn"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_34_yale, 16, 34, "Answer 3", "yard", "yellow", "yale", "yarn"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_35_yard, 16, 35, "Answer 1", "yard", "yarn", "yield", "yale"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_36_yarn, 16, 36, "Answer 2", "yard", "yarn", "yale", "year"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson16_asset_37_yale, 16, 37, "Answer 4", "yield", "year", "yard", "yale"));

        // Lesson 17
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

        // Lesson 18
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_1_husky, 18, 1, "Answer 1", "husky", "husk", "hate", "hair"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_2_gust, 18, 2, "Answer 2", "just", "gust", "rust", "must"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_3_just, 18, 3, "Answer 3", "gust", "rust", "just", "must"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_4_loose, 18, 4, "Answer 1", "loose", "lose", "loss", "loop"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_5_does, 18, 5, "Answer 2", "dose", "does", "do", "done"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_6_rust, 18, 6, "Answer 3", "must", "gust", "rust", "dust"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_7_research, 18, 7, "Answer 4", "refer", "result", "reach", "research"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_8_reference, 18, 8, "Answer 2", "research", "reference", "refer", "refine"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_9_reopen, 18, 9, "Answer 3", "replay", "repeat", "reopen", "rebuild"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_10_relate, 18, 10, "Answer 1", "relate", "release", "remain", "relay"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_11_retake, 18, 11, "Answer 2", "retain", "retake", "return", "retool"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_12_retreat, 18, 12, "Answer 4", "return", "retain", "retain", "retreat"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_13_depressed, 18, 13, "Answer 1", "depressed", "depress", "depressing", "deep"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_14_deliver, 18, 14, "Answer 3", "declare", "develop", "deliver", "delete"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_15_direction, 18, 15, "Answer 2", "decision", "direction", "direct", "director"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_16_declare, 18, 16, "Answer 4", "delete", "deliver", "develop", "declare"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_17_decay, 18, 17, "Answer 1", "decay", "delay", "decal", "deep"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_18_degrade, 18, 18, "Answer 2", "degree", "degrade", "decree", "design"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_19_baked, 18, 19, "Answer 3", "bake", "backed", "baked", "banked"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_20_saved, 18, 20, "Answer 4", "save", "saver", "savee", "saved"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_21_feared, 18, 21, "Answer 1", "feared", "fear", "fared", "feast"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_22_missed, 18, 22, "Answer 2", "miss", "missed", "mister", "mist"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_23_planned, 18, 23, "Answer 3", "plan", "planner", "planned", "planet"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_24_mailed, 18, 24, "Answer 4", "mail", "mailer", "main", "mailed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_25_1, 18, 25, "Answer 1", "1", "2", "3", "4"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_26_2, 18, 26, "Answer 2", "1", "2", "3", "4"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_27_3, 18, 27, "Answer 3", "1", "2", "3", "4"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_28_4, 18, 28, "Answer 4", "1", "2", "3", "4"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_29_5, 18, 29, "Answer 1", "5", "6", "7", "8"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_30_6, 18, 30, "Answer 2", "5", "6", "7", "8"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_31_7, 18, 31, "Answer 3", "5", "6", "7", "8"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_32_8, 18, 32, "Answer 4", "5", "6", "7", "8"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_33_9, 18, 33, "Answer 1", "9", "10", "11", "12"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_34_10, 18, 34, "Answer 2", "9", "10", "11", "12"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_35_11, 18, 35, "Answer 3", "9", "10", "11", "12"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_36_12, 18, 36, "Answer 4", "9", "10", "11", "12"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_37_13, 18, 37, "Answer 1", "13", "14", "15", "16"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_38_14, 18, 38, "Answer 2", "13", "14", "15", "16"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_39_15, 18, 39, "Answer 3", "13", "14", "15", "16"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson18_asset_40_16, 18, 40, "Answer 4", "13", "14", "15", "16"));

        // Lesson 19
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_1_part, 19, 1, "Answer 4", "paid", "park", "pale", "part"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_2_ms, 19, 2, "Answer 1", "ms", "miss", "mass", "must"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_3_opportunity, 19, 3, "Answer 3", "option", "open", "opportunity", "opera"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_4_present, 19, 4, "Answer 2", "past", "present", "future", "gift"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_5_advertise, 19, 5, "Answer 2", "add", "advertise", "advance", "advice"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_6_immediate, 19, 6, "Answer 3", "immense", "immigrant", "immediate", "image"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_7_use, 19, 7, "Answer 4", "us", "user", "used", "use"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_8_review, 19, 8, "Answer 2", "revive", "review", "renew", "reveal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_9_unite, 19, 9, "Answer 3", "unit", "union", "unite", "unique"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_10_few, 19, 10, "Answer 1", "few", "new", "view", "feud"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_11_unit, 19, 11, "Answer 2", "unite", "unit", "unison", "unique"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_12_pure, 19, 12, "Answer 3", "pore", "pair", "pure", "pour"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_13_view, 19, 13, "Answer 4", "few", "vow", "veil", "view"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_14_unique, 19, 14, "Answer 2", "union", "unique", "unite", "unit"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_15_acute, 19, 15, "Answer 1", "acute", "cute", "act", "accurate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_16_payment, 19, 16, "Answer 4", "pay", "pave", "paint", "payment"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_17_department, 19, 17, "Answer 3", "depart", "departed", "department", "deport"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_18_settlement, 19, 18, "Answer 2", "settle", "settlement", "setting", "setback"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_19_management, 19, 19, "Answer 4", "manager", "managing", "manual", "management"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_20_equipment, 19, 20, "Answer 3", "equip", "equal", "equipment", "equator"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_21_treatment, 19, 21, "Answer 1", "treatment", "treat", "treaty", "treasure"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_22_shipment, 19, 22, "Answer 2", "ship", "shipment", "shipping", "shape"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_23_movement, 19, 23, "Answer 3", "move", "movie", "movement", "motive"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_24_element, 19, 24, "Answer 4", "elephant", "elevate", "election", "element"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_25_advertisement, 19, 25, "Answer 1", "advertisement", "advertise", "advance", "advice"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_26_replacement, 19, 26, "Answer 4", "replace", "replay", "relate", "replacement"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_27_assignment, 19, 27, "Answer 2", "assign", "assignment", "assistant", "assume"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_28_special, 19, 28, "Answer 3", "space", "species", "special", "speech"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_29_financial, 19, 29, "Answer 2", "final", "financial", "finish", "finger"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_30_initial, 19, 30, "Answer 4", "init", "inside", "into", "initial"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_31_especial, 19, 31, "Answer 1", "especial", "special", "species", "speech"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_32_social, 19, 32, "Answer 2", "society", "social", "sock", "sofa"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_33_initially, 19, 33, "Answer 3", "init", "initial", "initially", "inner"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_34_partial, 19, 34, "Answer 4", "part", "party", "partner", "partial"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_35_official, 19, 35, "Answer 2", "office", "official", "offer", "offense"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson19_asset_36_initialed, 19, 36, "Answer 1", "initialed", "initial", "init", "inner"));

        // Lesson 20
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_2_now, 20, 1, "Answer 3", "note", "noted", "now", "not"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_3_south, 20, 2, "Answer 2", "south", "south", "soup", "sound"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_4_how, 20, 3, "Answer 4", "hi", "hat", "hot", "how"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_5_down, 20, 4, "Answer 1", "down", "dawn", "done", "den"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_6_loud, 20, 5, "Answer 2", "load", "loud", "long", "lord"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_7_house, 20, 6, "Answer 3", "hose", "horse", "house", "host"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_8_sound, 20, 7, "Answer 4", "sand", "send", "soup", "sound"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_9_account, 20, 8, "Answer 2", "accountant", "account", "amount", "account"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_10_ounce, 20, 9, "Answer 1", "ounce", "once", "one", "ounce"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_11_found, 20, 10, "Answer 2", "fond", "found", "fund", "fend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_12_round, 20, 11, "Answer 3", "road", "read", "round", "rent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_13_crowd, 20, 12, "Answer 4", "crown", "cross", "crown", "crowd"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_14_other, 20, 13, "Answer 1", "other", "outer", "order", "over"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_15_mother, 20, 14, "Answer 2", "matter", "mother", "meter", "mister"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_16_either, 20, 15, "Answer 3", "ether", "eater", "either", "enter"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_17_another, 20, 16, "Answer 4", "ant", "any", "anger", "another"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_18_gather, 20, 17, "Answer 2", "gator", "gather", "gathered", "gate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_19_bother, 20, 18, "Answer 3", "bothered", "bottle", "bother", "butter"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_20_whether, 20, 19, "Answer 1", "whether", "weather", "whale", "where"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_21_brother, 20, 20, "Answer 2", "broom", "brother", "brood", "broke"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_22_bothered, 20, 21, "Answer 4", "bother", "border", "bot", "bothered"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_23_confer, 20, 22, "Answer 1", "confer", "confuse", "confirm", "contain"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_24_considerable, 20, 23, "Answer 2", "consider", "considerable", "consist", "contain"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_25_concrete, 20, 24, "Answer 3", "concert", "concern", "concrete", "convey"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_26_concert, 20, 25, "Answer 4", "concern", "conclude", "connect", "concert"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_27_control, 20, 26, "Answer 2", "contact", "control", "contain", "convert"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_28_contract, 20, 27, "Answer 3", "contact", "concrete", "contract", "contest"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_29_convey, 20, 28, "Answer 4", "convert", "convict", "convince", "convey"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_30_convince, 20, 29, "Answer 1", "convince", "convey", "convert", "contact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_31_contest, 20, 30, "Answer 2", "context", "contest", "contact", "connect"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_32_complete, 20, 31, "Answer 3", "complex", "compare", "complete", "compete"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_33_compliment, 20, 32, "Answer 4", "complete", "compete", "compose", "compliment"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_34_combine, 20, 33, "Answer 1", "combine", "common", "comfort", "comment"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_35_computer, 20, 34, "Answer 2", "compute", "computer", "commute", "composer"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_36_compare, 20, 35, "Answer 3", "company", "compass", "compare", "compile"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_37_accomplish, 20, 36, "Answer 4", "accompany", "account", "accord", "accomplish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_38_connect, 20, 37, "Answer 1", "connect", "contact", "content", "contest"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_39_commit, 20, 38, "Answer 2", "come", "commit", "common", "comma"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_40_commercial, 20, 39, "Answer 3", "comment", "commence", "commercial", "committee"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_41_connection, 20, 40, "Answer 4", "connect", "connects", "connector", "connection"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_42_commerce, 20, 41, "Answer 1", "commerce", "comment", "common", "comet"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson20_asset_43_accommodate, 20, 42, "Answer 2", "accompany", "accommodate", "account", "accord"));

        // Lesson 21
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_1_advantage, 21, 1, "Answer 3", "disadvantage", "forward", "advantage", "advance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_2_several, 21, 2, "Answer 1", "several", "advantage", "forward", "advance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_3_every, 21, 3, "Answer 4", "forward", "several", "advance", "every"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_4_suggest, 21, 4, "Answer 2", "advance", "suggest", "every", "forward"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_5_out, 21, 5, "Answer 2", "ten", "out", "advance", "forward"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_6_very, 21, 6, "Answer 4", "advance", "several", "out", "very"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_7_ten, 21, 7, "Answer 1", "ten", "very", "forward", "advantage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_8_tend, 21, 8, "Answer 3", "forward", "ten", "tend", "advance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_9_potential, 21, 9, "Answer 2", "advance", "potential", "several", "advantage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_10_bulletin, 21, 10, "Answer 1", "bulletin", "advantage", "potential", "advance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_11_attend, 21, 11, "Answer 4", "advantage", "advance", "bulletin", "attend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_12_competent, 21, 12, "Answer 2", "forward", "competent", "attend", "advantage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_13_stand, 21, 13, "Answer 1", "stand", "competent", "advantage", "forward"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_14_attention, 21, 14, "Answer 4", "forward", "stand", "advantage", "attention"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_15_consistent, 21, 15, "Answer 2", "advantage", "consistent", "attention", "advance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_16_cotton, 21, 16, "Answer 3", "advance", "consistent", "cotton", "stand"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_17_tentative, 21, 17, "Answer 1", "tentative", "attention", "advantage", "forward"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_18_straighten, 21, 18, "Answer 4", "cotton", "tentative", "advantage", "straighten"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_19_tonight, 21, 19, "Answer 2", "advance", "tonight", "advantage", "tentative"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_20_deny, 21, 20, "Answer 3", "tentative", "advance", "deny", "forward"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_21_evidence, 21, 21, "Answer 1", "evidence", "tonight", "forward", "advance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_22_danger, 21, 22, "Answer 4", "deny", "evidence", "advance", "danger"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_23_dentist, 21, 23, "Answer 2", "forward", "dentist", "advantage", "advance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_24_condense, 21, 24, "Answer 3", "advance", "dentist", "condense", "advantage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_25_dinner, 21, 25, "Answer 2", "condense", "dinner", "advantage", "advance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_26_sudden, 21, 26, "Answer 1", "sudden", "advance", "dinner", "forward"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_27_president, 21, 27, "Answer 4", "forward", "dinner", "advance", "president"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_28_guidance, 21, 28, "Answer 2", "advance", "guidance", "sudden", "advantage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_29_obtain, 21, 29, "Answer 3", "sudden", "guidance", "obtain", "advance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_30_attain, 21, 30, "Answer 1", "attain", "obtain", "advance", "advantage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_31_pertain, 21, 31, "Answer 2", "advance", "pertain", "attain", "obtain"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_32_contain, 21, 32, "Answer 4", "advance", "obtain", "advantage", "contain"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_33_retain, 21, 33, "Answer 3", "forward", "advance", "retain", "advantage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_34_certain, 21, 34, "Answer 2", "retain", "certain", "advance", "forward"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_35_maintain, 21, 35, "Answer 1", "maintain", "retain", "advance", "advantage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_36_detain, 21, 36, "Answer 4", "advance", "certain", "advantage", "detain"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson21_asset_37_obtainable, 21, 37, "Answer 2", "forward", "obtainable", "advance", "advantage"));

        // Lesson 22
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_2_ten, 22, 1, "Answer 2", "one", "ten", "five", "six"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_3_tem, 22, 2, "Answer 3", "ten", "six", "tem", "five"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_4_temper, 22, 3, "Answer 1", "temper", "tem", "six", "one"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_5_attempt, 22, 4, "Answer 4", "tem", "five", "six", "attempt"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_6_estimate, 22, 5, "Answer 1", "estimate", "attempt", "ten", "five"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_7_temporary, 22, 6, "Answer 3", "six", "attempt", "temporary", "ten"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_8_item, 22, 7, "Answer 2", "ten", "item", "five", "six"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_9_customer, 22, 8, "Answer 4", "five", "six", "ten", "customer"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_10_system, 22, 9, "Answer 2", "customer", "system", "six", "ten"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_11_contemplate, 22, 10, "Answer 3", "ten", "system", "contemplate", "five"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_12_tomorrow, 22, 11, "Answer 1", "tomorrow", "system", "six", "estimate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_13_demand, 22, 12, "Answer 4", "estimate", "system", "six", "demand"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_14_seldom, 22, 13, "Answer 2", "system", "seldom", "five", "tomorrow"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_15_domestic, 22, 14, "Answer 3", "tomorrow", "six", "domestic", "five"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_16_demonstrate, 22, 15, "Answer 1", "demonstrate", "domestic", "demand", "ten"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_17_freedom, 22, 16, "Answer 4", "domestic", "tomorrow", "seldom", "freedom"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_18_damage, 22, 17, "Answer 2", "freedom", "damage", "system", "seldom"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_19_demonstration, 22, 18, "Answer 3", "damage", "tomorrow", "demonstration", "ten"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_20_random, 22, 19, "Answer 1", "random", "damage", "five", "freedom"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_21_medium, 22, 20, "Answer 4", "random", "damage", "system", "medium"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_22_dearmrs, 22, 21, "Answer 2", "medium", "dearmrs", "freedom", "seldom"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_23_dearmiss, 22, 22, "Answer 3", "system", "dearmrs", "dearmiss", "random"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_24_cordiallyyours, 22, 23, "Answer 1", "cordiallyyours", "dearmrs", "medium", "damage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_25_dearmr, 22, 24, "Answer 4", "freedom", "cordiallyyours", "seldom", "dearmr"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_26_dearms, 22, 25, "Answer 2", "dearmr", "dearms", "random", "medium"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_27_yourssincerely, 22, 26, "Answer 3", "dearmrs", "dearms", "yourssincerely", "system"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_28_toknow, 22, 27, "Answer 1", "toknow", "damage", "freedom", "random"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_29_tome, 22, 28, "Answer 4", "toknow", "damage", "system", "tome"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_30_tomake, 22, 29, "Answer 2", "random", "tomake", "freedom", "seldom"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_31_sunday, 22, 30, "Answer 3", "domestic", "tomake", "sunday", "toknow"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_32_wednesday, 22, 31, "Answer 1", "wednesday", "sunday", "tome", "system"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_33_friday, 22, 32, "Answer 4", "damage", "wednesday", "toknow", "friday"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_34_monday, 22, 33, "Answer 2", "wednesday", "monday", "random", "seldom"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_35_thursday, 22, 34, "Answer 3", "monday", "toknow", "thursday", "system"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_36_saturday, 22, 35, "Answer 1", "saturday", "tome", "domestic", "toknow"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_37_tuesday, 22, 36, "Answer 4", "monday", "toknow", "random", "tuesday"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_38_january, 22, 37, "Answer 2", "tuesday", "january", "sunday", "six"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_39_may, 22, 38, "Answer 3", "system", "seldom", "may", "tuesday"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_40_september, 22, 39, "Answer 1", "september", "may", "freedom", "wednesday"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_41_february, 22, 40, "Answer 4", "saturday", "toknow", "may", "february"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_42_june, 22, 41, "Answer 2", "february", "june", "thursday", "system"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_43_october, 22, 42, "Answer 3", "monday", "june", "october", "may"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_44_march, 22, 43, "Answer 1", "march", "may", "toknow", "freedom"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_45_july, 22, 44, "Answer 4", "march", "september", "system", "july"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_46_november, 22, 45, "Answer 2", "may", "november", "march", "june"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_47_april, 22, 46, "Answer 3", "november", "toknow", "april", "july"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_48_august, 22, 47, "Answer 1", "august", "april", "february", "march"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson22_asset_49_december, 22, 48, "Answer 4", "april", "june", "toknow", "december"));

        // Lesson 23
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_1_acknowledge, 23, 1, "Answer 3", "acknowledge", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_2_time, 23, 2, "Answer 1", "time", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_3_organize, 23, 3, "Answer 4", "organize", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_4_general, 23, 4, "Answer 2", "general", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_5_question, 23, 5, "Answer 1", "question", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_6_over, 23, 6, "Answer 3", "over", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_7_oversight, 23, 7, "Answer 2", "oversight", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_8_overdo, 23, 8, "Answer 4", "overdo", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_9_overcame, 23, 9, "Answer 1", "overcame", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_10_def, 23, 10, "Answer 2", "def", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_11_defy, 23, 11, "Answer 4", "defy", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_12_define, 23, 12, "Answer 1", "define", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_13_differ, 23, 13, "Answer 2", "differ", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_14_defied, 23, 14, "Answer 3", "defied", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_15_definite, 23, 15, "Answer 4", "definite", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_16_different, 23, 16, "Answer 1", "different", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_17_defeat, 23, 17, "Answer 2", "defeat", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_18_defect, 23, 18, "Answer 3", "defect", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_19_difference, 23, 19, "Answer 4", "difference", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_20_divide, 23, 20, "Answer 1", "divide", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_21_dividend, 23, 21, "Answer 2", "dividend", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_22_devote, 23, 22, "Answer 3", "devote", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_23_division, 23, 23, "Answer 4", "division", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_24_develop, 23, 24, "Answer 1", "develop", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_25_devotion, 23, 25, "Answer 2", "devotion", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_26_create, 23, 26, "Answer 3", "create", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_27_appropriate, 23, 27, "Answer 4", "appropriate", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_28_piano, 23, 28, "Answer 1", "piano", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_29_area, 23, 29, "Answer 2", "area", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_30_appreciate, 23, 30, "Answer 3", "appreciate", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_31_brilliant, 23, 31, "Answer 4", "brilliant", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_32_recreation, 23, 32, "Answer 1", "recreation", "acknowledgement", "acquire", "acquired"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson23_asset_33_aviation, 23, 33, "Answer 2", "aviation", "acknowledgement", "acquire", "acquired"));

        // Lesson 24
        answerItemList.add(new AnswerListItem(R.drawable.lesson24_asset_1, 24, 1, "Answer 1", "R L K G", "G L K R", "L K R G", "K R G L"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson24_asset_3, 24, 2, "Answer 3", "G-l R-k K-r", "G-l K-r R-k", "K-r R-k G-l", "K-r K-r G-l"));

        // Lesson 25
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_1_envelope, 25, 1, "Answer 2", "envelope", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_2_state, 25, 2, "Answer 3", "state", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_3_success, 25, 3, "Answer 1", "success", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_4_difficult, 25, 4, "Answer 4", "difficult", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_5_satisfy, 25, 5, "Answer 2", "satisfy", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_6_wish, 25, 6, "Answer 3", "wish", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_7_progress, 25, 7, "Answer 1", "progress", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_8_request, 25, 8, "Answer 4", "request", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_9_under, 25, 9, "Answer 2", "under", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_10_underneath, 25, 10, "Answer 3", "underneath", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_11_undertake, 25, 11, "Answer 1", "undertake", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_12_undermine, 25, 12, "Answer 4", "undermine", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_13_newyork, 25, 13, "Answer 2", "newyork", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_14_boston, 25, 14, "Answer 3", "boston", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_15_losangeles, 25, 15, "Answer 1", "losangeles", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_16_chicago, 25, 16, "Answer 4", "chicago", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_17_philadelphia, 25, 17, "Answer 2", "philadelphia", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_18_stlouis, 25, 18, "Answer 3", "stlouis", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_19_michigan, 25, 19, "Answer 1", "michigan", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_20_massahusetts, 25, 20, "Answer 4", "massahusetts", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_21_missouri, 25, 21, "Answer 2", "missouri", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_22_illinois, 25, 22, "Answer 3", "illinois", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_23_pennsylvania, 25, 23, "Answer 1", "pennsylvania", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_24_california, 25, 24, "Answer 4", "california", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_25_assoonas, 25, 25, "Answer 2", "assoonas", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_26_todo, 25, 26, "Answer 3", "todo", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_27_letus, 25, 27, "Answer 1", "letus", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_28_assoonnaspossible, 25, 28, "Answer 4", "assoonnaspossible", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_29_ofcourse, 25, 29, "Answer 2", "ofcourse", "enveloped", "file", "files"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson25_asset_30_hope, 25, 30, "Answer 3", "hope", "enveloped", "file", "files"));

        // Lesson 26
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

        // Lesson 26
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_1_signs, 26, 1, "Answer 3", "symbols", "marks", "signs", "alerts"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_2_science, 26, 2, "Answer 1", "science", "symbols", "alerts", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_3_compliance, 26, 3, "Answer 4", "alerts", "symbols", "marks", "compliance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_4_dial, 26, 4, "Answer 2", "symbols", "dial", "alerts", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_5_client, 26, 5, "Answer 3", "marks", "alerts", "client", "symbols"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_6_reliance, 26, 6, "Answer 1", "reliance", "alerts", "marks", "symbols"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_7_prior, 26, 7, "Answer 4", "symbols", "alerts", "marks", "prior"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_8_quiet, 26, 8, "Answer 2", "alerts", "quiet", "marks", "symbols"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_9_appliances, 26, 9, "Answer 3", "marks", "alerts", "appliances", "symbols"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset__10_enjoy, 26, 10, "Answer 1", "enjoy", "symbols", "alerts", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_11_engagement, 26, 11, "Answer 4", "marks", "symbols", "alerts", "engagement"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_12_endanger, 26, 12, "Answer 2", "alerts", "endanger", "symbols", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_13_engrave, 26, 13, "Answer 3", "symbols", "alerts", "engrave", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_14_engine, 26, 14, "Answer 1", "engine", "symbols", "marks", "alerts"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_15_encourage, 26, 15, "Answer 4", "marks", "symbols", "alerts", "encourage"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_16_endeavor, 26, 16, "Answer 2", "endeavor", "marks", "symbols", "alerts"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_17_enlarge, 26, 17, "Answer 3", "symbols", "marks", "enlarge", "alerts"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_18_encouragement, 26, 18, "Answer 1", "encouragement", "alerts", "symbols", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_19_until, 26, 19, "Answer 4", "symbols", "alerts", "marks", "until"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_20_unfair, 26, 20, "Answer 2", "unfair", "symbols", "marks", "alerts"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_21_unfilled, 26, 21, "Answer 3", "alerts", "symbols", "unfilled", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_22_unless, 26, 22, "Answer 1", "unless", "symbols", "marks", "alerts"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_23_unpaid, 26, 23, "Answer 4", "marks", "symbols", "alerts", "unpaid"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_24_unreasonable, 26, 24, "Answer 2", "symbols", "unreasonable", "marks", "alerts"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_25_undue, 26, 25, "Answer 3", "alerts", "marks", "undue", "symbols"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_26_uncertain, 26, 26, "Answer 1", "uncertain", "alerts", "symbols", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_27_undoubtedly, 26, 27, "Answer 4", "symbols", "alerts", "marks", "undoubtedly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_28_innovation, 26, 28, "Answer 2", "marks", "innovation", "symbols", "alerts"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_29_enact, 26, 29, "Answer 3", "symbols", "marks", "enact", "alerts"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_30_unable, 26, 30, "Answer 1", "unable", "alerts", "marks", "symbols"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_31_morethan, 26, 31, "Answer 4", "symbols", "alerts", "marks", "morethan"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_32_tous, 26, 32, "Answer 2", "tous", "marks", "symbols", "alerts"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_33_yourorder, 26, 33, "Answer 3", "symbols", "alerts", "yourorder", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_34_wehope, 26, 34, "Answer 1", "wehope", "symbols", "alerts", "marks"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_35_letme, 26, 35, "Answer 4", "symbols", "alerts", "marks", "letme"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson26_asset_36_youordered, 26, 36, "Answer 2", "alerts", "youordered", "symbols", "marks"));

        // Lesson 27
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_1_particular, 27, 1, "Answer 2", "practical", "particular", "particularly", "particulate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_2_speak, 27, 2, "Answer 3", "speak", "sneak", "spark", "spike"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_3_newspaper, 27, 3, "Answer 1", "newspaper", "newsletter", "newsman", "newborn"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_4_probable, 27, 4, "Answer 4", "probably", "problem", "probation", "probable"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_5_subject, 27, 5, "Answer 1", "subject", "sublet", "subside", "submit"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_6_opinion, 27, 6, "Answer 3", "open", "option", "opinion", "oppose"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_7_regular, 27, 7, "Answer 2", "regulate", "regular", "region", "register"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_8_regard, 27, 8, "Answer 4", "region", "register", "regular", "regard"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_9_idea, 27, 9, "Answer 1", "idea", "ideal", "idle", "identify"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_11_seen, 27, 10, "Answer 2", "scene", "seen", "seed", "seal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_12_sing, 27, 11, "Answer 3", "song", "sink", "sing", "sign"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_13_bring, 27, 12, "Answer 4", "brink", "bright", "brittle", "bring"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_14_length, 27, 13, "Answer 2", "lent", "length", "linger", "long"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_15_sang, 27, 14, "Answer 3", "song", "sing", "sang", "sunk"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_16_rang, 27, 15, "Answer 1", "rang", "ring", "rung", "run"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_17_strength, 27, 16, "Answer 4", "string", "strange", "stretch", "strength"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_18_song, 27, 17, "Answer 2", "sing", "song", "sang", "sign"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_19_wrong, 27, 18, "Answer 3", "ring", "wrote", "wrong", "wring"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_20_single, 27, 19, "Answer 1", "single", "signal", "sing", "singer"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_22_seem, 27, 20, "Answer 2", "seam", "seem", "seen", "same"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_23_sink, 27, 21, "Answer 4", "sing", "sank", "sunk", "sink"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_24_rank, 27, 22, "Answer 1", "rank", "rink", "ring", "rang"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_25_drink, 27, 23, "Answer 3", "drip", "drank", "drink", "drone"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_26_uncle, 27, 24, "Answer 2", "uncut", "uncle", "under", "unit"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_27_frank, 27, 25, "Answer 4", "fringe", "frame", "free", "frank"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_28_shrink, 27, 26, "Answer 1", "shrink", "shrunk", "shine", "shrank"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_29_banquet, 27, 27, "Answer 3", "bank", "banish", "banquet", "banner"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_30_bank, 27, 28, "Answer 2", "bark", "bank", "band", "back"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_31_ink, 27, 29, "Answer 4", "inch", "in", "inn", "ink"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_32_anxious, 27, 30, "Answer 1", "anxious", "anxiety", "anchor", "announce"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_33_addition, 27, 31, "Answer 2", "addition", "addict", "added", "address"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_34_quotation, 27, 32, "Answer 3", "quote", "quota", "quotation", "quorum"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_35_permission, 27, 33, "Answer 4", "permit", "permanent", "persuade", "permission"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_36_edition, 27, 34, "Answer 1", "edition", "editor", "educate", "edible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_37_combination, 27, 35, "Answer 2", "combine", "combination", "combat", "comfort"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_38_commission, 27, 36, "Answer 3", "commit", "common", "commission", "commence"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_39_notation, 27, 37, "Answer 4", "notice", "notary", "noted", "notation"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_40_reputation, 27, 38, "Answer 1", "reputation", "repeater", "republic", "report"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson27_asset_41_estimation, 27, 39, "Answer 2", "esteem", "estimation", "estimate", "estate"));

        // Lesson 28
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_1_ahead, 28, 1, "Answer 3", "head", "hair", "ahead", "heat"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_2_awaken, 28, 2, "Answer 1", "awaken", "awake", "awoke", "award"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_3_awoke, 28, 3, "Answer 4", "award", "awaken", "awake", "awoke"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_4_away, 28, 4, "Answer 2", "award", "away", "awake", "ahead"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_5_awakened, 28, 5, "Answer 3", "awake", "award", "awakened", "awoke"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_6_award, 28, 6, "Answer 4", "awake", "awakened", "ahead", "award"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_7_miss, 28, 7, "Answer 2", "mix", "miss", "mass", "mess"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_8_mix, 28, 8, "Answer 1", "mix", "miss", "fix", "fees"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_9_fees, 28, 9, "Answer 3", "fix", "miss", "fees", "tax"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_10_fix, 28, 10, "Answer 2", "fees", "fix", "mix", "tax"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_11_tax, 28, 11, "Answer 4", "fix", "fees", "indexes", "tax"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_12_relax, 28, 12, "Answer 1", "relax", "relaxation", "relaxes", "box"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_13_indexes, 28, 13, "Answer 2", "tax", "indexes", "relaxes", "complex"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_14_taxes, 28, 14, "Answer 3", "indexes", "tax", "taxes", "relaxation"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_15_relaxes, 28, 15, "Answer 4", "relaxation", "taxes", "box", "relaxes"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_16_approximate, 28, 16, "Answer 2", "relax", "approximate", "complex", "box"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_17_box, 28, 17, "Answer 1", "box", "complex", "approximate", "relax"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_18_relaxation, 28, 18, "Answer 2", "relaxes", "relaxation", "relax", "approximate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_19_complex, 28, 19, "Answer 3", "box", "approximate", "complex", "relaxation"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_20_fun, 28, 20, "Answer 1", "fun", "done", "son", "sum"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_21_done, 28, 21, "Answer 2", "fun", "done", "begun", "become"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_22_lunch, 28, 22, "Answer 4", "son", "fun", "sum", "lunch"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_23_begun, 28, 23, "Answer 1", "begun", "done", "fun", "become"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_24_son, 28, 24, "Answer 3", "done", "fun", "son", "sum"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_25_front, 28, 25, "Answer 2", "sum", "front", "son", "done"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_26_sum, 28, 26, "Answer 1", "sum", "son", "fun", "lunch"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_27_come, 28, 27, "Answer 4", "become", "income", "become", "come"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_28_income, 28, 28, "Answer 2", "become", "income", "come", "column"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_29_summer, 28, 29, "Answer 3", "sum", "son", "summer", "income"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_30_become, 28, 30, "Answer 1", "become", "income", "come", "begun"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_31_column, 28, 31, "Answer 4", "come", "become", "income", "column"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_32_rush, 28, 32, "Answer 2", "touch", "rush", "much", "budget"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_33_touch, 28, 33, "Answer 1", "touch", "rush", "much", "budget"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_34_judged, 28, 34, "Answer 3", "brushed", "budget", "judged", "much"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_35_brushed, 28, 35, "Answer 2", "budget", "brushed", "judged", "touch"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_36_much, 28, 36, "Answer 4", "judged", "budget", "rush", "much"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson28_asset_37_budget, 28, 37, "Answer 1", "budget", "rush", "touch", "judged"));

        // Lesson 29
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

        // Lesson 30
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_1_b, 30, 1, "Answer 2", "a", "b", "c", "d"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_2_v, 30, 2, "Answer 2", "a", "v", "c", "d"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_3_p, 30, 3, "Answer 2", "a", "p", "c", "d"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_4_f, 30, 4, "Answer 2", "a", "f", "c", "d"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_5_s, 30, 5, "Answer 2", "a", "s", "c", "d"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_7_pr, 30, 6, "Answer 2", "a", "pr", "c", "d"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_8_p1, 30, 7, "Answer 2", "a", "p1", "c", "d"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_9_br, 30, 8, "Answer 2", "a", "br", "c", "d"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_10_bl, 30, 9, "Answer 2", "a", "bl", "c", "d"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_12_fr, 30, 10, "Answer 2", "a", "fr", "c", "d"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson30_asset_13_fl, 30, 11, "Answer 2", "a", "fl", "c", "d"));

        // Lesson 31
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_1_never, 31, 1, "Answer 3", "know", "knew", "never", "nevermind"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_2_throughout, 31, 2, "Answer 2", "though", "throughout", "thought", "thorough"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_3_govern, 31, 3, "Answer 4", "government", "governor", "governing", "govern"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_4_quantity, 31, 4, "Answer 1", "quantity", "quality", "equal", "amount"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_5_object, 31, 5, "Answer 2", "subject", "object", "objective", "objection"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_6_correspond, 31, 6, "Answer 3", "correspondence", "respond", "correspond", "responsible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_7_executive, 31, 7, "Answer 4", "execute", "execution", "executed", "executive"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_8_character, 31, 8, "Answer 1", "character", "characterize", "cartoon", "letter"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_9_failure, 31, 9, "Answer 3", "fail", "failed", "failure", "failing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_10_feature, 31, 10, "Answer 2", "future", "feature", "feather", "fate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_11_nature, 31, 11, "Answer 4", "natural", "nation", "native", "nature"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_12_future, 31, 12, "Answer 1", "future", "feature", "fate", "further"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_13_featured, 31, 13, "Answer 3", "feature", "future", "featured", "feather"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_14_natural, 31, 14, "Answer 2", "nature", "natural", "nation", "native"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_15_actual, 31, 15, "Answer 4", "fact", "action", "act", "actual"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_16_schedule, 31, 16, "Answer 2", "school", "schedule", "shed", "scheme"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_17_equal, 31, 17, "Answer 1", "equal", "equally", "equation", "equator"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_18_gradual, 31, 18, "Answer 3", "graduate", "grade", "gradual", "graduation"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_19_scheduled, 31, 19, "Answer 4", "schedule", "shed", "scheduleable", "scheduled"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson31_asset_20_equally, 31, 20, "Answer 2", "equal", "equally", "equator", "equation"));

        // Lesson 32
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

        // Lesson 32
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_1_steady, 32, 1, "Answer 2", "steel", "steady", "steal", "stead"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_2_steadily, 32, 2, "Answer 1", "steadily", "steel", "steal", "stead"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_3_readily, 32, 3, "Answer 1", "readily", "ready", "read", "reader"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_4_temporarily, 32, 4, "Answer 2", "temporary", "temporarily", "temper", "temporal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_5_family, 32, 5, "Answer 1", "family", "fame", "familiar", "familiarity"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_6_easily, 32, 6, "Answer 1", "easily", "easy", "ease", "easier"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_7_hastily, 32, 7, "Answer 3", "haste", "hasty", "hastily", "has"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_8_families, 32, 8, "Answer 4", "family", "familiar", "fame", "families"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_9_alter, 32, 9, "Answer 1", "alter", "altar", "alert", "altered"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_10_altogether, 32, 10, "Answer 1", "altogether", "all", "together", "other"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_11_also, 32, 11, "Answer 3", "always", "ally", "also", "alas"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_12_altered, 32, 12, "Answer 4", "alter", "alert", "altering", "altered"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_13_almost, 32, 13, "Answer 1", "almost", "all", "mostly", "mostly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_14_almanac, 32, 14, "Answer 1", "almanac", "all", "manual", "map"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_15_discuss, 32, 15, "Answer 3", "talk", "debate", "discuss", "discussing"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_16_discourage, 32, 16, "Answer 1", "discourage", "courage", "encourage", "course"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_17_distance, 32, 17, "Answer 4", "distant", "instance", "stance", "distance"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_18_discover, 32, 18, "Answer 1", "discover", "discovering", "cover", "over"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_19_dismiss, 32, 19, "Answer 3", "miss", "mist", "dismiss", "mission"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_20_dispose, 32, 20, "Answer 1", "dispose", "pose", "posing", "poses"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_21_describe, 32, 21, "Answer 1", "describe", "scribe", "script", "scribble"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_22_despite, 32, 22, "Answer 3", "site", "spite", "despite", "sprite"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_23_desperate, 32, 23, "Answer 4", "separate", "spire", "spare", "desperate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_24_description, 32, 24, "Answer 2", "scribe", "description", "script", "prescription"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson32_asset_25_destroy, 32, 25, "Answer 1", "destroy", "story", "toy", "try"));

        // Lesson 33
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_1_forget, 33, 1, "Answer 3", "forget", "form", "furnish", "further"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_2_form, 33, 2, "Answer 2", "forget", "form", "further", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_3_forerunner, 33, 3, "Answer 4", "further", "furnish", "form", "forerunner"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_4_forgive, 33, 4, "Answer 1", "forgive", "form", "forget", "further"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_5_inform, 33, 5, "Answer 2", "forget", "inform", "form", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_6_forlorn, 33, 6, "Answer 3", "form", "further", "forlorn", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_7_effort, 33, 7, "Answer 1", "effort", "form", "forget", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_8_information, 33, 8, "Answer 4", "forget", "form", "furnish", "information"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_9_forever, 33, 9, "Answer 2", "forget", "forever", "form", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_10_furnish, 33, 10, "Answer 3", "form", "forget", "furnish", "further"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_11_furniture, 33, 11, "Answer 1", "furniture", "form", "forget", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_12_further, 33, 12, "Answer 4", "forget", "form", "furnish", "further"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_13_furnished, 33, 13, "Answer 2", "forget", "furnished", "form", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_14_furnace, 33, 14, "Answer 1", "furnace", "form", "forget", "further"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_15_furthermore, 33, 15, "Answer 3", "form", "forget", "furthermore", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_16_daysago, 33, 16, "Answer 4", "forget", "form", "furnish", "days ago"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_17_yearsago, 33, 17, "Answer 2", "forget", "years ago", "form", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_18_severaldaysago, 33, 18, "Answer 3", "form", "forget", "several days ago", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_19_weeksago, 33, 19, "Answer 1", "weeks ago", "form", "forget", "furnish"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_20_monthsago, 33, 20, "Answer 4", "forget", "form", "furnish", "months ago"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson33_asset_21_fewdaysago, 33, 21, "Answer 2", "forget", "few days ago", "form", "furnish"));

        // Lesson 34
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_1_iwant, 34, 1, "Answer 3", "he want", "i want", "she want", "we want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_2_iwanted, 34, 2, "Answer 2", "i want", "he want", "she want", "we want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_3_ifyouwant, 34, 3, "Answer 4", "he want", "we want", "she want", "i want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_4_youwant, 34, 4, "Answer 1", "youwant", "i want", "he want", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_5_hewants, 34, 5, "Answer 3", "i want", "she want", "he want", "we want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_6_doyouwant, 34, 6, "Answer 2", "i want", "doyouwant", "she want", "he want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_7_report, 34, 7, "Answer 1", "report", "i want", "he want", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_8_quart, 34, 8, "Answer 4", "i want", "he want", "we want", "quart"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_9_sort, 34, 9, "Answer 3", "he want", "i want", "sort", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_10_export, 34, 10, "Answer 2", "i want", "export", "he want", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_11_quarterly, 34, 11, "Answer 1", "quarterly", "i want", "he want", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_12_portable, 34, 12, "Answer 3", "i want", "he want", "portable", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_13_turn, 34, 13, "Answer 2", "i want", "turn", "he want", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_14_term, 34, 14, "Answer 4", "i want", "he want", "she want", "term"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_15_southern, 34, 15, "Answer 1", "southern", "i want", "he want", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_16_return, 34, 16, "Answer 2", "i want", "return", "he want", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_17_termed, 34, 17, "Answer 3", "i want", "he want", "termed", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_18_thermometer, 34, 18, "Answer 4", "i want", "he want", "she want", "thermometer"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_19_western, 34, 19, "Answer 1", "western", "i want", "he want", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_20_determined, 34, 20, "Answer 3", "i want", "he want", "determined", "she want"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson34_asset_21_modern, 34, 21, "Answer 2", "i want", "modern", "he want", "she want"));

        // Lesson 35
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_1_interest, 35, 1, "Answer 2", "intersting", "interest", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_2_international, 35, 2, "Answer 4", "inter", "interact", "intersting", "international"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_3_interrupt, 35, 3, "Answer 1", "interrupt", "interest", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_4_interview, 35, 4, "Answer 3", "interact", "inter", "interview", "intersting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_5_interval, 35, 5, "Answer 2", "intersting", "interval", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_6_internal, 35, 6, "Answer 4", "inter", "interact", "intersting", "internal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_7_introduce, 35, 7, "Answer 3", "interact", "inter", "introduce", "intersting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_8_intruder, 35, 8, "Answer 1", "intruder", "interest", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_9_intricate, 35, 9, "Answer 2", "intersting", "intricate", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_10_entering, 35, 10, "Answer 4", "inter", "interact", "intersting", "entering"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_11_entertain, 35, 11, "Answer 3", "interact", "inter", "entertain", "intersting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_12_enterprise, 35, 12, "Answer 4", "intersting", "inter", "interact", "enterprise"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_13_entered, 35, 13, "Answer 2", "intersting", "entered", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_14_entertainment, 35, 14, "Answer 4", "inter", "interact", "intersting", "entertainment"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_15_enterprising, 35, 15, "Answer 3", "interact", "inter", "enterprising", "intersting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_16_entrance, 35, 16, "Answer 2", "intersting", "entrance", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_17_entrances, 35, 17, "Answer 1", "entrances", "intersting", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_18_entrant, 35, 18, "Answer 4", "inter", "interact", "intersting", "entrant"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_19_savings, 35, 19, "Answer 3", "interact", "inter", "savings", "intersting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_20_furnishings, 35, 20, "Answer 2", "intersting", "furnishings", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_21_proceeding, 35, 21, "Answer 4", "inter", "interact", "intersting", "proceeding"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_22_opening, 35, 22, "Answer 3", "interact", "inter", "opening", "intersting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_23_earnings, 35, 23, "Answer 2", "intersting", "earnings", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_24_hearings, 35, 24, "Answer 4", "inter", "interact", "intersting", "hearings"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_25_oneofthe, 35, 25, "Answer 3", "interact", "inter", "oneofthe", "intersting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_26_someofthe, 35, 26, "Answer 2", "intersting", "someofthe", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_27_manyofthe, 35, 27, "Answer 4", "inter", "interact", "intersting", "manyofthe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_28_oneofthem, 35, 28, "Answer 3", "interact", "inter", "oneofthem", "intersting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_29_uptodate, 35, 29, "Answer 2", "intersting", "uptodate", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_30_inthefuture, 35, 30, "Answer 4", "inter", "interact", "intersting", "inthefuture"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_31_someofour, 35, 31, "Answer 3", "interact", "inter", "someofour", "intersting"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_32_intheworld, 35, 32, "Answer 2", "intersting", "intheworld", "inter", "interact"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson35_asset_33_twoorthree, 35, 33, "Answer 4", "inter", "interact", "intersting", "twoorthree"));

        // Lesson 36
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_1_nature, 36, 1, "Answer 2", "night", "nature", "need", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_2_procedure, 36, 2, "Answer 4", "night", "nature", "need", "procedure"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_3_creature, 36, 3, "Answer 3", "night", "creature", "nature", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_4_annual, 36, 4, "Answer 1", "annual", "nature", "need", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_5_gradual, 36, 5, "Answer 4", "night", "nature", "need", "gradual"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_6_equal, 36, 6, "Answer 3", "night", "equal", "nature", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_7_pressure, 36, 7, "Answer 2", "night", "pressure", "need", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_8_treasure, 36, 8, "Answer 4", "night", "nature", "need", "treasure"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_9_ensure, 36, 9, "Answer 3", "night", "ensure", "nature", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_10_casual, 36, 10, "Answer 4", "night", "nature", "need", "casual"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_11_visual, 36, 11, "Answer 3", "visual", "night", "need", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_12_visually, 36, 12, "Answer 4", "night", "nature", "need", "visually"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_13_o, 36, 13, "Answer 2", "night", "o", "need", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_14_on, 36, 14, "Answer 4", "night", "nature", "need", "on"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_15_sho, 36, 15, "Answer 3", "night", "sho", "nature", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_16_non, 36, 16, "Answer 4", "night", "nature", "need", "non"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_18_oo, 36, 17, "Answer 3", "oo", "night", "nature", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_19_noo, 36, 18, "Answer 2", "night", "noo", "need", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_20_noom, 36, 19, "Answer 4", "night", "nature", "need", "noom"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_22_hard, 36, 20, "Answer 3", "hard", "night", "nature", "nail"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson36_asset_23_hailed, 36, 21, "Answer 4", "night", "nature", "need", "hailed"));

        // Lesson 37
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_1_knowingly, 37, 1, "Answer 2", "know", "knowingly", "now", "noway"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_2_increasingly, 37, 2, "Answer 4", "know", "know", "now", "increasingly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_3_seemingly, 37, 3, "Answer 3", "know", "knowingly", "seemingly", "noway"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_4_accordingly, 37, 4, "Answer 4", "know", "knowingly", "now", "accordingly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_5_surprisingly, 37, 5, "Answer 4", "know", "knowingly", "now", "surprisingly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_6_exceedingly, 37, 6, "Answer 4", "know", "knowingly", "now", "exceedingly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_7_impose, 37, 7, "Answer 4", "know", "knowingly", "now", "impose"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_8_import, 37, 8, "Answer 4", "know", "knowingly", "now", "import"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_9_improvement, 37, 9, "Answer 4", "know", "knowingly", "now", "improvement"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_10_impossible, 37, 10, "Answer 4", "know", "knowingly", "now", "impossible"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_11_impartial, 37, 11, "Answer 4", "know", "knowingly", "now", "impartial"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_12_impression, 37, 12, "Answer 4", "know", "knowingly", "now", "impression"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_13_employ, 37, 13, "Answer 4", "know", "knowingly", "now", "employ"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_14_emphasis, 37, 14, "Answer 4", "know", "knowingly", "now", "emphasis"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_15_empire, 37, 15, "Answer 4", "know", "knowingly", "now", "empire"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_16_employment, 37, 16, "Answer 4", "know", "knowingly", "now", "employment"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_17_emphasize, 37, 17, "Answer 4", "know", "knowingly", "now", "emphasize"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_18_embarrass, 37, 18, "Answer 4", "know", "knowingly", "now", "embarrass"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_19_immodest, 37, 19, "Answer 4", "know", "knowingly", "now", "immodest"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_20_immortal, 37, 20, "Answer 4", "know", "knowingly", "now", "immortal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_21_emotional, 37, 21, "Answer 4", "know", "knowingly", "now", "emotional"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_22_serious, 37, 22, "Answer 4", "know", "knowingly", "now", "serious"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_23_period, 37, 23, "Answer 4", "know", "knowingly", "now", "period"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_24_situate, 37, 24, "Answer 4", "know", "knowingly", "now", "situate"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_25_obvious, 37, 25, "Answer 4", "know", "knowingly", "now", "obvious"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_26_ideal, 37, 26, "Answer 4", "know", "knowingly", "now", "ideal"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_27_situated, 37, 27, "Answer 4", "know", "knowingly", "now", "situated"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_28_various, 37, 28, "Answer 4", "know", "knowingly", "now", "various"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_29_theory, 37, 29, "Answer 4", "know", "knowingly", "now", "theory"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson37_asset_30_situation, 37, 30, "Answer 4", "know", "knowingly", "now", "situation"));

        // Lesson 38
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_1_friendship, 38, 1, "Answer 2", "friend", "friendship", "fried", "fry"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_2_membership, 38, 2, "Answer 1", "membership", "friend", "fry", "friend"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_3_scholarships, 38, 3, "Answer 3", "friend", "scholarships", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_4_hardship, 38, 4, "Answer 4", "friend", "fried", "friendship", "hardship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_5_ownership, 38, 5, "Answer 2", "friend", "ownership", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_6_relationships, 38, 6, "Answer 1", "relationships", "friend", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_7_submit, 38, 7, "Answer 3", "friend", "fried", "submit", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_8_subdivision, 38, 8, "Answer 2", "friend", "subdivision", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_9_suburban, 38, 9, "Answer 4", "friend", "fried", "friendship", "suburban"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_10_regulate, 38, 10, "Answer 1", "regulate", "friend", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_11_formulate, 38, 11, "Answer 3", "friend", "fried", "formulate", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_12_calculate, 38, 12, "Answer 2", "friend", "calculate", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_13_subscribe, 38, 13, "Answer 4", "friend", "fried", "friendship", "subscribe"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_14_substantiate, 38, 14, "Answer 1", "substantiate", "friend", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_15_sublease, 38, 15, "Answer 3", "friend", "fried", "sublease", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_16_congratulate, 38, 16, "Answer 2", "friend", "congratulate", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_17_speculated, 38, 17, "Answer 4", "friend", "fried", "friendship", "speculated"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_18_calculator, 38, 18, "Answer 3", "friend", "fried", "calculator", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_19_regulation, 38, 19, "Answer 2", "friend", "regulation", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_20_population, 38, 20, "Answer 1", "population", "friend", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_21_stipulation, 38, 21, "Answer 4", "friend", "fried", "friendship", "stipulation"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_22_accumulation, 38, 22, "Answer 3", "friend", "fried", "accumulation", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_23_insulation, 38, 23, "Answer 2", "friend", "insulation", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_24_congratulations, 38, 24, "Answer 1", "congratulations", "friend", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_25_charity, 38, 25, "Answer 3", "friend", "fried", "charity", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_26_prosperity, 38, 26, "Answer 2", "friend", "prosperity", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_27_authorities, 38, 27, "Answer 4", "friend", "fried", "friendship", "authorities"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_28_majority, 38, 28, "Answer 3", "friend", "fried", "majority", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_29_sincerity, 38, 29, "Answer 1", "sincerity", "friend", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_30_securities, 38, 30, "Answer 2", "friend", "securities", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_31_maturity, 38, 31, "Answer 4", "friend", "fried", "friendship", "maturity"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_32_minority, 38, 32, "Answer 3", "friend", "fried", "minority", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson38_asset_33_integrity, 38, 33, "Answer 1", "integrity", "friend", "fried", "friendship"));

        // Lesson 39
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_1_ability, 39, 1, "Answer 3", "friend", "ability", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_2_locality, 39, 2, "Answer 1", "locality", "friend", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_3_qualities, 39, 3, "Answer 4", "friend", "friendship", "fried", "qualities"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_4_personality, 39, 4, "Answer 2", "friend", "personality", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_5_reliability, 39, 5, "Answer 3", "friend", "fried", "reliability", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_6_responsibilities, 39, 6, "Answer 1", "responsibilities", "friend", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_7_faculty, 39, 7, "Answer 4", "friend", "fried", "friendship", "faculty"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_8_loyalty, 39, 8, "Answer 2", "friend", "loyalty", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_9_penalty, 39, 9, "Answer 3", "friend", "fried", "penalty", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_10_herself, 39, 10, "Answer 1", "herself", "friend", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_11_itself, 39, 11, "Answer 4", "friend", "fried", "friendship", "itself"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_12_myself, 39, 12, "Answer 2", "friend", "myself", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_13_himself, 39, 13, "Answer 3", "friend", "fried", "himself", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_14_yourself, 39, 14, "Answer 1", "yourself", "friend", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_15_oneself, 39, 15, "Answer 4", "friend", "fried", "friendship", "oneself"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_16_themselves, 39, 16, "Answer 2", "friend", "themselves", "fried", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_17_ourselves, 39, 17, "Answer 3", "friend", "fried", "ourselves", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson39_asset_18_yourselves, 39, 18, "Answer 1", "yourselves", "friend", "fried", "friendship"));

        // Lesson 40
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_1_consequent, 40, 1, "Answer 3", "friend", "consequently", "consequent", "friendship"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_2_subsequent, 40, 2, "Answer 1", "subsequent", "friend", "consequently", "consequent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_3_eloquent, 40, 3, "Answer 4", "friend", "eloquent", "consequently", "consequent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_4_consequently, 40, 4, "Answer 2", "friend", "consequently", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_5_subsequently, 40, 5, "Answer 3", "friend", "subsequently", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_6_frequently, 40, 6, "Answer 1", "frequently", "friend", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_7_tribute, 40, 7, "Answer 4", "friend", "consequently", "consequent", "tribute"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_8_contribute, 40, 8, "Answer 2", "friend", "contribute", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_9_distribute, 40, 9, "Answer 3", "friend", "distribute", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_10_attribute, 40, 10, "Answer 1", "attribute", "friend", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_11_contributed, 40, 11, "Answer 4", "friend", "consequently", "consequent", "contributed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_12_distributor, 40, 12, "Answer 2", "friend", "distributor", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_13_attributes, 40, 13, "Answer 3", "friend", "attributes", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_14_contribution, 40, 14, "Answer 1", "contribution", "friend", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_15_distribution, 40, 15, "Answer 4", "friend", "consequently", "consequent", "distribution"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_16_require, 40, 16, "Answer 2", "friend", "require", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_17_inquire, 40, 17, "Answer 3", "friend", "inquire", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_18_inquiries, 40, 18, "Answer 1", "inquiries", "friend", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_19_requirement, 40, 19, "Answer 4", "friend", "consequently", "consequent", "requirement"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_20_inquired, 40, 20, "Answer 2", "friend", "inquired", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_21_acquire, 40, 21, "Answer 3", "friend", "acquire", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_22_substitute, 40, 22, "Answer 1", "substitute", "friend", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_23_institute, 40, 23, "Answer 4", "friend", "consequently", "consequent", "institute"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_24_constitute, 40, 24, "Answer 2", "friend", "constitute", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_25_substitution, 40, 25, "Answer 3", "friend", "substitution", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_26_institution, 40, 26, "Answer 1", "institution", "friend", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_27_constitution, 40, 27, "Answer 4", "friend", "consequently", "consequent", "constitution"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_28_aptitude, 40, 28, "Answer 2", "friend", "aptitude", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_29_gratitude, 40, 29, "Answer 3", "friend", "gratitude", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_30_latitude, 40, 30, "Answer 1", "latitude", "friend", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_31_psychology, 40, 31, "Answer 4", "friend", "consequently", "consequent", "psychology"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_32_sociology, 40, 32, "Answer 2", "friend", "sociology", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_33_apology, 40, 33, "Answer 3", "friend", "apology", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_34_pshychological, 40, 34, "Answer 1", "pshychological", "friend", "consequent", "eloquent"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_35_sociological, 40, 35, "Answer 4", "friend", "consequently", "consequent", "sociological"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson40_asset_36_apologies, 40, 36, "Answer 2", "friend", "apologies", "consequent", "eloquent"));

        // Lesson 41
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

        // Lesson 42
        answerItemList.add(new AnswerListItem(R.drawable.lesson42_asset_1_my, 42, 1, "Answer 2", "may", "my", "I", "we"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson42_asset_2_lie, 42, 2, "Answer 1", "lie", "may", "I", "we"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson42_asset_3_fight, 42, 3, "Answer 3", "may", "I", "fight", "we"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson42_asset_5_ow, 42, 4, "Answer 4", "may", "I", "we", "ow"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson42_asset_6_oi, 42, 5, "Answer 1", "oi", "may", "I", "we"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson42_asset_8_ith, 42, 6, "Answer 3", "may", "I", "ith", "we"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson42_asset_9_nt, 42, 7, "Answer 2", "may", "nt", "I", "we"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson42_asset_10_mt, 42, 8, "Answer 1", "mt", "may", "I", "we"));

        // Lesson 43
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_1_mistake, 43, 1, "Answer 2", "make", "mistake", "mistaken", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_2_misplaced, 43, 2, "Answer 4", "make", "mistakenly", "mistaken", "misplaced"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_3_misunderstand, 43, 3, "Answer 3", "make", "mistake", "misunderstand", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_4_misconception, 43, 4, "Answer 1", "misconception", "make", "mistake", "mistaken"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_5_mislaid, 43, 5, "Answer 3", "make", "mistake", "mislaid", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_6_misunderstood, 43, 6, "Answer 4", "make", "mistake", "mistaken", "misunderstood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_7_misprint, 43, 7, "Answer 2", "make", "misprint", "mistake", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_8_misapprehension, 43, 8, "Answer 3", "make", "mistake", "misapprehension", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_9_mystery, 43, 9, "Answer 1", "mystery", "make", "mistake", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_10_supervise, 43, 10, "Answer 4", "make", "mistake", "mistaken", "supervise"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_11_superintendent, 43, 11, "Answer 3", "make", "mistake", "superintendent", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_12_superior, 43, 12, "Answer 2", "make", "superior", "mistake", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_13_supervisor, 43, 13, "Answer 3", "make", "mistake", "supervisor", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_14_superhuman, 43, 14, "Answer 1", "superhuman", "make", "mistake", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_15_superb, 43, 15, "Answer 2", "make", "superb", "mistake", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_16_supervision, 43, 16, "Answer 4", "make", "mistake", "mistaken", "supervision"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_17_superimpose, 43, 17, "Answer 3", "make", "mistake", "superimpose", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_18_supersede, 43, 18, "Answer 1", "supersede", "make", "mistake", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_19_music, 43, 19, "Answer 4", "make", "mistake", "mistaken", "music"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_20_musical, 43, 20, "Answer 2", "make", "musical", "mistake", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_21_municipal, 43, 21, "Answer 3", "make", "mistake", "municipal", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_22_mutual, 43, 22, "Answer 1", "mutual", "make", "mistake", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_23_communicate, 43, 23, "Answer 2", "make", "communicate", "mistake", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_24_continue, 43, 24, "Answer 4", "make", "mistake", "mistakenly", "continue"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_26_monument, 43, 25, "Answer 3", "make", "mistake", "monument", "mistakenly"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson43_asset_27_discontinue, 43, 26, "Answer 1", "discontinue", "make", "mistake", "mistakenly"));

        // Lesson 44
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_1_selfmade, 44, 1, "Answer 3", "self", "selfie", "self made", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_2_selfaddressed, 44, 2, "Answer 4", "self", "selfie", "said", "selfaddressed"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_3_selfish, 44, 3, "Answer 2", "selfish", "self", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_4_selfcontrol, 44, 4, "Answer 3", "self", "selfcontrol", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_5_selfimprovement, 44, 5, "Answer 4", "self", "selfie", "said", "selfimprovement"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_6_selfishness, 44, 6, "Answer 2", "selfishness", "self", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_7_selfreliance, 44, 7, "Answer 3", "self", "selfreliance", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_8_selfexplanatory, 44, 8, "Answer 4", "self", "selfie", "said", "selfexplanatory"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_9_unselfishly, 44, 9, "Answer 2", "unselfishly", "self", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_10_circumstance, 44, 10, "Answer 3", "self", "circumstance", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_11_circumstances, 44, 11, "Answer 4", "self", "selfie", "said", "circumstances"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_12_circumstantial, 44, 12, "Answer 2", "circumstantial", "self", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_13_ratification, 44, 13, "Answer 3", "self", "ratification", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_14_notification, 44, 14, "Answer 4", "self", "selfie", "said", "notification"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_15_modifications, 44, 15, "Answer 2", "modifications", "self", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_16_classification, 44, 16, "Answer 3", "self", "classification", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_17_gratification, 44, 17, "Answer 4", "self", "selfie", "said", "gratification"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_18_qualifications, 44, 18, "Answer 2", "qualifications", "self", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_19_justification, 44, 19, "Answer 3", "self", "justification", "selfie", "said"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson44_asset_20_identification, 44, 20, "Answer 4", "self", "selfie", "said", "identification"));

        // Lesson 45
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_1_neighborhood, 45, 1, "Answer 3", "nail", "neighbor", "neighborhood", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_2_boyhood, 45, 2, "Answer 4", "nail", "hood", "neighbor", "boyhood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_3_likelihood, 45, 3, "Answer 1", "likelihood", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_4_childhood, 45, 4, "Answer 1", "childhood", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_5_girlhood, 45, 5, "Answer 1", "girlhood", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_6_parenthood, 45, 6, "Answer 2", "nail", "parenthood", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_7_onward, 45, 7, "Answer 4", "nail", "neighbor", "hood", "onward"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_8_outward, 45, 8, "Answer 2", "nail", "outward", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_9_rewarding, 45, 9, "Answer 1", "rewarding", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_10_backward, 45, 10, "Answer 2", "nail", "backward", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_11_upward, 45, 11, "Answer 3", "nail", "neighbor", "upward", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_12_forward, 45, 12, "Answer 1", "forward", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_13_afterward, 45, 13, "Answer 2", "nail", "afterward", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_14_inward, 45, 14, "Answer 3", "nail", "neighbor", "inward", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_15_forwarded, 45, 15, "Answer 1", "forwarded", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_16_result, 45, 16, "Answer 2", "nail", "result", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_17_consultant, 45, 17, "Answer 3", "nail", "neighbor", "consultant", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_18_cultured, 45, 18, "Answer 1", "cultured", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_19_consult, 45, 19, "Answer 2", "nail", "consult", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_20_ultimately, 45, 20, "Answer 3", "nail", "neighbor", "ultimately", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_21_culminate, 45, 21, "Answer 1", "culminate", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_22_insult, 45, 22, "Answer 2", "nail", "insult", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_23_multitude, 45, 23, "Answer 3", "nail", "neighbor", "multitude", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_24_simultaneous, 45, 24, "Answer 1", "simultaneous", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_25_600, 45, 25, "Answer 2", "nail", "600", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_26_5000000000, 45, 26, "Answer 3", "nail", "neighbor", "5000000000", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_27_severalhundred, 45, 27, "Answer 1", "severalhundred", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_28_600dollar, 45, 28, "Answer 2", "nail", "600dollar", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_29_adollar, 45, 29, "Answer 3", "nail", "neighbor", "adollar", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_30_fivepounds, 45, 30, "Answer 1", "fivepounds", "nail", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_31_8000000, 45, 31, "Answer 2", "nail", "8000000", "neighbor", "hood"));
        answerItemList.add(new AnswerListItem(R.drawable.lesson45_asset_32_8feet, 45, 32, "Answer 3", "nail", "neighbor", "8feet", "hood"));



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
        //CurrentQuestionNumber = 1;
        //NumberCorrectAnswer = 0;
        //LessonNumber = 0;

        //super.onBackPressed();
    }


}