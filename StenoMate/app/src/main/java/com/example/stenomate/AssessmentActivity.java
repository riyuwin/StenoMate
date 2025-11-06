package com.example.stenomate;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;

public class AssessmentActivity extends AppCompatActivity {

    ImageView ImageHolder;
    private Dialog countdownDialog;
    private TextView countdownTextView;
    TextView TimerNoId, QestionNoId;
    private CountDownTimer countDownTimer;
    LinearLayout TranslationKeyLinear, AnswerLinear;
    Button SubmitBtn;
    EditText AnswerEditText;
    ArrayList<AssessmentItem> assessmentList;
    int lesson_number, assessment_list_group_name;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        dbHelper = new MyDatabaseHelper(this);

        Intent intent = getIntent();
        lesson_number = intent.getIntExtra("lesson_number", 0);
        assessment_list_group_name = intent.getIntExtra("assessment_list_group_name", 0);

        //Toast.makeText(this, "Hey: " + assessment_list_group_name, Toast.LENGTH_SHORT).show();

        TranslationKeyLinear = findViewById(R.id.translationKeyLinear);
        AnswerLinear = findViewById(R.id.answerLinear);
        ImageHolder = findViewById(R.id.stenoImageHolder);
        TimerNoId = findViewById(R.id.timerNoId);
        SubmitBtn = findViewById(R.id.submitBtn);
        AnswerEditText = findViewById(R.id.answerEditText);
        QestionNoId = findViewById(R.id.questionNoId);

        QestionNoId.setText("Assessment No. " + lesson_number  +" - " + assessment_list_group_name);

        AnswerListGenerator();

        AssessmentItem selectedItem = getAssessmentItem(lesson_number, assessment_list_group_name);

        if (selectedItem != null) {
            ImageHolder.setImageResource(selectedItem.getImageResId());
        } else {
            Toast.makeText(this, "Invalid lesson/group combo", Toast.LENGTH_SHORT).show();
            finish();
            return;
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
            AnswerChecker(selectedItem, answer);
        });

        showTimerDialog(selectedItem);

    }

    private AssessmentItem getAssessmentItem(int lessonNum, int groupNum) {
        for (AssessmentItem item : assessmentList) {
            if (item.getLessonNumber() == lessonNum && item.getItemNumber() == groupNum) {
                return item;
            }
        }
        return null;
    }


    private void showTimerDialog(AssessmentItem selectedItem) {
        countdownDialog = new Dialog(this);
        countdownDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        countdownDialog.setContentView(R.layout.dialog_countdown);
        countdownDialog.setCancelable(false);

        countdownTextView = countdownDialog.findViewById(R.id.countdownTextView);
        countdownDialog.show();

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
                startTimer(selectedItem.getTimerMinutes()); // ✅ match selectedItem
            }
        }.start();
    }


    private void startTimer(float minutes) {
        long millisInFuture = (long) (minutes * 60 * 1000); // convert float minutes → milliseconds

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
                TranslationKeyLinear.setVisibility(View.GONE);
                AnswerLinear.setVisibility(View.VISIBLE);
            }
        }.start();
    }


    public void AnswerChecker(AssessmentItem selectedItem, String answer) {
        String correctAnswer = selectedItem.getAnswerKey();
        int similarity = calculateSimilarityPercentage(answer, correctAnswer);
        showConfirmationDialog(similarity, answer, selectedItem);
    }



    public int calculateSimilarityPercentage(String input, String correctAnswer) {
        input = input.trim().toLowerCase();
        correctAnswer = correctAnswer.trim().toLowerCase();

        int maxLength = Math.max(input.length(), correctAnswer.length());
        if (maxLength == 0) return 100;

        int distance = levenshteinDistance(input, correctAnswer);
        return (int) ((1 - (double) distance / maxLength) * 100);
    }

    public int levenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
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
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    private void showConfirmationDialog(float percentage, String answer, AssessmentItem selectedItem) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to submit? This action cannot be undone.")
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    insertAssessment(percentage, answer, selectedItem.getAnswerKey());
                })
                .setNegativeButton("Back", null)
                .show();
    }

//    private void showConfirmationDialog(float percentage, String answer) {
//        new androidx.appcompat.app.AlertDialog.Builder(this)
//                .setTitle("Confirmation")
//                .setMessage("Are you sure you want to submit? This action cannot be undone.")
//                .setCancelable(false)
//                .setPositiveButton("Confirm", (dialog, which) -> {
//                    insertAssessment(percentage, answer);
//                })
//                .setNegativeButton("Back", null)
//                .show();
//    }

//    public void insertAssessment(float percentage, String answer) {
//        boolean isInserted = dbHelper.insertAssessment(lesson_number, assessment_list_group_name, percentage, answer);
//        if (isInserted) {
//            Toast.makeText(this, "Assessment added for Lesson " + lesson_number, Toast.LENGTH_SHORT).show();
//            showResultDialog(percentage);
//        } else {
//            Toast.makeText(this, "Failed to add Assessment", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void insertAssessment(float percentage, String answer, String correctAnswer) {
        boolean isInserted = dbHelper.insertAssessment(lesson_number, assessment_list_group_name, percentage, answer);
        if (isInserted) {
            Toast.makeText(this, "Assessment added for Lesson " + lesson_number, Toast.LENGTH_SHORT).show();
            showResultDialog(percentage, correctAnswer, answer); // pass correct + user answer
        } else {
            Toast.makeText(this, "Failed to add Assessment", Toast.LENGTH_SHORT).show();
        }
    }


    private void showResultDialog(float percentage, String correctAnswer, String userAnswer) {
        String remarks = (percentage >= 75) ? "Status: ✅ Passed" : "Status: ❌ Failed";
        String message = "Your Answer:\n\n" + userAnswer +
                "\n\nAccuracy: " + percentage + "%" +
                "\n" + remarks;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Assessment Complete!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("View Attempts", (dialog, which) -> {
                    showAttemptsDialog();
                })
                .setNeutralButton("View Answer", (dialog, which) -> {
                    showAnswerDialog(correctAnswer, userAnswer, percentage);
                })
                .setNegativeButton("Back", (dialog, which) -> {
                    Intent intent = new Intent(AssessmentActivity.this, AssessmentCategory.class);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private void showAnswerDialog(String correctAnswer, String userAnswer, float percentage) {
        String remarks = (percentage >= 75) ? "Status: ✅ Passed" : "Status: ❌ Failed";

        String message = "Your Answer:\n\n" + userAnswer +
                "\n\nCorrect Answer:\n\n" + correctAnswer +
                "\n\nAccuracy: " + percentage + "%" +
                "\n" + remarks;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Answer Key")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(AssessmentActivity.this, AssessmentList.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                })
                .show();
    }

    private void showAttemptsDialog() {
        Cursor cursor = dbHelper.getAllAssessments();
        StringBuilder messageBuilder = new StringBuilder();

        int attemptIndex = 1;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int lessonNum = cursor.getInt(cursor.getColumnIndexOrThrow("lesson_number"));
                int groupNum = cursor.getInt(cursor.getColumnIndexOrThrow("lesson_group_number"));

                if (lessonNum == lesson_number && groupNum == assessment_list_group_name) {
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
            messageBuilder.append("No attempts found for this lesson and group.");
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Attempts: Lesson " + lesson_number + " - Group " + assessment_list_group_name)
                .setMessage(messageBuilder.toString())
                .setCancelable(false)
                .setNegativeButton("Back", (dialog, which) -> {
                    Intent intent = new Intent(AssessmentActivity.this, AssessmentList.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                })
                .show();
    }


    public void AnswerListGenerator() {
        assessmentList = new ArrayList<>();
        // Lesson 1 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                1,
                1,
                R.drawable.lesson1_assessment_group_a,
                "1 Nate may stay for tea. 2 Dave made the Navy team in May. 3 I need a vase. 4 Nate ate the meat. 5 Amy sees Dean Meade on May 15.",
                1.6F //1.6F
        ));
        assessmentList.add(new AssessmentItem(
                1,
                2,
                R.drawable.lesson1_assessment_group_b,
                "6 Fay is mean and vain. 7 Fay stayed all day with me. 8 Amy made a date with Dave. 9 Fay Day may meet me on East Main Street. 10 The deed is in Dave's safe.",
                1.85F
        ));
        assessmentList.add(new AssessmentItem(
                1,
                3,
                R.drawable.lesson1_assessment_group_c,
                "11 Dean made $10 on May 18. 12 Dave's fee is $18. 13 Nate stayed at 15 East Main all day. 14 The Meade team faced the Navy on May 10. 15 Dave made a safety.",
                1.8F
        ));
        assessmentList.add(new AssessmentItem(
                1,
                4,
                R.drawable.lesson1_assessment_group_d,
                "16 Amy made me eat the meal. 17 Fay saved a seat for me. 18 The dean is easy to see. 19 May heard Fay say \"Feed me\". 20 Dean Meade will see Dave on May 18.",
                1.85F
        ));
        // Lesson 2 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                2,
                1,
                R.drawable.lesson2_assessment_group_a,
                "1 Ray Stone may phone me at my home.  2 Lee may fail writing. 3 My whole right side is sore. 4 My train leaves at seven; Dale Reeve's train leaves later. 5 Steven may buy a mail meter.",
                1.45F
        ));
        assessmentList.add(new AssessmentItem(
                2,
                2,
                R.drawable.lesson2_assessment_group_b,
                "6 Lee Reed stayed home last evening. 7 He made me a $5 loan. 8 My reading rate is low. Is Dave's reading rate high? 9 Ray may rely on me. 10 My final rating is 80.",
                1.85F
        ));
        assessmentList.add(new AssessmentItem(
                2,
                3,
                R.drawable.lesson2_assessment_group_c,
                "11 I hear he might fly to Rome in a day or so. 12 I need more light in my retail store on Vail Drive. 13 Ray is leaving home; Lee is remaining here. 14 My writing style is fair. 15 Dale wrote a fine story. 16 He drove the whole night. 17 The freight train is late.",
                2.9F
        ));
        assessmentList.add(new AssessmentItem(
                2,
                4,
                R.drawable.lesson2_assessment_group_d,
                "18 Is it snowing or raining or hailing? 19 Is my trian late? 20 He notified me he may fly home later. 21 I may sign my lease. 22 Dale may fly home later.",
                1.7F
        ));
        assessmentList.add(new AssessmentItem(
                2,
                5,
                R.drawable.lesson2_assessment_group_e,
                "23 Dave is feeling fine. 24 Fay dyed her hair; Mary might dye her hair too. 25 He notified me my radio was stolen.",
                1.2F
        ));
        // Lesson 3 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                3,
                1,
                R.drawable.lesson3_assessment_group_a,
                "1 Mr. Peters will write four plays.\n" +
                        "2 My niece reads at a slow pace. I am not pleased.\n" +
                        "3 Our wills are inside our steel safe in our library.\n" +
                        "4 I have pride in our home.\n" +
                        "5 I will read Mary's brief at home. Please leave it in my library\n" +
                        "6 Our sales? in our Reno store are high, I am indeed pleased.\n" +
                        "7 Our sales in our Erie store are low.\n",
                3.65F
        ));
        assessmentList.add(new AssessmentItem(
                3,
                2,
                R.drawable.lesson3_assessment_group_b,
                "8 I have a slight pain in my right ear. I will stay inside.\n" +
                        "9 I will mail Mr. Deering a brief note.\n" +
                        "\n" +
                        "10 Mr. Paine, a well-known labor leader, will buy my home in Maine.\n" +
                        "11 I am driving home in an hour or so. Please notify my niece. \n" +
                        "12 My neighbor, Mr. Peter Bates, saved my life.\n" +
                        "13 Fay made a will in Mary's favor.\n" +
                        "14 Mr. Blair's neighbors are polo players.\n",
                3.7F
        ));
        assessmentList.add(new AssessmentItem(
                3,
                3,
                R.drawable.lesson3_assessment_group_c,
                "15 I realize I am late.\n" +
                        "16 My niece owns an airplane. It flies at 350 miles an hour.\n" +
                        "17 I will sign a lease in May.\n" +
                        "18 I have placed my deed in our private safe.\n" +
                        "19 Our papers are in my file.\n" +
                        "20 Mr. Bates stayed in my library an hour or so writing a paper.\n" +
                        "21 Please buy me a spare tire.\n",
                3.25F
        ));
        assessmentList.add(new AssessmentItem(
                3,
                4,
                R.drawable.lesson3_assessment_group_d,
                "22 I am not failing filing, I might fail in typing.\n" +
                        "23 I need a file in my library. Please buy it in Mr. Blair's store.\n" +
                        "24 I hear Mr. Stone will remain in Spain.\n" +
                        "25 I will read my evening paper at home.\n" +
                        "26 He will see Mr. Stone in Rome in May.\n" +
                        "27 Mr. Ray will not buy a home in Mobile. He will buy a home in Moline.\n",
                3.55F
        ));
        // Lesson 4 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                4,
                1,
                R.drawable.lesson4_assessment_group_a,
                "1 My wife will take my niece Gail Kline skating while we are at White Pines. I will go skating too. 2 We will try to keep our room clean. 3 Mr. Sweet will stay a week or so at my home in Green Acres. 4 We have two girls who are going to Wayne High School. 5 I am not willing to wait while Mr. Wade grades our typing papers. I have to go to a meeting in an hour.",
                4.05F
        ));
        assessmentList.add(new AssessmentItem(
                4,
                2,
                R.drawable.lesson4_assessment_group_b,
                "6 I do not know why he came so late. 7 I will increase Mr. Bailey's pay in a week or so. 8 Mr. Sweet gave me a nice raise; I need it. 9 Wade swears he knows who broke my vase. 10 Our produce sales are increasing. I am well pleased.",
                2.6F
        ));
        assessmentList.add(new AssessmentItem(
                4,
                3,
                R.drawable.lesson4_assessment_group_c,
                "11 I will not go skiing. I have a fever. It made me weak. 12 We have a weak polo team. I have a vague feeling we will lose to White Plains High School. 13 My wife will mail in our renewal in a week or ten days. 14 I will take Route 15 to Lake Rose.",
                2.85F
        ));
        assessmentList.add(new AssessmentItem(
                4,
                4,
                R.drawable.lesson4_assessment_group_d,
                "15 Mr. White's keys are not in my blue suit. 16 Mary Bailey types four or five hours a day. 17 Mr. Weeks will prune our maple trees in May. 18 Kate Gates made a poor grade in Dean Wade's legal course. 19 I do not eat sweets; I am trying to lose weight.",
                2.7F
        ));
        assessmentList.add(new AssessmentItem(
                4,
                5,
                R.drawable.lesson4_assessment_group_e,
                "20 I ate two rolls at noon. I do not feel too well. 21 Whose notes are in my library? 22 It will take me at least an hour to read Mr. Blair's brief. 23 We will not take a plane to Dover; we will drive. 24 Ray Stone will retire in May.",
                2.65F
        ));
        // Lesson 5 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                5,
                1,
                R.drawable.lesson5_assessment_group_a,
                "Mrs. Sweet: Your letter of April 10 to Mr. Keith Booth gives the facts relating to his clothing bill, but I am afraid that your letter is a little too terse. It will hurt his pride. \n" +
                        "Can you rewrite your letter so that the tone is not so severe? Remember, we have to keep Mr. Booth happy with our service.\n" +
                        " Please mail me a carbon of the rewrite that you prepare. Ethel Parks\n",
                3.6F
        ));
        assessmentList.add(new AssessmentItem(
                5,
                2,
                R.drawable.lesson5_assessment_group_b,
                "Mrs. Ruth: Here are four facts relating to Mr. Smith's new motel in Salem that you may not know:\n" +
                        "\n" +
                        "1. It can take care of 450 guests with ease.\n" +
                        "\n" +
                        "2. It has three meeting rooms. Two of these rooms are really movie theaters.\n" +
                        "\n" +
                        "3. It has an inside swimming pool.\n" +
                        "\n" +
                        "4. It has a staff that is ready to take care of your needs.\n" +
                        "\n" +
                        "The rooms at Smith's Motel are first class, but Mr. Smith has kept his rates low. His rates are given in the pamphlet that is clipped to my letter.\n" +
                        "If you are planning a sales meeting, have it at Smith's Motel in Salem. Fred White\n",
                3.45F
        ));
        assessmentList.add(new AssessmentItem(
                5,
                3,
                R.drawable.lesson5_assessment_group_c,
                "Dear Neighbor: We are happy to write you that the new Weaver cars have arrived. The new cars are well built as well as attractive.\n" +
                        " You can buy the car you like or you can lease it. If you buy it, we will help you finance it. But if you prefer to lease it, we will prepare a lease that will appeal to you. \n" +
                        "See these fine cars during your noon hour or in the evening. We are open till eight. Your Weaver Car Dealer\n",
                4.25F
        ));
        assessmentList.add(new AssessmentItem(
                5,
                4,
                R.drawable.lesson5_assessment_group_d,
                "Dear Edith: Harry Smith wrote me that in April you addressed a meeting of our dealers in Dallas. He said that you spoke with the help of notes but that you spoke like a veteran. \n" +
                        "I am indeed happy that you did so well. I plan to ask you to address the new members of our sales staff the last week of May. Are you free the last week of May? J. C. Farmer\n",
                3.7F
        ));
        assessmentList.add(new AssessmentItem(
                5,
                5,
                R.drawable.lesson5_assessment_group_e,
                "Mrs. Sweet: During the first week of April, Mr. Barry White will fill the vacancy we have in our clothing store in West Haven. He hopes to move to West Haven with his wife Kathleen in six weeks.\n" +
                        " May I ask a favor of you, Mrs. Sweet? Please help the Whites locate a place to live in or near West Haven. The Whites need a home with three bedrooms.\n" +
                        "If you know of a home that the Whites can lease or buy, please telephone me at 555-1612.4 \n" +
                        "\n" +
                        "I know you will help the Whites if you can. Beth Harvey\n",
                4.45F
        ));
        assessmentList.add(new AssessmentItem(
                5,
                6,
                R.drawable.lesson5_assessment_group_f,
                "Dear Ted: As you may know, my clerk, Bill Smith, will celebrate his twentieth birthday the last week of May. We plan to give Bill a pair of theater tickets as a surprise, but we need a little help that I have a feeling you can supply. We do not know the plays that Bill has seen. I know that with your tact, you can get me a list of four or five plays that Bill has not seen.\n" +
                        "I know you will help me surprise Bill Willis\n",
                4.4F
        ));
        // Lesson 6 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                6,
                1,
                R.drawable.lesson6_assessment_group_a,
                "Mrs. Vail: In his note of May 18 our salesman, Mr. Harry Ruth, writes that you broke a bone in your left leg while skiing last week, but he adds that the break is healing fast. In fact, he tells me that it is healing so well that you will go home in a week or ten days. \n" +
                        "     Why not plan to stay four or five days with me at my cabin near Lake Pine. I have a guest room that you can sleep in. We can have our meals in a little diner that is close to my cabin.\n" +
                        "     Are you in favor of my plan? Edith Harper\n",
                5.45F
        ));
        assessmentList.add(new AssessmentItem(
                6,
                2,
                R.drawable.lesson6_assessment_group_b,
                "Mr. Sweet: As you know, Harty Allen is not happy with the salary he is making as our tire salesman in Dallas. He said he wrote you two letters to that effect. \n" +
                        "     Last evening I had an hour's meeting with him at my motel, but I do not feel I made him happy. If you do not increase his salary, I am afraid we will lose him.\n" +
                        "     Harry is a fine salesman. Can you give him a raise effective in April or May? Mark Palmer",
                4.25F
        ));
        assessmentList.add(new AssessmentItem(
                6,
                3,
                R.drawable.lesson6_assessment_group_c,
                "Dear Ned: Last night while eating in the main dining room of the Three Acres Motel in Dallas, I had a real I surprise. Bill Wade, whom I had not seen since our high school days, came in. I had an hour's visit with him.\n" +
                        "     It seems that he owns a travel service in Dallas that is doing well. He has a little girl who is in the first grade.\n" +
                        "     If you care to telephone Bill, you can get him at (206) 555-8172. You can ask him to try to get you tickets to a Dallas game. Henry Lopez",
                4.95F
        ));
        // Lesson 7 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                7,
                1,
                R.drawable.lesson7_assessment_group_a,
                "Mrs. Grace: I am sorry to write you that on July 10 Mr. James Swift notified me that he will not take the job of chief shipping clerk that you have had open in your Dallas branch since June 5. I talked with him on the phone.\n" +
                        "He tells me that he likes the job, but it is his feeling that the salary we are offering him is too low.\n" +
                        "Can you see your way clear to authorize an increase in our salary scale? If you cannot, we may well have a problem filling the job. Ethel Wall",
                1
        ));
        assessmentList.add(new AssessmentItem(
                7,
                2,
                R.drawable.lesson7_assessment_group_b,
                "Dear Sir: If you need a new jeep on your farm, we invite you to stop in at our showroom at 15 Church Avenue. We will show you the eight models we have on our floors. We assure you that we can supply you with a jeep that will meet all your needs at a price that will please you. If you care to test-drive a jeep, our salesman, Mr. Jack Small, will arrange it.\n" +
                        "\n" +
                        "We are open till nine at night on all weekdays. Sincerely yours.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                7,
                3,
                R.drawable.lesson7_assessment_group_c,
                "Dear Madam: On June 30 I bought a Model 16 easy chair at your Cherry Lane store. The chair arrived at my home on Park Drive on July 3, but I cannot accept it as it is. It has two large rips in the pillow as well as a deep scratch on the right rear leg.\n" +
                        "Please arrange to take the chair back to your factory. Yours very truly",
                1
        ));
        assessmentList.add(new AssessmentItem(
                7,
                4,
                R.drawable.lesson7_assessment_group_d,
                "Mr. Paul: We will have a meeting of our college sales staff on March 15 at the Hotel George at 150 Fifth Avenue. The meeting will start at nine. My plans are ready, but I need your advice on two vital sales matters.\n" +
                        "\n" +
                        "Can you arrange to meet me at eight on March 15 in the lobby of the Hotel George? We will have an hour or so to talk while we have breakfast in the coffee shop of the Hotel George. Horace Small",
                1
        ));
        assessmentList.add(new AssessmentItem(
                7,
                5,
                R.drawable.lesson7_assessment_group_e,
                "Mr. James: My secretary, Miss Mary Page, tells me that the copying machine in the first floor mailing room is broken again. That machine has given the members of my staff lots of headaches.\n" +
                        "I have a feeling that the final answer to the problem is a new machine. Please investigate the matter of cost. If the cost of a new, larger copier is not too great, please buy it. Give the bill to Miss Page. She will see that it is paid. Beth Church",
                1
        ));
        assessmentList.add(new AssessmentItem(
                7,
                6,
                R.drawable.lesson7_assessment_group_f,
                "Mrs. Bridges: As I promised you, I am attaching a copy of a chart showing July sales in our two shoe shops. As you will notice, our Moline shop is doing well but our Dallas shop is not. The Dallas shop has had a sharp decrease since March.\n" +
                        "Can you tell me why sales in our Moline shop are high while sales in our Dallas shop are causing me gray hairs? Jerry Small",
                1
        ));
        // Lesson 8 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                8,
                1,
                R.drawable.lesson8_assessment_group_a,
                "Dear Passenger: Early in June, members of our staff made a survey in which they asked 1,000 passengers of our railroad if they smoked on their way to the office or on their way home in the evening. This survey by our staff showed that merely 100 of them, or 10 percent, smoked. Nearly 200 of them, or 20 percent, said they would prefer that there be no smoking on our trains because \"smoking is not good for you.\" \n" +
                        "Therefore, beginning this week, only two cars on each ten-car train will be smoking cars. If you would like to smoke on our trains, please ride in the first or last car, both of which are smoking cars. Sincerely yours\n" +
                        "\n",
                2.88F
        ));
        assessmentList.add(new AssessmentItem(
                8,
                2,
                R.drawable.lesson8_assessment_group_b,
                "Mr. Parks: I do not relish writing you this letter because I have to tell you that we are increasing the price of our letter pads slightly. I believe you know that our costs have increased greatly. In March they increased by nearly 5 percent. In April they increased by nearly 8 percent. We have absorbed these increased costs, but we cannot go on absorbing them. I am, therefore, with a good deal of regret, making a price increase, but only a small increase of 8 percent. \n" +
                        "Beginning July 15, the following price changes will be in effect: Style 153 pads will be $80 a gross. Style 20 pads will be $90.50 a gross. Style 26 pads will be $98 a gross.\n" +
                        "\tI sincerely hope. Mr. Parks, that this price increase will not affect your sales adversely. James R. Baker\n",
                3.5F
        ));
        assessmentList.add(new AssessmentItem(
                8,
                3,
                R.drawable.lesson8_assessment_group_c,
                " Mrs. Charles: Last night at 6 o'clock I met for an hour with Jack Sweet urging him earnestly not to leave his job as head of our mailing room. I even promised to give him an increase of 20 percent, which would raise his salary to $15,000, but he would not accept it. He will leave on July 18.\n" +
                        " I am sincerely sorry to lose Jack because he did a good job operating the mailing room. As you well know, good people are not easy to get these days. Mary Farmer\n",
                2.28F
        ));
        assessmentList.add(new AssessmentItem(
                8,
                4,
                R.drawable.lesson8_assessment_group_d,
                " Dear Madam: Early in March 1 wrote you to the effect that the trees in back of your home need a good spraying because the insects will start to attack them late in May or early in June. I have not had an answer to my letter. Did it reach you?\n" +
                        " If you would like my staff to take care of spraying your trees, please call me collect at (300) 555-1171. The cost of spraying your trees properly with a new type of spray that will not harm animals or trees will be only $150. Yours very truly\n",
                2.45F
        ));
        assessmentList.add(new AssessmentItem(
                8,
                5,
                R.drawable.lesson8_assessment_group_e,
                " Dear Fred: There is a fairly good chance that I will be asked to talk to our 100 office-machines sales trainees at their meeting at the Hotel White in Dallas on June 15.\n" +
                        " As the meeting will close well before 3 o'clock, 2 there will still be at least four hours of daylight in which we can play 18 holes of golf.\n" +
                        " Will you be free on June 15? It would give me great pleasure to treat you to a fine steak following our golf game. \n" +
                        "I sincerely hope you will be free. Sincerely yours\n",
                2.38F
        ));
        // Lesson 9 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                9,
                1,
                R.drawable.lesson9_assessment_group_a,
                "To the Stall: Because our salespeople need a good deal more space to operate efficiently, we are going to move them to the first floor, a location which the National Television Corporation is planning to vacate. They will move on or before July 18.\n" +
                        "     We plan to proceed with our move on the 19th of July 3 Therefore, I would like all members of the sales section to finish their preparations for moving well before 5 o'clock on July 18. The movers will be here at 9 o'clock on July 19. If no hitch occurs, the sales section will be operating efficiently again by July 21 at the latest.\n" +
                        "     I know that I can rely on your cooperation as well as on your patience while this move is taking place. Beth Sweeney",
                3.3F
        ));
        assessmentList.add(new AssessmentItem(
                9,
                2,
                R.drawable.lesson9_assessment_group_b,
                "Dear Jim: As you may know, Harry Smith will be twenty-one on\n" +
                        "June 28. In honor of this occasion, I am planning to have a birthday celebration for him on that day at my efficiency cabin in Ocean Grove. I am inviting seven or eight of his college classmates to this celebration.\n" +
                        "     Are you free on June 28? If you are, please plan to be in Ocean Grove an hour or so before 5 o'clock.\n" +
                        "     I sincerely hope, Jim, that we will see you on June 28. Yours very truly,",
                2.25F
        ));
        assessmentList.add(new AssessmentItem(
                9,
                3,
                R.drawable.lesson9_assessment_group_c,
                "Corporation, has not paid his bill for $650 in spite of the four collection letters we wrote him.\n" +
                        "     Please arrange to visit Mr. Baker to see if you can get his check for $650.\n" +
                        "     May I caution you, Mrs. Abbey, to be patient but firm with him. As I am sure I need not tell you, his goodwill means a great deal to our firm. C.F. Miller",
                2.025F
        ));
        assessmentList.add(new AssessmentItem(
                9,
                4,
                R.drawable.lesson9_assessment_group_d,
                "Operations, to the National Hotel in Memphis. Please make sure that they reach the National Hotel before March 20, the day on which I am to address a group of 300 clothing store owners.\n" +
                        "     Mr. Keith, the chairman of the meeting l said he would be happy to place a leaflet on each chair in the meeting room if the leaflets arrive at the National Hotel by 4 o'clock on March 19. \n" +
                        "     I am pleased to have the chance to talk to these people, They are fine prospects for the service we offer to the clothing trade, Sincerely yours,",
                2.85F
        ));
        assessmentList.add(new AssessmentItem(
                9,
                5,
                R.drawable.lesson9_assessment_group_e,
                "To the Staff: It is with a heavy heart that I write this letter to the staff. I have the sad task of telling you that our treasurer, Mrs. Mary Gates, will retire on July 30. As you will remember, Mrs. Gates had a severe stroke early in March. While she is rapidly regaining her health, her physician feels that she would be wise to retire.\n" +
                        "     Mrs. Gates has served our firm with efficiency since 1970. We will all miss her patient advice as well as her inspiration.\n" +
                        "     No action will be taken to fill Mrs.Gates position before the fall. A. Smith",
                2.55F
        ));
        assessmentList.add(new AssessmentItem(
                9,
                6,
                R.drawable.lesson9_assessment_group_f,
                "attached pamphlet listing the finest vacation hotels inthe West will help you with your\n" +
                        "planning.\n" +
                        "     In all of these hotels you can get a fine room for as little as $50 a day, which includes three meals a day.\n" +
                        "     Before you make a final selection of a vacation hotel, why not stop in at the National Travel Agency. We have ten efficient people on our staff who will help you select a vacation hotel that will provide you with the greatest pleasure at the lowest cost. Sincerely yours,",
                2.63F
        ));
        assessmentList.add(new AssessmentItem(
                9,
                7,
                R.drawable.lesson9_assessment_group_g,
                "To the Staff: I am sure that there are occasions on which you have professional visitors with whom you would like to talk privately. Please feel free to take your visitors to Room 15.\n" +
                        "     This room has three chairs as well as a large desk. If you need to provide more chairs, you can borrow those in Room 17. Helen J. Smith",
                1.55F
        ));
        // Lesson 10 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                10,
                1,
                R.drawable.lesson10_assessment_group_a,
                "Dear Professor Moses: Why are people in Atlanta switching their car insurance to National? They are switching because:\n" +
                        "1. They find that we can save them 15 percent or more on their car insurance.\n" +
                        "2. They like the efficient way in which our 15 offices in Atlanta serve their needs.\n" +
                        "3. They know that we render good, fast claim services.\n" +
                        "Why not invite our agents to read your policy. If they find that you can save by insuring with National, they will tell you so. But if they find that your policy is as good as ours, they will not try to get you to change.\n" +
                        "There is no charge for this service. If you would like to have it, I will be happy to arrange it, Professor Moses. Simply call me at (703) 555-5176. Yours very truly",
                1
        ));
        assessmentList.add(new AssessmentItem(
                10,
                2,
                R.drawable.lesson10_assessment_group_b,
                "Dear Parents: Have you had a half hour or so to read the pamphlet on Grand Island Camp that we sent you? If you have, we are sure you liked the steps Grand Island Camp has taken to be sure that your daughter will have a pleasant vacation.\n" +
                        "Applications for July at Grand Island Camp are currently arriving at the rate of four or five a day. As we can take care of only 200 campers, we anticipate that all our space will be assigned by early June.\n" +
                        "If you would like your daughter to spend two weeks at Grand Island Camp, please mail your application by June 2 at the latest. We don't want to find it necessary to write you that we cannot take your daughter because your application didn't reach our offices by the closing date. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                10,
                3,
                R.drawable.lesson10_assessment_group_c,
                "Mrs. Bond: As you know, last week I asked Mr. Trent to prepare two mailing pieces on our new line of mattresses. I am attaching copies of the two pieces Mr. Trent wrote. I am well pleased with his copy.\n" +
                        "I will have our printer print 10,000 copies of each of these mailing pieces. Four thousand copies will be sent to our Dallas office. Four thousand copies will be sent to our Erie office. Two thousand copies will be kept here in our main office on Park Avenue.\n" +
                        "I am sure that these mailing pieces will increase our sales of mattresses in the fall. Barry Smith",
                1
        ));
        assessmentList.add(new AssessmentItem(
                10,
                4,
                R.drawable.lesson10_assessment_group_d,
                "Dear Andrew: As you know, our salesman in the East, Bob Moses, left my staff to take a position with the Carpenter Meat Processing Corporation in Flint.\n" +
                        "An hour or so ago, a chap by the name of Bill Landry came in to see me. He applied for the job. During his visit he said he knew you well. In fact, he said that he often played golf with your sister.\n" +
                        "He left me with the feeling that he is a talented, efficient man who would do well as a salesman for our firm.\n" +
                        "Do you share my feeling? Sincerely",
                1
        ));
        assessmentList.add(new AssessmentItem(
                10,
                5,
                R.drawable.lesson10_assessment_group_e,
                "Dear Professor Sanders: Here is an offer that is a real bargain. For $520 you can get a trip to Paris this winter as well as 50 hours of French lessons while you are in Paris. You may rightly wonder if you can learn French in only 50 hours. With our efficient new methods, though, you will be surprised at the proficiency you will achieve.\n" +
                        "If you would like more facts, call our main offices at 555-1187. If you prefer, see your local travel agency. The agents are familiar with all our travel plans. Sincerely yours",
                1
        ));
        // Lesson 11 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                11,
                1,
                R.drawable.lesson11_assessment_group_a,
                "Dear Friend: When it is necessary for you to travel 150 miles or more to a city in which a meeting is to be held, you should not drive because that is the hard way. A better way by far is to charter an airplane from the Jordan Flying Corporation and save hours of boring driving. \n" +
                        "We will provide the services of a highly skilled and efficient crew that will get you to your meeting in style and take you home after the meeting. I know you will be pleased with our efficiency.\n" +
                        "If you could arrange to share the rental of a chartered plane with your colleagues, you would be able to have the pleasure and speed that a chartered plane provides at small cost. \n" +
                        "We were the first to offer charter service in Westfield. If you would like to have all the facts, mail the attached card. We will be happy to send them to you. Better still, stop in and get the facts at our Field Street office. I am there from nine to five daily. Sincerely yours\n",
                4.53F
        ));
        assessmentList.add(new AssessmentItem(
                11,
                2,
                R.drawable.lesson11_assessment_group_b,
                "give a good talk entitled \"Children and Their Problems. \"After she finished. I asked her if we could print her talk in our magazine, Child Care. She offered to send me two copies. When I get them from her, should I send them to you, or should I send them to Mr. Sweet at his office address? \n" +
                        "I told Mrs. Fields that if all goes well, we should be able to print her talk in our July issue. Can we do this? Sincerely yours\n",
                2.5F
        ));
        assessmentList.add(new AssessmentItem(
                11,
                3,
                R.drawable.lesson11_assessment_group_c,
                " Dear Friend: If you have been trying to find the offices of Coastal Airlines at 49th Street and Garden Road but have not been able to locate them, here is the answer to the problem: We have moved from our old location to a new building at 415 Third Street, which is across the street from the National Insurance Building.\n" +
                        " Our new offices are bigger, and we have hired more people who will be able to help you plan all your trips. \n" +
                        "When you again have occasion to fly to a city that we serve, visit our Third Street offices and let our people take care of your needs. They are eager to help. Yours very truly\n",
                2.93F
        ));
        assessmentList.add(new AssessmentItem(
                11,
                4,
                R.drawable.lesson11_assessment_group_d,
                "Dear Richard: I have been trying to reach you at your Fifth Street office for the last ten days, but I have not been able to get you. Apparently you have been on the road. Therefore, I am writing you this note to ask if you could do me a favor. \n" +
                        "Two of my old friends are going to visit me from July 18 to July 25. While they are here, they would like to see the Mets play the Cardinals on the afternoon of July 19. I tried to get tickets, but I could not get them. They told me that all tickets for that game had been sold since June 15.\n" +
                        " I thought that with your influence, you might be able to get tickets for my friends, even if they are in the center field bleachers!\n" +
                        " I know you will do your best for me. Sincerely yours\n" +
                        "\n",
                3.73F
        ));
        assessmentList.add(new AssessmentItem(
                11,
                5,
                R.drawable.lesson11_assessment_group_e,
                " Dear Madam: As you know, on April 15 you bought two handbags from our ladies' store on Baker Street and charged them.\n" +
                        " You were billed for these handbags, and our bill should have been paid by June 1. \n" +
                        "Mr. James, a member of our billing section, has called you on the phone on three occasions, but he has not been able to reach you. We are, therefore, writing to ask you to send your check for $40 to pay for these bags. \n" +
                        "Won't you please take care of this matter. Yours very truly\n",
                2.33F
        ));
        // Lesson 12 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                12,
                1,
                R.drawable.lesson12_assessment_group_a,
                "Dear Ethel: Early this week while Mary and I were shopping on Park\n" +
                        "Avenue, I bought a set of records for your children. I am sending them by parcel post to your West Street office in Flint marked \"Do not open before Christmas.\" They should arrive well before Christmas. Please drop me a note when they reach you\n" +
                        "     Mary and I had hoped that we could spend Christmas with you and the children, but I have had word from our Atlanta plant that they are having labor problems. Therefore, we canceled our plans. If we can arrange it, we will visit you after I get back from Atlanta. \n" +
                        "     Have a Merry Christmas! Sincerely yours,",
                2.85F
        ));
        assessmentList.add(new AssessmentItem(
                12,
                2,
                R.drawable.lesson12_assessment_group_b,
                "Dear National Cardholder: We are highly gratified that since 1975 we have been able to increase the benefits offered to National card- holders, but there has been no increase in our fee. The pressures of inflation, though, make it necessary for National to raise its fee from $15 to $20 an increase of 33 1/3 percent. The $20 fee will be in effect in July.\n" +
                        "     While we are increasing our fee, we are happy to be able to tell you that we are adding five new services to the large list of those that we have been offering since 1978. The attached pamphlet lists them on page 18 and page 19.\n" +
                        "     We sincerely hope that it will not be necessary to increase our fee again. Yours very truly,",
                3.2F
        ));
        assessmentList.add(new AssessmentItem(
                12,
                3,
                R.drawable.lesson12_assessment_group_c,
                "Dear Madam: It is a pleasure to learn from your letter of June 18 that our staff rendered efficient service to your treasurer, Mr. Ruth, and his sister, Mrs. Sweet, on their trip from Dallas to the West Coast on our airline.\n" +
                        "     We do not often get letters like yours. Most people write a letter only when they feel that our services have not been good.\n" +
                        "     We assure you that we will strive to serve all our riders with the same efficiency which moved you to write your letter of June 18. Sincerely yours,",
                2.35F
        ));
        assessmentList.add(new AssessmentItem(
                12,
                4,
                R.drawable.lesson12_assessment_group_d,
                "Dear Sir: Could you spare mean hour or so at your office during the week of July 18? I would like to show you a novel plan that I have prepared for getting people to pay bills that are past due.\n" +
                        "     Simply indicate on the attached card when I may call. As I said, it will take me only an hour to show you the way my plan operates. Sincerely yours,",
                1.78F
        ));
        assessmentList.add(new AssessmentItem(
                12,
                5,
                R.drawable.lesson12_assessment_group_e,
                "Dear Friend: Our treasurer is a little worried. He says that you owe the National Printing Corporation $450 which should have been paid by March 15.\n" +
                        "     If your firm is having cash-flow problems – we have them too! – and cannot spare $450, we know we can arrange for you to settle your bill in a way that will not strain your\n" +
                        "finances.\n" +
                        "     But we have to hear from you! Sincerely yours,",
                1.75F
        ));
        // Lesson 13 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                13,
                1,
                R.drawable.lesson13_assessment_group_a,
                "Dear Professor Woods: Please accept our thanks for the order you mailed us for a Cook color television set. The order reached us yesterday, and I was glad to get it.\n" +
                        "You have chosen your set wisely because the Cook color television set is the work of a number of the finest minds in the television industry.\n" +
                        "Your set will be shipped this afternoon by truck from our factory, and you should have it soon. We know that it will give you hundreds of hours of pleasure.\n" +
                        "I am enclosing a copy of a circular that lists our entire line of radios, television sets, and record players. Please read the circular when you have a chance. Very truly yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                13,
                2,
                R.drawable.lesson13_assessment_group_b,
                "Dear Sir: Ordering food for hundreds of patients in a nursing home and serving it is not an easy job. It is a real challenge to prepare and to serve three appetizing meals a day. You must work hard to keep the soup hot and the ice cream cold and the salad crisp. That is the job that Cook Food Services does.\n" +
                        "We prepare the meals in our own kitchens. They are then placed on trays and later put in ovens which heat the food. The meals are then ready to be served. We have been serving food to nursing homes since 1950.\n" +
                        "A circular listing all our services is enclosed. If you would like to have Mr. Bates, our chief food planner, talk to the officers of your nursing home, we will be glad to send him. To arrange for his visit, just call us. Our number is 555-9274.7 Verv truly yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                13,
                3,
                R.drawable.lesson13_assessment_group_c,
                "Dear Professor Cook: Thank you for the nice letter you wrote in my behalf to Mrs. Helen Dudley of the Woods Book Corporation. She called me yesterday to tell me that the job was mine and that I could begin work on July 1.\n" +
                        "I am sure that your letter was a vital factor in Mrs. Dudley's selection of me from the dozens of people who were applying for the job. I assure you, Professor Cook, that I will do my utmost to succeed in this job.\n" +
                        "I plan to stop in to see you soon and thank you again for your kindness. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                13,
                4,
                R.drawable.lesson13_assessment_group_d,
                "Dear Bud: We were sorry to learn yesterday that our shipping room did not fill your July 3 order properly. I made a note on the order that 100 copies of Mrs. Swift's Cooking Guide should be shipped to your Fifth Street stockroom but that the bill for $450 should be sent to your home. The shipping clerk did not see my note and sent the books to your home and enclosed the bill inside the package.\n" +
                        "Our truck will pick up the books at your home soon and take them to your stockroom.\n" +
                        "Thanks, Bud, for being so patient with the inefficiency of our clerk. Yours very truly.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                13,
                5,
                R.drawable.lesson13_assessment_group_e,
                "To the Staff: I am glad to be able to write you that in April and May we had a good increase in the sale of our sugar products. I am well pleased. This increase can be traced to three factors:\n" +
                        "1. The hard work of our salespeople.\n" +
                        "2. The fine work of the copywriters who prepared the circulars we mailed to our dealers in March.\n" +
                        "3. The fine work of our shipping staff who filled all orders the same day they came in.\n" +
                        "May I thank each of you sincerely for your cooperation. A. G. Smith",
                1
        ));
        assessmentList.add(new AssessmentItem(
                13,
                6,
                R.drawable.lesson13_assessment_group_f,
                "Mrs. Tucker: Thank you for the copy of the notes you made at our March sales meeting in Tulsa. I was glad to get them because I left my own notes on the plane and was not able to recover them. Your notes will be of great help to Mr. Parks and Mr. White, who will soon start working on their plans for the July meeting.\n" +
                        "I learned yesterday that the only date on which we can hold our July meeting is July 31. All the meeting rooms in our building will be full from July 1 through July 30.\n" +
                        "Again, thanks for the notes you enclosed with your letter. It will be nice to see you again at the meeting on July 31. Ethel Booth",
                1
        ));
        // Lesson 14 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                14,
                1,
                R.drawable.lesson14_assessment_group_a,
                "Mrs. Quinn: The final copy for the circular promoting our hardware products arrived yesterday. I was indeed glad to have it, and I thank you for it. You must have spent hours and hours working during the weekend to have finished it so soon.\n" +
                        " I am placing an order today with the Broadway Printing Shop for 20,000 copies. We should have them by March 10. They will be mailed to our dealers between March 15 and April 10.\n" +
                        " I am enclosing our check for $320 to pay for the work you did on the circular. Thank you again for your good service. Harry J. Quill",
                2.63F
        ));
        assessmentList.add(new AssessmentItem(
                14,
                2,
                R.drawable.lesson14_assessment_group_b,
                "Mr. Baldwin: Today I was invited to address a meeting of the National School Book Editors on March 20, and I quickly accepted. Their meeting will be held in the Twin Cities Motel. I have selected the topic \"The Study Habits of Teenage Children.\" The details of the meeting are listed in the enclosed folder. \n" +
                        "This means that I will not be able to chair our weekly production meeting. Will you be free on March 204 so that you could fill in for me?\n" +
                        " If you are \n" +
                        "not free, we may have to cancel the production meeting. Gwen Sweet\n",
                2.48F
        ));
        assessmentList.add(new AssessmentItem(
                14,
                3,
                R.drawable.lesson14_assessment_group_c,
                "Dear Professor Dwight: You will recall that on June 22 vou asked me to quote you a price on the building¹ of a study on the east side of your dwelling at 600 Park Street. After I visited your premises.  I quoted you a price of $5,000 if you would authorize us to begin work by July 15, but I have not had an answer to my offer. \n" +
                        "As you well know, inflation is taking its toll, and if I cannot get started by July 15, I will have to withdraw the price I quoted you and increase it by 105 percent. \n" +
                        "Why not call me today, Professor Dwight, and authorize me to pro-ceed with the building of your study. Very truly yours\n",
                3.075F
        ));
        assessmentList.add(new AssessmentItem(
                14,
                4,
                R.drawable.lesson14_assessment_group_d,
                "Dear Friend: If you can give a positive answer to the following queries, you qualify for a car loan at the National Credit Corporation:\n" +
                        " 1. Are you 18 or older? \n" +
                        "2. Do you have a steady job at which you earn at least $90 a week? If so, we are ready to finance a new car for you when you need it. If you prefer, we will hold your loan for three weeks. Meanwhile, you can look for just the car that meets your needs. \n" +
                        "Send for our booklet listing all our loan plans. Better still, stop in at the National Credit Corporation today. There is no red tape involved in arranging a loan. It is quick and easy. Sincerely yours\n",
                3.025F
        ));
        assessmentList.add(new AssessmentItem(
                14,
                5,
                R.drawable.lesson14_assessment_group_e,
                "Mr. White: Yesterday I visited the Broadway Office Supplies Shop and selected two desk lamps for the office of Mr. Smith, our chief editor. He has needed these lamps for weeks. They should help him increase his efficiency. The lamps were on sale, and I was able to get both for only $85. \n" +
                        "The lamps have been shipped from the shop and should arrive at our office soon. \n" +
                        "The bill for $85 is attached. Grace Baldwin",
                1.9F
        ));
        assessmentList.add(new AssessmentItem(
                14,
                6,
                R.drawable.lesson14_assessment_group_f,
                "Dear Madam: When you have a National air travel credit card, buying tickets on Coastal Airlines is quick and quite simple. All you need do is pick up your phone, call 555-8702, tell our efficient clerk the  city you plan to visit, and give him or her your credit card number. Your tickets will be mailed the same day. \n" +
                        "If you would like us to provide this credit card service to your officers, fill in and send us the enclosed card. We will take care of all the details. Very truly yours",
                2.3F
        ));
        // Lesson 15 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                15,
                1,
                R.drawable.lesson15_assessment_group_a,
                "Dear Dr. Quinn: One thing you can do to let the people of Winfield learn about the valuable services or goods your business makes available to them is to place your ads in the Winfield News, which reaches about 50 percent more readers in this region than the Winfield Post.\n" +
                        "     Any ad you place in our paper will reach about Y 800.000 thinking people whose respect we have won since our paper first appeared in 1930.\n" +
                        "     The Winfield News will assist you in building your sales. No matter what you sell or what you produce, you will be ables to reach the largest possible number of prospects when you place your ads in our paper. Let our capable research staff help you prepare a well-planned campaign that will produce a sizable increase in your sales.\n" +
                        "     Our rates are listed on the enclosed circular, Dr. Quinn. You will find them quite reasonable. Yours very truly,",
                3.85F
        ));
        assessmentList.add(new AssessmentItem(
                15,
                2,
                R.drawable.lesson15_assessment_group_b,
                "Dear Dr. Sweet: What do you think is the most valuable thing one can possess as the owner of a business? Most business people who were queried replied that there was nothing more valuable than a fine credit rating. I think you will agree that they are right.\n" +
                        "     You have a favorable credit rating with us, Dr. Sweet, but you could jeopardize it it we do not receive a check from you by June 26 for $90 to pay for the repair work we did on\n" +
                        "your car recently.\n" +
                        "     Do not do anything that would harm your credit rating; send us your check for $90 today. Yours very truly,",
                2.73F
        ));
        assessmentList.add(new AssessmentItem(
                15,
                3,
                R.drawable.lesson15_assessment_group_c,
                "Dear Dr. Dwight: If your home is insured at yesterday’s prices, just hope that it does not catch on fire. Inflation has raised the value of all the things you own. This means that it would cost more to replace or install anything you lose than your insurance would provide. That is why it is vital for you to be sure that your insurance keeps up with inflation.\n" +
                        "     It you are wise, you will have your insurance coverage restudied by a reliable, capable independent broker. If you don’t have an independent broker and would like to talk to one,\n" +
                        "call me before noon any weekday at 555-8261. I will be glad to give you the name of one who is located near your home or your business. Do this soon, today if possible. Sincerely yours,",
                3.38F
        ));
        assessmentList.add(new AssessmentItem(
                15,
                4,
                R.drawable.lesson15_assessment_group_d,
                "Dear Sir: If you are an investor and want reliable advice on what stocks to order, what stocks to hold, and what stocks to sell, we invite you to talk with Mr. Henry Baker, head of our research section.\n" +
                        "     After weighing what you tell him about your needs, he will be able to tell you what stocks you should order, what stocks (if any) you should sell, and what stocks you should\n" +
                        "hold.\n" +
                        "     To learn more about what. Mr. Baker can do for you, stop in to see him during the noon hour at our office at 15 Park Street. Or if you prefer, call him. His number is 555-8720. Sincerely yours,",
                2.8F
        ));
        assessmentList.add(new AssessmentItem(
                15,
                5,
                R.drawable.lesson15_assessment_group_e,
                "Dear Madam: To operate smoothly, your business needs a favorable flow of working capital. When people are slow in paying their bills, they can cut into your available supply of cash, and there is always the chance 2that a number of people may not be able to pay their bills at all.\n" +
                        "     By asking people to pay their bills firmly and in a friendly way, you will resolve your collection problems and increase your receipts.\n" +
                        "     As you will see by the samples I am enclosing. our collection stickers make efficient reminders. They are brief and they are friendly.\n" +
                        "     Why not try them. An order card is enclosed. Sincerely yours,",
                2.73F
        ));
        // Lesson 16 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                16,
                1,
                R.drawable.lesson16_assessment_group_a,
                "Dear Dr. Yale: If you will fill in and mail the enclosed card, you will receive a valuable gift from the Detroit Insurance Corporation. It is a yellow vinyl traveling case that will keep your suits clean and fresh when you travel. We offer you this gift for the chance to tell you about a financing plan that we think will appeal to you-one that is quite simple and immensely effective.\n" +
                        "Most businessmen and businesswomen earn not less than $400,000 between their first and last paychecks. Yet many do not have anything to show for their years of toil. Our plan offers you a quick but effective way to save money for the day you retire.\n" +
                        "Take a minute, Dr. Yale, to mail the enclosed card. When we receive it, we will tell you what our plan can do for you and send you the yellow vinyl traveling case we mentioned. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                16,
                2,
                R.drawable.lesson16_assessment_group_b,
                "Dear Professor Yale: Royal Park's volunteer fire fighters have a drive each year to raise money for the many services they render to Royal Park's citizens. Would you be willing to join us and work as a section manager in this year's drive?\n" +
                        "Each manager is assigned a section of Royal Park. Your section, in which there are 30 homes, would be from Eighth Street to Doyle Square.\n" +
                        "As a manager, you would appoint five men or women, each of whom would solicit money or pledges from six homes. After they have visited the homes, they would leave the money and pledges that they collected with you. I would finally collect them from you at your office.\n" +
                        "I sincerely hope you will be able to say yes. Our goal this year is $100,000. We need your help to get it. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                16,
                3,
                R.drawable.lesson16_assessment_group_c,
                "Dear Doyle: Yesterday I finally yielded to my boys' pleas that we install a swimming pool in our yard. I had to say yes once Mrs. Roy joined them. They had won. I had to give up; I had no choice! I plan to install a pool in a month or so.\n" +
                        "Could you send me any circulars or booklets that show the types of pools you build? Better still, why not stop by to see me soon and tell me what is involved in building a pool in our yard. Very truly yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                16,
                4,
                R.drawable.lesson16_assessment_group_d,
                "Mr. Doyle: I am glad to write you that the Yale Corporation board at its last meeting ruled that all managers who have been with us for 15 years should hereafter receive one month's vacation.\n" +
                        "I have listed on the attached sheet all those who are eligible for a month's vacation. Please notify them at once of our new policy. Thank you for taking care of this matter for me.\n" +
                        "I think this new policy will have a good effect on the morale and efficiency of our managing staff. Ethel J. Smith",
                1
        ));
        assessmentList.add(new AssessmentItem(
                16,
                5,
                R.drawable.lesson16_assessment_group_e,
                "Dear Sir: If your car is five or six years old, it may not be as good-looking or as smooth-riding or as efficient as it once was. It might even be a safety hazard. You might be wise to order a new car instead of having your old one repaired.\n" +
                        "You can arrange the financing for a new car at the Reliable Finance Corporation here in Troy. We will be glad to arrange a loan for you that you can take 36 months to repay. It is easy to arrange a well-planned loan. It will take less than half an hour. Sincerely yours\n",
                1
        ));
        // Lesson 17 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                17,
                1,
                R.drawable.lesson17_assessment_group_a,
                "Mrs. Purcell: This morning I received the following short note from Mr. Jack Trent, director of production for the Doyle Manufacturing Company: \"Gentlemen: We are sorry that we will not be able to deliver before next June the 40 washing machines we promised to deliver by April 10 because of a wildcat strike by our factory personnel.\"\n" +
                        " 1 have canceled the order we gave to the Doyle Manufacturing Company. Do you know where we can purchase 40 washing machines on short notice and have them delivered by April 10? This is a matter of tremendous importance. It is so important that we will, if necessary, pay a premium of 10 percent in order to get the machines by 9 o'clock on the morning of our deadline. Harry Quinn",
                1
        ));
        assessmentList.add(new AssessmentItem(
                17,
                2,
                R.drawable.lesson17_assessment_group_b,
                "Gentlemen: Yesterday morning I received a copy of your company's personnel policies booklet. It is well planned and should be valuable to your people. It should add to their efficiency. \n" +
                        "My purpose in writing you, though, is to learn the name of the person who designed the booklet. I think it is nicely designed, and I would like to hire the designer to help us prepare an important brochure we will send to the stockholders of the National Manufacturing Company early next year. Could you tell me the name of your designer and where we can reach him or her? Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                17,
                3,
                R.drawable.lesson17_assessment_group_c,
                "Dear Dr. Yale: Thanks for your short note about our personnel policies booklet. I am glad you were pleased with it.\n" +
                        " This booklet was the work of David White, a personal friend of mine. You can reach him at his business address at 2115 West Street in Dallas. David has been designing circulars, booklets, pamphlets, and similar things for us for five years. He likes detailed work, and he al-ways does a good job. He has won many prizes for his designs. His work is imaginative and depend-able. What is more, he believes in meeting a deadline, which is of great importance in any type of business. Yours very truly\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                17,
                4,
                R.drawable.lesson17_assessment_group_d,
                "Dear Dr. Percy: All of us are directly or indirectly involved with the manufacture of milk products. The price and richness of milk, therefore, are matters of importance to all of us. Yet many people do not realize the important problems that we have in manufacturing and delivering milk products. \n" +
                        "I am enclosing a booklet prepared by Mr. Roy Sweet that tells the whole story. If you would like to have enough copies of this booklet for your classes, we will be delighted to supply them. They are free. Just tell me where to send them. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                17,
                5,
                R.drawable.lesson17_assessment_group_e,
                "Ladies and Gentlemen: At Coastal Airlines, we believe that a short flight should feel like a short flight. At most major airfields, we have machines that print your ticket in ten seconds or less. We have carry-on luggage storage space on  most of our planes. \n" +
                        "What is more, most of our short business flights are designed to get business people to their appointed city and back the same day. \n" +
                        "At Coastal Airlines, short flights feel short. \n" +
                        "Fly Coastal on the next occasion that personal business or pleasure takes you to a city we serve. Yours very truly\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                17,
                6,
                R.drawable.lesson17_assessment_group_f,
                "Dear Friend: Are you looking for a new car? If you are like most persons, you will spend at least the next six weekends looking before you decide on the model you will purchase. \n" +
                        "But perhaps you shouldn't purchase a car at all. Perhaps you should lease one. \n" +
                        "Our efficient staff can arrange a leasing plan for you in less than half an hour. When your lease is up after four or five years, we will have a new car waiting for you. You won't have to worry about selling your old car or shopping for your next one.\n" +
                        " If you don't want to tie up a lot of money in a car, lease one from us soon. Sincerely yours\n",
                1
        ));
        // Lesson 18 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                18,
                1,
                R.drawable.lesson18_assessment_group_a,
                "Recently a studying the activities of a person in business revealed that a normal working day was spent as follows: 9 percent writing, 16 percent reading, 30 percent talking, and 45 percent listening. One important fact emerges from this study – listening occupies more working hours than anything else. Yet research indicates that most of us listen with only about 25 percent efficiency.\n" +
                        "     If you are to succeed in business, you must have good listening habits.\n" +
                        "     There are two types of listening – active and passive. When we listen passively, we absorb only a portion of a person's words. We can get by with passive listening when we chat with friends or talk with a relative on the telephone. In these cases it does not matter the next day if we do not remember anything that has been said.\n" +
                        "     Active listening, though, necessitates mental action by the listener in order to remember what is being said. You must be able to decide when you can get by with passive listening and when you must be an active listener.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                18,
                2,
                R.drawable.lesson18_assessment_group_b,
                "     Speed of Talking and Listening. The average person talks about 125 words a minute, but a listener can \"hear\" speech at the rate of at least 300 words a minute, Thus it is easy 12 for the mind to wander while it is waiting to receive details.\n" +
                        "     During the last two decades, educators have been giving thought to speeding up reading. Because skill in listening is thought by many to be just as important as reading skill, steps are being taken in many schools today which should help learners listen more effectively. There are even a number of companies where valuable short courses are being offered to learners to  help them get more from what they hear.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                18,
                3,
                R.drawable.lesson18_assessment_group_c,
                "During the years you are in school – and later in your business life and personal, activities – you will profit by making good notes. You will listen to your teachers in classes. You will read from books and magazines and all types of printed matter. You will hear speeches and debates on current topics.\n" +
                        "     If you could remember all you hear, you would have no need for notes. But important facts slip from your mind quickly, and soon you cannot remember them. You may not recall this morning things that were said yesterday afternoon. You cannot possibly remember most of what you read and hear if you do not make notes.\n" +
                        "     Making good notes should help you in three important ways:\n" +
                        "     1. You will learn more and learn it more quickly.\n" +
                        "     2. You will remember more of what you hear and what you read.\n" +
                        "     3. You will study and work more efficiently.\n" +
                        "     It is a well-known fact that you learn more and learn faster by doing – that is, keeping your mind involved with what you are trying to absorb. When you make notes, you cannot avoid learning. The act of making notes helps you remember more too.\n" +
                        "     Work hard to perfect your note making skills. They will be of value to you the rest of your life.",
                1
        ));
        // Lesson 19 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                19,
                1,
                R.drawable.lesson19_assessment_group_a,
                "Ladies and Gentlemen: For the past 20 years the Daily Star has issued a special new-car preview section. This year we will again issue a special new-car preview section. This valuable section will be delivered with the morning issue of the July 5 Daily Star.\n" +
                        "This unique preview will reach about 500,000 people, a great many of whom are ready to order and are able to buy a new car. Of the people who will read this preview, 81 percent presently own one or more cars, and 62 percent purchased a new or used car last year.\n" +
                        "As the manufacturer of car equipment, accessories, and parts, you should invest your advertising money where it will do the most good. You should place a well-planned advertisement in this preview. It represents an opportunity for an immediate increase in your sales. Take a few moments immediately to call Ms. Mary Hughes, manager of our advertising department, and arrange to get your advertising message in the July 5 preview section. Ms. Hughes' number is 555-9864. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                19,
                2,
                R.drawable.lesson19_assessment_group_b,
                "Ladies and Gentlemen: For the past 20 years the Daily Star has issued a special new-car preview section. This year we will again issue a special new-car preview section. This valuable section will be delivered with the morning issue of the July 5 Daily Star.\n" +
                        "This unique preview will reach about 500,000 people, a great many of whom are ready to order and are able to buy a new car. Of the people who will read this preview, 81 percent presently own one or more cars, and 62 percent purchased a new or used car last year.\n" +
                        "As the manufacturer of car equipment, accessories, and parts, you should invest your advertising money where it will do the most good. You should place a well-planned advertisement in this preview. It represents an opportunity for an immediate increase in your sales. Take a few moments immediately to call Ms. Mary Hughes, manager of our advertising department, and arrange to get your advertising message in the July 5 preview section. Ms. Hughes' number is 555-9864. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                19,
                3,
                R.drawable.lesson19_assessment_group_c,
                "Dear Professor Royal: In the enclosed copy of the Financial Review, you may find a few advertisements from companies about which you would like to have more essential facts. You can have them because all our advertisers want to tell you more about their businesses and the important work they do than they can tell you in the limited space of their advertisements.\n" +
                        "Therefore, the Financial Review has provided an opportunity for you to send for these facts at no cost. Simply drop the enclosed card, addressed to Ms. Mildred Hugo, Director of Advertising, Department B, in the mail, and she will be glad to send the facts to you immediately.\n" +
                        "This represents one more illustration of the special, efficient service we render the readers of the Financial Review. Yours very truly",
                1
        ));
        assessmentList.add(new AssessmentItem(
                19,
                4,
                R.drawable.lesson19_assessment_group_d,
                "Mr. Hugo: Yesterday Mr. White came to see me to ask if I could help him write his new circular for the advertising department. There was not anything I could do to help him, but I asked him to call Mrs. Sweet of the Main Street Advertising Company\n" +
                        "What do you think about this? May I have a note from you soon? Thanks. Van Smith\n",
                1
        ));
        // Lesson 20 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                20,
                1,
                R.drawable.lesson20_assessment_group_a,
                "Dear Dr. Brown: Please accept our thanks for the valuable part you played in making our conference on consumer advertising at Arden House in South Bend a memorable one. I received many comments about the conference, and I found that all of them relating to your speech were especially complimentary.\n" +
                        " In the ten years that I have been conducting these and other seminars, this is the first one where there were so many compliments and so few complaints. I sincerely hope that you will have an opportunity to be present at our conference next year. \n" +
                        "I have asked Ms. Royal of our accounting department to send a check for $250 directly to you. You should have it shortly. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                20,
                2,
                R.drawable.lesson20_assessment_group_b,
                "Dear Mark: When I saw your mother and father at a convention of commercial artists recently, they showed me an announcement saying that you had been appointed manager of the advertising department of the Downing Leather Manufacturing Company and that you would move to their main office located in South Orange immediately. I know, Mark, that you won this important job with your hard work and your proficiency. \n" +
                        "Your mother and father are indeed proud of your accomplishments, and so am I. This promotion represents a great opportunity for you, and I know we can count on you to fill it with honor. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                20,
                3,
                R.drawable.lesson20_assessment_group_c,
                "Dear Neighbor: Thanksgiving provides us a unique opportunity to convey our thanks to a person we love. It is a perfect opportunity to say \"Thanks for being a considerate friend\" or \"Thanks for being a good mother or father or brother.\" \n" +
                        "It is easy. Simply stop by the South Street Flower House, order the flowers which you would like to send, and we will complete the job. A special card saying that the flowers are being sent from you with your compliments will be enclosed when the flowers are delivered. If you would rather telephone us, you can call Mr. James Yale. His number is 555-1818.\n" +
                        " We are open between the hours of 8 o'clock in the morning and 5 o'clock in the afternoon. Remember, we now accept all major credit cards. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                20,
                4,
                R.drawable.lesson20_assessment_group_d,
                "Gentlemen: The energy crisis represents a real threat, but it is one we think we can combat effectively. 1 Would you like to learn how you can conserve energy in your business? \n" +
                        "If you would, there is one thing you can do immediately-send for our two complimentary booklets entitled Needed: A Well-Designed Conservation Plan and Starting an Energy Management Plan. These two special booklets were composed by Dr. James Swift of Dwight College. Together they list 33 concrete ways in which you can conserve energy. \n" +
                        "Use the enclosed card to get your booklets. We will be glad to send them to you. The card does not need any postage. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                20,
                5,
                R.drawable.lesson20_assessment_group_e,
                "Dear Sir: Yesterday I had a note from our bookkeeper, Mrs. Mary Purcell, telling me that your account, which amounts to $150, was 90 days past due.\n" +
                        " I know there must be a reason why you have not paid it. Perhaps the payment of your bill slipped your mind completely. In that case you will be glad to receive this friendly note. If there is a reason why you cannot pay us now, could you please tell us what it is. It is of considerable importance that your account be settled soon. Sincerely yours\n",
                1
        ));
        // Lesson 21 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                21,
                1,
                R.drawable.lesson21_assessment_group_a,
                "Dear Dr. Fenton: The fact that there has been an energy shortage for several years does not mean that you have to be content with insufficient lighting. You can obtain very good light by simply installing Winston energy-saving lamps, the finest lamps manufactured today.\n" +
                        "     No other lamps for sale today can compare with Winston lamps. They get more light out of every watt than any other type of lamp. This means that you can cut down considerably on power usage without sacrificing the advantages of outstanding lighting. About the only things you should do is replace completely your present lamps with new Winston lamps.\n" +
                        "     There are Winston lamps for whatever residential lighting needs you may have. We suggest that you install Winston lamps immediately. You will be making a very wise move. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                21,
                2,
                R.drawable.lesson21_assessment_group_b,
                "Dear Lieutenant Trenton: As president of the National Dental Schools, may I tell you how very glad we were yesterday when we were told that you will be able to take part in our meeting at Mountain Lake on the morning of July 15. I am confident this will be a very well attended meeting. Everyone will want to take advantage of the unique opportunity to hear you.\n" +
                        "     Whatever topic you select will certainly be accepted by the board of directors of the National Dental Schools. But may I suggest a topic likes \"How To Train Outstanding Dental Assistants.“ Incidentally, please plan to join Mr. and Mrs. Swift and severals of our officers for dinner at the Yale restaurant on July 16. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                21,
                3,
                R.drawable.lesson21_assessment_group_c,
                "Gentlemen: The hardest way to find out about insurance is by accident. There are hidden dangers in every operation. They suddenly materialize into accidents when you least anticipate them.\n" +
                        "     At the Continental Insurance Company we work very hard to help our policyholders protect their businesses against accidents which suddenly arise. We have helped thousands of businesses like yours whenever they needed insurance help quickly. We confidently believe we could help you too.\n" +
                        "     If you would like to find out more about the many advantages of obtaining your insurance from the Continental Insurance Company, we suggest that you write us. We will be glad to send you several circulars that outline our polices in detail. Better yet, call our insurance counselor, Ms. Mary White, at 555-9271. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                21,
                4,
                R.drawable.lesson21_assessment_group_d,
                "Dear Professor Brothers: Ever since the United Food Store was established at 15 Park Street in 1930, it has been our policy to:\n" +
                        "     1. Do what is honest and to the best advantage of every purchaser.\n" +
                        "     2. Give outstanding values and fair treatment to everyone.\n" +
                        "     3. Give every person the most value for his or her money.\n" +
                        "     4. Maintain the very highest possible food standards.\n" +
                        "     5. Give fast and efficient service.\n" +
                        "     We think this policy is the reason why we have won the business of thousands of persons and why they order their food from us year after year.\n" +
                        "     If you have not yet been in our store, we suggest that you buy your next food supply from us soon. We are confident that once you do, you will decide that you do not want to shop elsewhere. Yours very truly,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                21,
                5,
                R.drawable.lesson21_assessment_group_e,
                "Mr. Quill: I am shipping to the Dallas office 300 copies of the enclosed circular advertising our printing presses.\n" +
                        "     It is of great importance that these circulars be mailed to our dealers by\n" +
                        "March 18. Please see that the mailing is completed by that date.\n" +
                        "     Thank you for attending to this important matter for me. C.C. Cook",
                1
        ));
        // Lesson 22 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                22,
                1,
                R.drawable.lesson22_assessment_group_a,
                "Ms. Fenton: As you know, our domestic automobile business has been growing very fast. In fact, we estimate that it has doubled in recent years and that next year will be bigger than ever. Because of this, we have outgrown our present space. We are using every bit of space on the four floors we now occupy.\n" +
                        "You will be glad to know, therefore, that we have rented temporary space on the third floor of our building, where there have been no tenants since August or September. We will move several departments to this floor, including your advertising department and Mr. Quimby's mailing department. This move will have the advantage of keeping all our company's operations together.\n" +
                        "We contemplate moving your department starting on the morning of October 31 and completing the move on Tuesday afternoon, November 1. I suggest, therefore, that everyone begin to make immediate preparations for the move.\n" +
                        "I am planning to give you more details tomorrow or Wednesday. Charles H. Dempsey\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                22,
                2,
                R.drawable.lesson22_assessment_group_b,
                "Dear Mrs. Temple: As head of the Red Cross campaign in Des Moines, I thank you for the valuable work you did to make our campaign succeed.\n" +
                        "As you know, the goal we set for the end of February was $200,000. You will be glad to know that we will surpass that goal by an estimated $30,000. On Thursday, February 25, we had collected $150,000 in cash and $60,000 in pledges-and we still have Friday, Saturday, and Sunday to go. This outstanding achievement is a source of great pride to me.\n" +
                        "It was a pleasure to work with you, Mrs. Temple. I have seldom worked with a person whose optimism was so contagious. Cordially yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                22,
                3,
                R.drawable.lesson22_assessment_group_c,
                "Dear Mr. Landon: Today, when your food dollar buys less, it is important that you demand more for your money. That is why National Farm Butter should be on your shopping list.\n" +
                        "It takes the better part of ten ounces of milk to make one ounce of fresh National Farm Butter. That is why it is so nourishing. It is now evident that inflation is eroding the value of just about everything. Therefore, you should make certain that you are getting real value in the items you purchase.\n" +
                        "Demand more for your money; demand National Farm Butter. Any high-grade food store stocks it. Get in a good supply at your first opportunity. Yours sincerely",
                1
        ));
        assessmentList.add(new AssessmentItem(
                22,
                4,
                R.drawable.lesson22_assessment_group_d,
                "Gentlemen: This letter represents our third attempt to collect the $250 that has been due on your account since Monday, December 1; it will be our last.\n" +
                        "If your check is not in our hands by Tuesday, January 30, we will send your account to our lawyer. This action will hurt both of us. We will lose a good customer, and your credit standing will be irreparably damaged.\n" +
                        "We would suggest, therefore, that you send us your check soon to pay for the automobile accessories and other items you purchased in October and November. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                22,
                5,
                R.drawable.lesson22_assessment_group_e,
                "Dear Dr. Yale: The repair work on the equipment that was damaged by fire on Wednesday, August 10, was completed yesterday, which is two weeks sooner than our staff had estimated. We were able to get the necessary parts more quickly than we anticipated. Everything is now in order and operating efficiently once again.\n" +
                        "What this means is that we will be able to ship your six tape recorders in November instead of December. They will be shipped on or before November 10 from our Bond Street warehouse, and you should have them shortly thereafter.\n" +
                        "Thank you. Dr. Yale, for having been so patient with us. Cordially yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                22,
                6,
                R.drawable.lesson22_assessment_group_f,
                "Ms. Davis: Could you print for me by Monday, January 15, about 200 copies of the enclosed circular, but without the coupon at the bottom? I can use these circulars to good advantage in an hour's talk I have been asked to give before Mr. Swift's business class. I think there are 85 students in his class.\n" +
                        "I accepted Mr. Swift's offer to talk to his class because of his importance to our manufacturing company as a customer. Mary Fountain",
                1
        ));
        // Lesson 23 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                23,
                1,
                R.drawable.lesson23_assessment_group_a,
                "Dear Ms. Overman: Now that the campaign is over and I have been elected mayor of Astoria, I must acknowledge with thanks the very important part you played in my election. I know that you devoted? several hours of your time every day to telephoning voters and to campaigning in general. I sincerely appreciate your efficiency and your devotion to our cause. \n" +
                        "There is no question that without your creative help and that of hundreds of other good citizens, the election would definitely have ended quite differently. \n" +
                        "I assure you, Ms. Overman, that my organization and I will do our best to demonstrate to the people of Astoria that they made a wise choice in this general election. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                23,
                2,
                R.drawable.lesson23_assessment_group_b,
                "Dear Mr. Overton: Every year since 1950, the Parker General Manufacturing Company has been able to give its shareholders the same good news-a dividend check.\n" +
                        " Our organization has definitely been doing things right for a considerable time. We developed the first completely automatic garage door. Our office machines department created the first copying device. Our aviation division has helped to \n" +
                        "develop efficient nuclear devices. We are not only doing things right in the domestic area but wherever our products are sold abroad as well. \n" +
                        "Enclosed is a copy of our financial bulletin. We suggest that you read it over at your leisure. If you have any questions about our organization, I would appreciate it if you would write to me personally. I promise to acknowledge your letter and to answer your questions fully. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                23,
                3,
                R.drawable.lesson23_assessment_group_c,
                "Dear Miss Short: On Tuesday, November 4, the citizens of Sedalia will vote on the important question of whether the city should purchase the land on Central Street that is presently owned by Mr. Jim White, head of National Advertising Associates. His organization wants to sell it to the city at a reasonable price.\n" +
                        " As your mayor, I definitely urge you to vote yes. This area consists of about five square blocks of badly needed land. This valuable site is directly across the street from the city hall, and this unique area will immediately provide many recreational advantages and opportunities for our citizens. The time to buy this land is now rather than later. So when you step into the voting booth next Tuesday morning, press the lever that says \"Yes\" on the proposition to purchase this desirable piece of land. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                23,
                4,
                R.drawable.lesson23_assessment_group_d,
                "Gentlemen: Yesterday I kept an appointment with Dr. Harold Yale of the health department of Peoria in which we talked about the importance of developing more recreational areas in the city.\n" +
                        " After the meeting was over, we were in complete agreement on one thing. In order to organize and carry out a plan to build and maintain these recreational areas, we will definitely need the assistance of the business organizations in the city. Their help will be essential. We think, too, that we will have to have an appropriation of not less than $200,000 from the city. \n" +
                        "Could you send a representative from your organization to a general meeting on Wednesday, January 15, in which we will outline several different plans that we have in mind? We would appreciate an acknowledgment of this letter soon so that we can proceed with our work in planning the meeting. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                23,
                5,
                R.drawable.lesson23_assessment_group_e,
                "Mrs. Bookman: The circular which I am enclosing has not been too effective, so the smartest thing we can do is get rid of our remaining copies. My records indicate that you have 15,000 of them in our Des Moines shipping room. Please sell them as wastepaper. I will be glad to make whatever saving we can on our investment in these circulars. Ethel Sweet",
                1
        ));
        assessmentList.add(new AssessmentItem(
                23,
                6,
                R.drawable.lesson23_assessment_group_f,
                "Dear Friend: I have had the personal pleasure of owning and operating Temple's Department Store since February 1960. Now it is time for me to retire and close up shop. The best way I can say thanks for the business you gave me during the years is to offer all my remaining stock for sale at a 50 percent reduction. \n" +
                        "To help you take advantage of this sale, I am putting more salespeople on each of the four floors. And we will remain open Monday, Thursday, Friday, and Saturday between nine in the morning and five in the afternoon. Sincerely yours",
                1
        ));
        // Lesson 24 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                24,
                1,
                R.drawable.lesson24_assessment_group_a,
                "Over the past few years we have been hearing a great deal of talk about consumer problems and complaints. What should consumers do when they have complaints?\n" +
                        "     Consumers who feel that a department store or other retail establishment has not treated them fairly may be torn between a desire for revenge and the hope that by making a complaint they can get better treatment next time.\n" +
                        "     Seeking revenge is certain to create even more problems. What is needed is a cool head and an organized plan for solving the problem.\n" +
                        "     When To Complain. You should consider making a complaint whenever you are not happy with a product and feel it has failed to measure up to its advertising claims. If there has been an error, honest and reputable merchants will generally want to know about it and will appreciate the opportunity to make an adjustment. It would be to their advantage if you would call a problem to their attention immediately.\n" +
                        "     Reliable dealers and manufacturers want to hear about the problems their customers are having with their products. They have devoted time and energy over the years to building their businesses. They value their old customers and the new ones they have won, and they sincerely want to keep them happy. They will often thank you for telling them about any problem you have had with their products.\n" +
                        "     Where To Complain. The merchant from whom you made your purchase is the best person to start with. Before you talk to the merchant, be sure you know what corrective action you plan to ask for. Then assemble all receipts and other records relating to the purchase. When you present your case to the merchant, be fair and reasonable, but definitely do not be timid.\n" +
                        "     What Next? If the merchant cannot or will not help you, you should consider complaining to the manufacturer. In most cases good manufacturers are glad to know how customers feel about their products. Most products carry the name of the manufacturer and the address of the company. Present your complaint in full in a letter. Allow several\n" +
                        "weeks for a reply so that the manufacturer can have an opportunity to, investigate your complaint.\n" +
                        "     If your letter is not acknowledged after a reasonable time and you have not been able to get any action on your complaint, you might want to consider more stringent measures. For instance, one thing you could do is write a second letter and send a copy to business agencies that are concerned with consumer affairs. The agency that most consumers think of initially is the Better Business Bureau, The bureau, which is well known for its efficiency, handles complaints without charge, but it insists that every complaint be in writing. This helps the staff to be certain that they have the facts straight before they take action on a problem.\n" +
                        "     Identifying Problems. An important consumer problem that is not easy to solve is that of the silent victim. No one can answer the question as to how many silent victims there are. Many are silent because they do not know that they have been deceived. Others do not like to admit that they were victims of a fraud. Still others do not know where to go for help.\n" +
                        "     We suggest that it is very important that all consumers be urged to make complaints that are legitimate and valid.",
                1
        ));
        // Lesson 25 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                25,
                1,
                R.drawable.lesson25_assessment_group_a,
                "Mr. Fenton: This is a rather difficult letter for me to write because I must refuse your request to change your territory from the state of Michigan to the state of New York. I wish I could satisfy you, but I am unable to do so.\n" +
                        "If you will read the personnel booklet of the Underwood Envelope Company, you will see that our representatives must have devoted at least two years to their territory before they are eligible to move to a different area. You have been in your territory a little over one year, and you have made very satisfactory progress. It is evident you are doing a good job. You have won many valuable and influential friends, and you should have another good year.\n" +
                        "If you need any assistance in your work, please let us know. Your success is just as important to our company as it is to you. You can, of course, apply for a change again as soon as you are eligible, which will be in about ten months.\n" +
                        "I hope you will understand our position. Henry Quill",
                1
        ));
        assessmentList.add(new AssessmentItem(
                25,
                2,
                R.drawable.lesson25_assessment_group_b,
                "Gentlemen: Your overdue account is not causing us any real concern. We are satisfied that a progressive organization like the General Toy Manufacturing Company intends to pay for the envelopes we printed for you. We think you will be glad to do this in order to maintain your good credit standing.\n" +
                        "But we are bound under the rules of the Missouri State Credit Bureau to list with them immediately all accounts that are 90 days overdue. If we do not hear from you soon, we will have to take the difficult step of listing your account with the Missouri State Credit Bureau. We do not wish to do this. I hope you will not make it necessary for us to do so.\n" +
                        "A stamped envelope is enclosed. Please take this opportunity to send your check directly to our office. Thanks for your understanding. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                25,
                3,
                R.drawable.lesson25_assessment_group_c,
                "Ladies and Gentlemen: We know that there is a satisfactory reason why you have not paid us the $30 you owe us or acknowledged our requests for payment for the goods you purchased in October and November. I hope, therefore, that you will take care of the enclosed statement or let us know as soon as possible the reason for your delay.\n" +
                        "Why not take care of this matter now. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                25,
                4,
                R.drawable.lesson25_assessment_group_d,
                "Ms. Temple: It is difficult for me to understand why we have made so little progress and have had so little success in finding a satisfactory solution to the question of pollution control in the paperboard and envelope division of our factory in Peoria, Illinois. If we do not shortly comply with the law that the state passed in January, we will be in for a difficult time.\n" +
                        "I wish to call a meeting of the operations committee as soon as possible and request every member to have a definite plan to suggest. I hope we can obtain a solution without delay because of the importance of the matter. Mark C. Yale",
                1
        ));
        assessmentList.add(new AssessmentItem(
                25,
                5,
                R.drawable.lesson25_assessment_group_e,
                "Mrs. Underwood: As you know, yesterday we made definite plans to have a meeting of our advertising department. We were to meet on the morning of Tuesday, August 5, in Chicago. We may have to change our plans though.\n" +
                        "I was just told by Mr. Sweet that he has called a tentative meeting of his finance committee for the same time in Philadelphia, Pennsylvania. He says it is an important meeting at which we will take up several important matters concerning the efficiency of our business operations.\n" +
                        "If his meeting takes place on August 5, we would be well advised to postpone our meeting till next month. I will, of course, call you as soon as I learn anything definite. Harry C. Doyle",
                1
        ));
        assessmentList.add(new AssessmentItem(
                25,
                6,
                R.drawable.lesson25_assessment_group_f,
                "Dear Dr. Dempsey: Does your house need a paint job or a more efficient kitchen? Whatever your needs, the State Street Finance Company can grant you a loan of up to $10,000 at a lower rate than you can get elsewhere.\n" +
                        "Here is what you have to do to get a loan. After you have decided on the changes you wish to make, request your contractor to estimate the cost. Then fill out the enclosed application, mail it, and let us do the rest. We will be able to give you a yes-or-no answer in 48 hours. Under our plan, repayment is easy. Very truly yours",
                1
        ));
        // Lesson 26 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                26,
                1,
                R.drawable.lesson26_assessment_group_a,
                "Dear Mr. Bryant: I realize, of course, that the Lyon's Grill is a little far from your State Street office, and It can well understand why it might be difficult for you to eat here often. \n" +
                        "But undoubtedly you have occasion from time to time to take special friends to dinner. At the Lyon's Grill you will find just the right kind of enjoyable food and satisfying service that make a meal a success. Since your last visit, we have made a great deal of progress. We have enlarged our main dining area, and we have engaged a famous French chef.\n" +
                        " Whenever you wish to make a table reservation, you should call Mrs. Eunice Swift. Often she will be able to take care of your request at the very last moment. We hope, Mr. Bryant, that we may soon have the opportunity to serve you. Yours very truly,\n" +
                        " PS. If you would like a copy of our calendar, fill out and mail the enclosed card in the envelope we have provided. We will be delighted to send you one before January 1.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                26,
                2,
                R.drawable.lesson26_assessment_group_b,
                "Dear Ms. Overmeyer: At Coastal Airlines our people treat every piece of baggage as a valuable piece of property. When they see a package marked \"Fragile,\" they take good care of it. When your plane lands, they try to have your baggage waiting for you by the time you reach the general baggage area. \n" +
                        "Baggage handling is just one thing that makes our organization the acknowledged and unquestioned leader in the travel field and one that travelers use more than any other when they have to go to a city that we serve.\n" +
                        " When you have occasion to fly to Chicago, St. Louis, Los Angeles, or to any other area where we operate, we hope that you will let us take you there. We guarantee you will enjoy your trip. Ms. Overmeyer. Very truly yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                26,
                3,
                R.drawable.lesson26_assessment_group_c,
                "Dear Mrs. Bryant: During the year I devote a great deal of time to attending many conventions of home appliance manufacturers. Seldom, though, do I hear a presentation as enlightening as the one you gave at our Miami meeting yesterday morning on the advantages of television advertising. Thank you for an enjoyable morning. \n" +
                        "After the meeting I talked to several of our members. I am glad to tell you that they had nothing but praise for your speech. You certainly won them over! Several of them suggested that 15 invite you to talk to us again next year, Mrs. Bryant. 1 hope you will be able to be with us at that time. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                26,
                4,
                R.drawable.lesson26_assessment_group_d,
                "Gentlemen: It is my sad duty to write you that your claim against the Science Printing Company amounting to $350 is uncollectible. We worked hard to obtain your money, but our endeavors were in vain.\n" +
                        " Here are the details. The 6,000 circulars you ordered were not printed, even though you had given the company a sizable deposit. The Science Printing Company is in unsatisfactory financial shape and has many unpaid bills. This company is operated by two persons who purchased the business only last year. Neither of them had any prior knowledge of the printing field, and they have been unable to operate with efficiency. They have not been able to gain many new customers, which might have enabled them to make a profit. They have been losing business to competitors rapidly. Unless they get immediate help. I think their demise is inevitable. \n" +
                        "Please let me know as soon as possible what you would like for me to do with your uncollected claim. Unless I hear from you soon, I will send it back to you. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                26,
                5,
                R.drawable.lesson26_assessment_group_e,
                "Dear Dr. Short: Here is a reason why the Home Magazine should play a part in your advertising next year. More of our readers have purchased appliances than the readers of any other magazine. It is a medium that is a \"must\" for you. Sincerely yours",
                1
        ));
        // Lesson 27 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                27,
                1,
                R.drawable.lesson27_assessment_group_a,
                "Dear Mrs. Lang: As you probably read in yesterday's newspaper, Long Island Florists will operate as a division of the Spring Street Seed Company beginning next Friday, November 12.\n" +
                        "     Long Island Florists has been serving Long Island ever since 1920. It consists of an attractive shop and six air-conditioned greenhouses.\n" +
                        "     In my opinion, this new addition to our business makes us the only complete flower organization in Long Island. My uncle, Mr. Roy Banks, is the new manager. Stop in soon and speak to him regarding your particular flower needs. He has been in the flower business for more than ten years and has lots of ideas on flower arrangements. Flowers are his favorite subject of conversation.\n" +
                        "     At this time I would like to thank you for placing your orders with us regularly. You have made this new addition to our business possible. I hope, Mrs. Lang, we will have many opportunities to serve you. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                27,
                2,
                R.drawable.lesson27_assessment_group_b,
                "Dear Mr. Lincoln: Your newspaper advertising business is quite valuable, Undoubtedly you have spent many years building up the strength and reputation of your business and making it a success. You have probably invested every spare dollar in it. If you were to die suddenly, you would certainly wish to pass it along in good condition to your heirs.\n" +
                        "     One thing you should think about, of course, is that Uncle Sam will insist on his full share. Your state will demand an additional share, This could cause your heirs great difficulty. Do you have any idea how or where they will get the necessary cash without selling part of their business? In our opinion, this will not be a problem for them if you immediately take out our regular business insurance. Our representative, Ms. Lydia Yale, will be glad to give you full details on this subject. She will be able to work with you to help you choose a plan that will be best for your particular needs.\n" +
                        "     Jot down on the enclosed card when Ms. Yale may call to speak with you regarding our business insurance. Then send the card to us in the envelope that we have provided. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                27,
                3,
                R.drawable.lesson27_assessment_group_c,
                "Dear Ms. Strong: You have probably read our advertisement in the afternoon edition of your local newspaper regarding the opening of the drive-in branch of our bank directly across the street from the Franklin bus station. We are particularly proud of this branch because it is our first.\n" +
                        "     It is a sign of the general progress we are making. In our opinion, the additional banking accommodation of this branch will enable us to do a satisfactory job of meeting every regular banking need of each of our clients.\n" +
                        "     We suggest that you stop in and let us show you our new branch. We think you will like it. The branch will be open from nine in the morning until four in the afternoon. Cordially yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                27,
                4,
                R.drawable.lesson27_assessment_group_d,
                "Dear Dr. Underwood: Yesterday I was requested to make a short speech at the banquet of the Newspaper Editors Association on the advantages of automation in the printing industry. The banquet will be held Monday, January 18, at the General Manufacturing Company building in St. Louis, Missouri, at 7 o'clock in the evening. L am unable to accept this very flattering invitation because of several rather important engagements I made for January 18.\n" +
                        "     In my opinion, you are without question the acknowledged leader in the subject area of automation and can share many of your ideas concerning new developments in the field with the group. I wonder whether you could attend the meeting and take over this special assignment for me.\n" +
                        "     An addressed envelope, which does not need any postage, is enclosed. Please use it to let me know whether you can take my place. Sincerely yours,",
                1
        ));
        // Lesson 28 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                28,
                1,
                R.drawable.lesson28_assessment_group_a,
                "Mr. Dix: As you are probably aware, the regular spring meeting of our newspaper advertising editors is less than six weeks away. I am particularly anxious to plan ahead so that we will not run into the perplexing problems we encountered last year. I wish it to be a well-organized success. Before I go ahead with the preparation of a tentative agenda, I would definitely like to have your frank opinion regarding the following general questions:\n" +
                        " 1 Where should we hold the meeting-in our State Street building in Chicago or away from Chi-cago, perhaps in Phoenix or Los Angeles or Philadelphia? \n" +
                        "2  What speakers do you suggest we invite and what subjects should they be requested to speak on? \n" +
                        "3 Should we touch on the budget for the coming year, or should we leave that subject for our summer meeting in August?\n" +
                        " 4 Are there any additional topics that in your judgment we should take up? \n" +
                        "I hope that you will send your suggestions and ideas to me as soon as possible; I will welcome them. Max Wilson",
                1
        ));
        assessmentList.add(new AssessmentItem(
                28,
                2,
                R.drawable.lesson28_assessment_group_b,
                "Dear Mrs. Short: As a conscientious, efficient taxpayer, you certainly wish to pay all the taxes that are due on your income under present tax laws. But why should you pay more? To help you decide just how much you owe  Uncle Sam is the purpose of our booklet, The Tax Guide. Thousands of people depend on it to enable them to take advantage of current tax laws covering capital gains and losses and company dividends.\n" +
                        "Plan ahead, Mrs. Short; order a copy of this valuable tax guide today. You can place your order for one by  filling out the coupon at the bottom of the enclosed circular and mailing it in the envelope we have provided. The Tax Guide costs only $4. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                28,
                3,
                R.drawable.lesson28_assessment_group_c,
                "Dear Ms. Overmeyer: You may find it very difficult to believe, but in this day of progressively rising taxes, there are quite a few business people who fail to take advantage of every tax deduction that the law permits. They are people like doctors, lawyers, and some small manufacturers who devote from 12 to 143 hours a day to making a success of something.\n" +
                        " Under the present tax law, you can put away as much as 15 percent of your income up to a maximum of $7,000 each year for your retirement. If you have several people on your staff, the plan works to satisfy their needs as well. \n" +
                        "Would you like more important facts, Ms. Overmever? We will be glad to give them to you. Simply let us know what time will be satisfactory for our representative to call on you. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                28,
                4,
                R.drawable.lesson28_assessment_group_d,
                "Dear Mr. Weston: Immediately after the first of next year, we will open a new Sunshine Hotel over-looking Hong Kong harbor. It will contain approximately 900 guest rooms which were designed by a decorator who is the acknowledged leader in the field. It will have a health spa and social club unlike anything you can find in other parts of the globe. In addition, there will be a heated outdoor swimming pool. Six floors of the hotel will be devoted completely to unique shopping areas where you will be able to obtain fine values on many different types of goods. \n" +
                        "But we cannot tell you everything about this new hotel in a letter Therefore, we are enclosing a booklet that contains the whole story. \n" +
                        "Whenever you have an opportunity to travel to Hong Kong, come to the Sunshine Hotel. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                28,
                5,
                R.drawable.lesson28_assessment_group_e,
                "Dear Mr. Lincoln: A man by the name of Max Long has written me applying for the position of assis-tant sales manager that we have open in our Miami newspaper office. In his application he mentions that he once worked for you as a commer-cial artist on the Trenton Bulletin. He says you can vouch for his efficiency. \n" +
                        "He seems to have a great deal of potential, and I hope to hire him. But before I do, I would like to speak with you regarding several parts of his application that seem rather strange.\n" +
                        " Could you spare me perhaps half an hour next week, Mr. Lincoln? 1 value your opinion highly. Please call me at 555-6402.6 Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                28,
                6,
                R.drawable.lesson28_assessment_group_f,
                "Dear Dr. Underwood: As you are undoubtedly aware, knowing how to earn money is a particularly valuable skill. But knowing how to spend it wisely is much more important. \n" +
                        "You can quickly lose large sums of money that you worked hard to earn if you do not plan your spending properly. How can you be sure of getting full value for the dollars you spend? The answer is to get a copy of the Complete Consumer's Guide. The Complete Consumer's Guide will show you: \n" +
                        "1.\tHow to tell an honest bargain from a fraud.\n" +
                        "2.\tHow to get good value when you are buying household goods. \n" +
                        "3. How to reduce your income taxes legally. \n" +
                        "4. How to borrow money wisely. \n" +
                        "5. How to plan for the days ahead. \n" +
                        "These are only a few of the things you will find in the Complete Consumer's Guide. We hope you will send for a copy as soon as possible. It costs only $8. An order blank is enclosed. Sincerely yours,",
                1
        ));
        // Lesson 29 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                29,
                1,
                R.drawable.lesson29_assessment_group_a,
                "Dear Mrs. Baxter: On Tuesday, February 8, the National Medical Magazine published a unique 16-page section devoted entirely to one subject. It was entitled \"Choices.\" This carefully prepared section solicited doctors everywhere to volunteer for extremely low-paying jobs in remote, unattractive parts of the world where medical care was critically needed. Over 300 doctors responded to this worthy appeal. These doctors recognized that they were making a change of great importance in their careers, their life experiences, and their incomes.\n" +
                        " This extraordinary response is typical of the powerful effect that our progressive publication has on the public. Isn't that the type of responsible publication you should be using to advertise your medical and surgical equipment? \n" +
                        "We suggest, Mrs. Baxter, that you write to us immediately requesting our rate card. In order to do so, just check the box marked \"Yes\" on the enclosed card and mail it to us in the envelope that is provided. When you receive the rate card, you will be convinced that our rates are extremely reasonable yours, Sincerely\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                29,
                2,
                R.drawable.lesson29_assessment_group_b,
                "Dear Mr. Dwyer: A person who is responsible for spending his or her organization's money efficiently on advertising baby cribs, bicycles, or any other article usually wants only one thing-immediate sales. Advertising in the Daily Chronicle brings such exceptional sales that our publication is now generally recognized as the most successful advertising medium in this area. \n" +
                        "Careful, thoughtful advertisers know excellent values when they see them. That is why, in our opinion, so many different types of businesses advertise regularly in the Daily Chronicle, which ordinarily reaches 18,000 more homes in this area than the other newspaper published in this city. It has been the experience of these advertisers that they always get their money's worth when they advertise their goods and services in the Daily Chronicle.\n" +
                        " When you plan next year's advertising budget, I hope you will remember the Daily Chronicle. You will be doing the right thing! Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                29,
                3,
                R.drawable.lesson29_assessment_group_c,
                "Dear Ms. Franklin: This morning my letter carrier delivered to me the fifth edition of The New World Atlas that you so thoughtfully sent me as a Christmas present. As usual, you chose something that you knew I would particularly appreciate. \n" +
                        "The New World Atlas is probably the most beautiful publication that the publishing industry has offered to the public this year. It is an excellent example of what 14 regard as book publishing at its best. The New World Atlas is not only beautiful but practical as well. \n" +
                        "Thank you, Ms. Franklin, for your delightful present. I am grateful for it. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                29,
                4,
                R.drawable.lesson29_assessment_group_d,
                "Dear Clinton: Mr. Harry Short tells me that you made a successful debut as a public speaker at the state meeting of the Medical Equipment Manufacturers Association yesterday afternoon. He says that you talked with assurance worthy of a veteran who had been speaking in public for years. He heard several extremely complimentary statements about your talk.\n" +
                        " I understand that you spoke on the advantages of automation. This is a very difficult but timely subject on which you are, of course, an acknowledged, unquestioned expert I wish I could have had the opportunity to lend you my encouragement. but I was away from the city on company business.\n" +
                        " I have an idea that whenever we need a speaker for an important meeting in the days ahead, we will know where to obtain one who is more than satisfactory-right in our own office! Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                29,
                5,
                R.drawable.lesson29_assessment_group_e,
                "Dear Mr. Overmeyer: It gives me great pleasure to recommend to you the new General insurance plan. This plan is definitely the most responsible and powerful protection you can buy, especially in these days of progressively rising medical costs. I examined this plan carefully, and I can give it my unquestioned endorsement.\n" +
                        " We all recognize that being hospitalized is ordinarily a painful experience. But wouldn't it give you a satisfying feeling to know that there is extra cash coming right at the time you need it most? \n" +
                        "Take my suggestion; fill out the attached blank immediately and mail it in the envelope that is enclosed. Cordially yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                29,
                6,
                R.drawable.lesson29_assessment_group_f,
                "Dear Mr. Ahern: Suppose you found a beautiful car that won you over. It was exactly the car for you, and it was well worth the cost. But your budget could not handle the payments with an ordinary three-year loan. Would that mean that you would have to do without the car? It would not if you came to the Public Bank and Trust Company. We make it easy for you to purchase the car you wish without an immediate strain on your budget. We give you as much as four years to repay your loan. Therefore, you pay less each month. \n" +
                        "Come in to see us soon. We are located in the Medical Towers building at 621 State Street. Sincerely yours\n",
                1
        ));
        // Lesson 30 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                30,
                1,
                R.drawable.lesson30_assessment_group_a,
                "Where does the money we use come from? If your next-door neighbor, Mr. Worth, needed to have some important typing done, he might request you to do it. You might work for about an hour in the morning on the job. When you successfully completed this short job, he should pay you, say, $4 for the work. You know that the $4 you now have in your hand came from Mr. Worth, but where did he get the money?\n" +
                        "     Mr. Worth works for the World Publishing Company and is responsible for public relations in that company, and the $4 he paid you was part of the money he receives for his services to that progressive, efficient business organization. It is probably safe to assume that the company earned that money through selling new editions of its textbooks and its other publications to schools and to students. And where did they get the money prior to that? We could go on asking this difficult question endlessly as the money passes from one hand to another. Yet we know that every single dollar bill must have been created by somebody and there must have been a person who received it for the first time.\n" +
                        "     If we were to trace a particular $10 note back through enough exchanges, we would very quickly find, in our opinion, that it came from a commercial bank. The chances are strong that a person received the note after drawing an ordinary check against a checking account and presenting it to a teller at a bank. The $10 bill was part of the currency that the person received.\n" +
                        "     For a satisfactory understanding of how money gets into the hands of thoughtful people like you and me, we must learn some basic considerations regarding the subject of checking accounts.\n" +
                        "     When you have a checking account, you have the advantage of greater safety in the handling of money. As many people undoubtedly know from sad experiences, currency and coins may be stolen or lost. When that happens, they are usually gone for good. On the other hand, if you lose a blank check, no damage has been done because no one but you can withdraw money from your account.\n" +
                        "     A regular checking account makes it easy to take care of most payments. Instead of personally going to the gas company, to the water company, or to the newspaper office, all you need to do is write out checks for the exact amounts involved, place the checks in envelopes, and mail them to your creditors.\n" +
                        "     A checking account is really a necessity if you wish to send money far away or just to make payments to places outside the city in which you live.\n" +
                        "     Your checking account will give you a valuable record of the payments you have made. Your bank will send you a monthly statement showing the deposits you have made and the checks you have written. Your canceled checks will be enclosed with your statement.\n" +
                        "     An even more i practical advantage of a checking account is the fact that your canceled checks are proof of payment. In the days or years ahead, if a question ever arises as to whether or not you paid a bill, your canceled check is usually recognized as positive proof that payment was made.",
                1
        ));
        // Lesson 31 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                31,
                1,
                R.drawable.lesson31_assessment_group_a,
                "Ms. Samuels: This morning our New York office received from the state government an envelope containing an order for 10,000 copies of our Executive Manual of Correspondence written by Dr. James Sweet. We thought we had sufficient stock of this publication to satisfy our needs throughout the remaining months of the year, but an order for this quantity will completely exhaust our stock by the end of September.\n" +
                        " We, of course, recognized the fact that we had a great thing in the Executive Manual of Correspondence, but we had never before received an order for so large a quantity. Because of the specialized character of this book, we felt it would have a limited sale, and we set as our objective a sale of 20,000 copies annually. I have an idea, though, that we will actually sell more than 50,000 copies this year. I am, quite naturally, very happy about this. \n" +
                        "May I request, Ms. Samuels, that you be responsible for setting up a schedule for printing another 50.000 copies of the Executive Manual of Correspondence. Please acknowledge this note as soon as you receive it. A. C. Quinn",
                1
        ));
        assessmentList.add(new AssessmentItem(
                31,
                2,
                R.drawable.lesson31_assessment_group_b,
                "Dear Mr. Worth: I think I have been successful in finding a home that should be exactly what you need. The house is on Banks Street. I am enclosing several pictures, each of which was taken from a different angle so that you can have a fairly clear idea of what the house actually looks like. \n" +
                        "The house will soon be advertised in every local newspaper. Because of its many excellent features, Mr. Worth, it will probably be sold in a short time. If you wish to pursue the matter, I encourage you to arrange to see the house immediately and to speak to the owner personally. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                31,
                3,
                R.drawable.lesson31_assessment_group_c,
                " Dear Mr. Dempsey: Here are a few of the important advantages of the house I talked to you about on the phone yesterday:\n" +
                        " 1 It is one block away from an elementary school. Your children will be able to walk to school. \n" +
                        "2 It is only one mile from a new shopping area and a half mile from the town government center.\n" +
                        " 3 It is four blocks from a public park.\n" +
                        " 4 It is only six blocks from the Harper Manufacturing Company. where your office will be located.\n" +
                        " 5 The taxes are actually quite low. The house. Mr. Dempsey, is in a nice section of town, and it will not long remain on the market. I suggest, therefore, that you let me arrange an appointment for you to see it in the near future. Very truly yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                31,
                4,
                R.drawable.lesson31_assessment_group_d,
                "Dear Mrs. Overmeyer: Why do the beautiful trees in your yard need feeding? Nature satisfactorily takes care of trees in a wooded area. On most grounds, though, all leaves are eventually raked up. Thus your trees are gradually deprived of a natural quantity of food and moisture throughout the winter. You must replenish this food supply annually if your trees are to remain healthy. Failure to do so will usually affect the beauty and special character of your trees. \n" +
                        "Our experts are prepared to apply chemical food to the roots of your trees with the most progressive machinery. This food will enable your trees to resist the ordinary ravages of winter and the difficulties that insects create when spring and summer arrive.\" \n" +
                        "We suggest that you invite our experienced representatives to give you an objective opinion of the cost of taking care of your trees. You will never regret doing so, Mrs. Overmeyer. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                31,
                5,
                R.drawable.lesson31_assessment_group_e,
                "To the Staff: Yesterday afternoon I received the sales figures for June. I understand that they are much higher than those of the corresponding month of last year. Keep up the good work! Frank Long",
                1
        ));
        // Lesson 32 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                32,
                1,
                R.drawable.lesson32_assessment_group_a,
                "Dear Frank: I read in yesterday afternoon's newspaper that on Wednesday, August 12, you were appointed by the governor to the bench of the third district of Albany. In my opinion, Frank, you are easily the best qualified person for this important job. You richly deserve it.\n" +
                        "Because of your successful and extensive career as a trial lawyer, covering almost twenty years, I know you will serve your state with distinction. Naturally, I think our class knew what it was doing when it recognized you as \"the person most likely to succeed.\" \n" +
                        "You have my best wishes for a long and eventful career as a judge. My corresponding secretary, Ms. Jean Overmeyer, also sends you her best wishes. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                32,
                2,
                R.drawable.lesson32_assessment_group_b,
                "Dear Dr. Mild: For almost half a century our organization, the Albany General Manufacturing Company, has been in the business of discovering practical and helpful answers to pollution problems for industry and government. Our objective has been to develop systems to solve pollution problems of every description throughout the world. \n" +
                        "\n" +
                        "Every recommendation our engineers and executives make regarding a pollution problem is unbiased in character. We have many ideas on ways to cool air and to clean air. We also have the people and the experience to make better air. \n" +
                        "\n" +
                        "We suggest that you invite us soon to discuss pollution problems with you on an individual basis. You will find, Dr. Mild, that we can help you easily and readily dispose of any pollution problems you may have in the near future. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                32,
                3,
                R.drawable.lesson32_assessment_group_c,
                "Dear Mrs. Samuels: Are you disturbed over the steadily rising cost of food? Beginning Thursday morning. September 1, we, the Albany Family Food Mart on Worth Street, will take some very definite steps to control food costs. Many items on our shelves will be priced far below our already low prices. Almost all the items we have selected for discounting will be for basic family needs. \n" +
                        "The prices that will be published in our advertising will be maintained, and stocks will be replenished in sufficient quantities to carry us from September 1 through October 31. \n" +
                        "We will also reduce temporarily the prices of several special seasonal products. A few of the values you will be able to obtain are described in the enclosed circular.\n" +
                        " We think, Mrs. Samuels, that the public should know about the things we are doing to control ever-increasing food prices. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                32,
                4,
                R.drawable.lesson32_assessment_group_d,
                "Dear Mr. Yale: There are many good reasons why more than 500,000 people will lease their new cars this year. Here are just three of them:\n" +
                        " 1. Many one-car families have discovered that leasing enables them to get a second car without having to make a second down payment. \n" +
                        "2. Many people are discouraged with the ordinary problems of owning a car. They have reached the point where they never again want to bother with car maintenance. \n" +
                        "3. Many people regularly use their cars for business and usually need responsibly prepared records for tax purposes. They get these records easily and simply by leasing. \n" +
                        "Should you lease your next car? You probably should, Mr. Yale, but why not discuss the subject of leasing with our representative, Mr. Charles Swift. He is an acknowledged expert in his field, and he can tell you whether leasing will solve your particular problem. Sincerely vours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                32,
                5,
                R.drawable.lesson32_assessment_group_e,
                "Dear Ms. Underwood: Thank you for speaking at our staff meeting on the morning of Friday, November 10, and describing the steps your organization is taking to increase the efficiency of order handling in the shipping department. Order handling has presented difficult, discouraging problems for our company also. I am very glad you have been able to make satisfactory progress in this area. Your comments were quite timely and of special importance to us. \n" +
                        "May I request a favor of you? Could you write up your remarks? We would like to print them in our staff publication, Working Together, so that our entire business family can have the advantage of reading them. \n" +
                        "I am enclosing a stamped envelope for your reply. Yours very truly",
                1
        ));
        // Lesson 33 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                33,
                1,
                R.drawable.lesson33_assessment_group_a,
                "Dear Mr. Bradford: Thank you for your order of Saturday, December 15, for a copy of the Executives Correspondence Manual. This publication, unfortunately, had a disappointing sale. It did not meet its sales forecast and was declared out of print several months ago.\n" +
                        "     Until two weeks ago, we had a small quantity of the manual in the home office, but the supply was exhausted a few days ago. I also checked with our warehouses in Chicago, New York, and Fort Worth, but my efforts met with no success. Furthermore, even the author could not furnish us with a copy.\n" +
                        "     I have an idea, Mr. Bradford, that you will readily be able to obtain a copy from the General Publishing Company on Fourth Street in Fort Collins, which specializes in used books. They also usually carry a stock of books that have been declared out of print. About two weeks ago they filled an order for us for a unique book that a good customer wished to purchase.\n" +
                        "     I am sorry we cannot give you more helpful information at this time. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                33,
                2,
                R.drawable.lesson33_assessment_group_b,
                "Dear Ms. Hartford: Two days ago, Ms. Hartford, I became your personal banker. Your former personal banker, Mrs. Mildred Ford, left us a week ago and accepted a responsible position with the federal government in Albany. \n" +
                        "     I am here to furnish you with any information you need and to answer whatever questions you may have about your account, our services, and any other financial matters.\n" +
                        "     Please stop in during your lunch hour sometime soon, Ms. Hartford. I would appreciate the opportunity of meeting you informally and having a discussion with you. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                33,
                3,
                R.drawable.lesson33_assessment_group_c,
                "Dear Mrs. Overmeyer: At theWorth Furniture Store we do more than sell furniture that is comfortable, has character, and represents extraordinary value. Throughout our years in the furniture business, we have been selling gracious living.\n" +
                        "     The family that purchases furniture from us also obtains the advantage of having the services of a practical, experienced, and skillful designer. This designer is a person who can discuss with you objectively the type of furniture you need and how it should be placed in your home.\n" +
                        "     We suggest that you call us to request a definite appointment with a designer. Your call will, of course, place you under no obligation. Sincerely yours.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                33,
                4,
                R.drawable.lesson33_assessment_group_d,
                "Dear Mr. Ahern: When you purchase furniture from the Royal Furniture Company, there is never any charge for the services of our world-famous designers. Furthermore, there is no charge for their well-recognized talent. When you buy furniture from us, the only thing you pay for is the furniture you buy and nothing else.\n" +
                        "     Isn't this the type of furniture company you would like to deal with? We think it is. Come in the next time you are in' the area and let us describe all our services to you personally. You will easily see why we are the largest furniture company in the state. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                33,
                5,
                R.drawable.lesson33_assessment_group_e,
                "Gentlemen: A few moments ago I read in yesterday afternoon's edition of the newspaper an item to the effect that I am to speak at the New York State Furniture Manufacturers convention at its regular annual meeting on Friday, January 18. The article also says that I am to speak on computers, data processing and automation in the business forecasting area. These are all subjects on which I have done considerable research",
                1
        ));
        assessmentList.add(new AssessmentItem(
                33,
                6,
                R.drawable.lesson33_assessment_group_f,
                "You can easily understand why this item was particularly disturbing to me when I tell you that I am actually scheduled to speak on Saturday morning, January 19. This means that I will probably have a small audience, if any.\n" +
                        "     Can you print a correction immediately regarding my speaking engagement? Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                33,
                7,
                R.drawable.lesson33_assessment_group_g,
                "Dear Dr. Langdon: Two years ago the Progressive Furniture Company was selected to furnish the efficiency apartments in eight of the most successful leisure home complexes in California. Why were we chosen to do this? Because in our opinion we are the acknowledged, foremost leader in the field of leisure-living furniture. Our furniture is so well constructed that it will last practically forever.\n" +
                        "     Are you presently thinking of building a leisure home somewhere in California? If you are, it is important for you to see us. We can help you make selections here in our Chicago showrooms, and you can rest assured that everything will be exactly to your liking when your furniture is installed in your leisure home.\n" +
                        "     Would you like an advertising circular that describes our complete line of dining room, bedroom, and living room suites? We will be glad to send you one if you will fill out, sign, and mail the enclosed short form to us in the envelope we have provided. Very truly yours,",
                1
        ));
        // Lesson 34 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                34,
                1,
                R.drawable.lesson34_assessment_group_a,
                "Dear Mr. Stern: Mr. Morton, manager of the sporting goods department of our State Street store, informed me yesterday morning that you gave him an order for a quantity of our sporting goods that were to be delivered to your southern division office in Atlanta. \n" +
                        "I wish we could start work on your order immediately. Unfortunately, this is not possible because our quarterly credit report on your organization is not complete.\n" +
                        " We realize that you want these sporting goods promptly. May we suggest, therefore, that you send us an advance check for $2,000. We will begin manufacturing the uniforms, jackets, and gloves as soon as we receive it. \n" +
                        "We hope, Mr. Stern, that we will be able to obtain a complete report on your credit condition soon because we want to grant you our usual discount terms on future orders. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                34,
                2,
                R.drawable.lesson34_assessment_group_b,
                "Dear Mr. Norton: An important newspaper executive named Mrs. Mildred Turner had a telephone extension installed in the kitchen a few days ago. Behind that extension lies a story.\n" +
                        "Mrs. Turner's teenage daughter Jane blamed her mother for the failure of a fruitcake. Jane claimed that the phone in the living room rang. and she left the kitchen to answer it. The cake slipped her mind temporarily, and on her return to the kitchen, it was burned almost to a crisp. She further claimed that this discouraging incident never would have happened if there had been an extension in the kitchen. Our news-paper executive recognized the wisdom of her daughter's contention and installed an extension promptly.\n" +
                        "Do you want to be blamed for the failure of a cake, a  cherry pie, or an apple turnover? I have an idea that you probably don't. Mr. Norton. So speak to our representative today, requesting the installation of an extension in your kitchen promptly. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                34,
                3,
                R.drawable.lesson34_assessment_group_c,
                "Dear Ms. Turner: Here are a few things we will do for you when you make reservations at the Eastport Hotel:\n" +
                        "1. We will pick you up at the terminal at any hour of the day at no charge.\n" +
                        "2. We will give you a modern, well-furnished, and soundproof room.\n" +
                        "3. We will take you to the airport promptly when you want to leave-also with our compliments.\n" +
                        "\n" +
                        "What is our object in doing all these thoughtful things for our guests? The answer to that question is that we want our guests to return regularly year after year. We are not satisfied to have them come only once. \n" +
                        "Call us the next time pleasure or government business takes you to Eastport, Ms. Turner. We will take very good care of you throughout your stay and make it an extraordinary experience. Cordially yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                34,
                4,
                R.drawable.lesson34_assessment_group_d,
                "Dear Mrs. Worth: You will recall that in the Wednesday, February 5. issue of World Travel News, we published a short article that de-scribed the modern X-ray units being used to check carry-on baggage. The publication of this article prompted some members of the traveling public to ask whether the radiation had any effect on different types of film.\n" +
                        " The Norton Company is responsible for the installation of this equip-ment. A few days ago we wrote them regarding this subject. They informed us that, in their opinion, the X-ray units will have no effect whatsoever on film.\n" +
                        " To speed up the processing of baggage at airport terminals, we have had Norton units installed in the terminals listed in the enclosed circular. We wanted you to have this information, Mrs. Worth; we hope it will be of value to you. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                34,
                5,
                R.drawable.lesson34_assessment_group_e,
                "Dear Dr. Dexter: The new Franklin Hotel isn't near the Miami airport it is actually in the terminal. Think of the perfect opportu nity this offers you if you want to hold a meeting of general sales representatives, correspondents, or customers.\n" +
                        "The Franklin Hotel is a successful, progressive, and full-service hotel. It has 900 modern rooms that have character and charm.\n" +
                        "You can obtain reservations promptly and without difficulty, Dr. Dexter. Just call us collect. Sincerely yours,",
                1
        ));
        // Lesson 35 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                35,
                1,
                R.drawable.lesson35_assessment_group_a,
                "Dear Dr. Worth: About two weeks ago I was introduced to one of the best prospects I have seen for a long time to fill the important job of corresponding secretary for Billings International Enterprises. She is a woman named Ms. Mary O'Brien who, in my opinion, has the experience necessary for this responsible job. I talked with her for more than an hour, and I am happy to report that I found her to be an interesting. entertaining, and delightful person with a unique grasp of world affairs. I want you to know that I think she will be a valuable addition to the staff of Billings International Enterprises.\n" +
                        " If you have no objection, Dr. Worth, I will suggest to Ms. O'Brien that she call you soon requesting an interview. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                35,
                2,
                R.drawable.lesson35_assessment_group_b,
                "Dear Mrs. Jennings: If you receive a monthly check from Uncle Sam, you never again need worry that it will be lost, stolen, or unduly delayed in the mail. You can easily arrange to protect your checks by filling out one of our regular forms. It will take you only two or three minutes to do this, We will send the form to the government, which will then automatically deposit your checks directly in an Intercity savings account. \n" +
                        "Open a savings account at one of the branches of the Intercity Savings Bank, Mrs. Jennings, and take ad-vantage of this extraordinary oppor-tunity to enjoy the peace of mind that comes with knowing that your checks are safe. Sincerely yours\n!",
                1
        ));
        assessmentList.add(new AssessmentItem(
                35,
                3,
                R.drawable.lesson35_assessment_group_c,
                "Dear Mr. Cummings: As an interior designer, would you be interested in attracting profitable clients? You can do this if you enter your name and provide a brief description of your offerings in the Interior Designers Guide, which we will publish with the Sunday, August 12, edition of our newspaper, The Westport Times.\n" +
                        " Prices for listings range from $60 to $180. If the idea of advertising in this special edition appeals to you, Mr. Cummings, call Ms. Mary Sweet at 555-1876 any morning except Sunday between nine and noon. She will be glad to give you complete information. She will, in addition, actually work with you in preparing advertising copy, if you wish. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                35,
                4,
                R.drawable.lesson35_assessment_group_d,
                "Dear Mr. Underwood: At one of the regular meetings of the Public Roads Commission in September, I wish you would introduce the subject of repaving State Street, which is in very unsatisfactory condition. As you probably know, there are two or three major intersections on State Street, and the main entrance to Billings Elementary School is near one of them. Everyone probably recognizes the importance of doing something about State Street, but apparently none of the members of the commission are willing to take the initiative to do anything until we have had a bad accident on that street. Until now, the president of the commission has been unwilling even to entertain a motion that the street be repaved.\n" +
                        " I hope, Mr. Underwood, that you will speak in favor of an appropriation for repaving the street and recommend awarding the job to the General Construction Company.\n" +
                        " Thank you for whatever help you can give us in solving this difficult and particularly annoying matter. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                35,
                5,
                R.drawable.lesson35_assessment_group_e,
                "Dear Mr. Jennings: Would you like to attend an informative, interesting, and entertaining demonstration of the unique art of cake decorating? Mr. Edwin Lopez, one of the finest pastry chefs in the world. will demonstrate his decorating proficiency at a meeting that the Interboro Department Store will hold on Friday, October 15, between 5 and 6. o'clock on the third floor. After the demonstration there will be a short question-and-answer session.\n" +
                        " If you are interested in attending. Mr. Jennings, you should return promptly the coupon attached to the enclosed circular. We have provided an envelope for that purpose. We will send you an entrance card as soon as we receive the coupon. While you are here, you will have a good opportunity to see our international collection of cake-decorating devices, all of which you will be able to order at specially reduced prices. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                35,
                6,
                R.drawable.lesson35_assessment_group_f,
                "Dear Mrs. Hastings: If you were to advertise in our publication, Modern Business Correspondence, you could reach 500,000 well-informed, enterprising, and successful business executives with your message. These people have the income to purchase large quantities of modern home furniture, paintings, and other articles. They can afford the best. Many of them have incomes well over $50,000 a year.\n" +
                        " A schedule of our rates is enclosed. If you are interested in receiving further information. Mrs. Hastings, just let us know. Sincerely yours\n",
                1
        ));
        // Lesson 36 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                36,
                1,
                R.drawable.lesson36_assessment_group_a,
                "Business and professional people, public officials, and government executives recognize that the administrative secretary of today has achieved a position of respect and esteem through hard work, difficult study, and creative effort. The person in this position often has the title of executive secretary or assistant and may have earned the rating off professional secretary. The person may have even won an award or received some other honor in the secretarial field.\n" +
                        "     Many business people feel that the executive secretary is as valuable to a company as its manufacturing plant or its equipment or, in some instances, the boss.\n" +
                        "     The competent, efficient secretary is further described as a kind of all-purpose staff member – a diplomat, a helpful researcher, and a \"business lawyer.\"\n" +
                        "     Secretaries to top executives usually earn between $15,000 and $25,000 a year after a few years of experience – and they are well worth the money. Working at the top level of management, they must be able to keep an office running smoothly and must have the objective judgment to know when to issue orders in the boss's name. They conserve the boss's time by screening telephone calls, visitors, and correspondence. But they keep the boss well informed on all important details. They are sensitive to undercurrents in human relations and are ready to resolve internal and external problems promptly when they discover them.\n" +
                        "     A secretary has been characterized as one who can think for the boss, act for the boss, anticipate the boss's wishes, and help to increase the company's productivity.\n" +
                        "     The right secretary can convey a favorable image, please friends, and soothe enemies.\n" +
                        "     The proficient secretary must be\n" +
                        "able to accomplish a great quantity of work, and the work must be\n" +
                        "completely accurate. When a secretary corresponds with an executive in another company, that secretary actually represents his or her entire organization in the executive's eyes. If a letter contains an error, the company's image could be damaged. But if the letter is perfect, very good feelings will be created.\n" +
                        "     Extensive interviews with 300 executives recently revealed that secretaries were spending almost 60 percent of their time with some of the minor operations of the business, such as handling forms, typing reports, and acknowledging general correspondence. They were spending about 10 percent of their time attending meetings and more than 15 percent making phone calls which could easily have been handled by a competent assistant. Unfortunately, only about 15 percent of their time was actually devoted to practical planning. Still, this survey clearly reveals why business executives attach so much importance to having capable secretaries and why they want responsible persons who cannot only handle the ordinary routine work but can also serve as their assistants and free their time for long-range planning, the most important part of an executives job.\n" +
                        "     Throughout the years secretarial positions have been filled largely by women. A few years ago, though, many business organizations started actively looking for both men and women secretaries who had the makings of future executives.\n" +
                        "     The job of professional secretary never looked brighter than it does today. And it is expected to look even brighter in the days ahead.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                36,
                2,
                R.drawable.lesson36_assessment_group_b,
                "It is sometimes said that the person who gets ahead in business is the person who can follow through. Exactly what does this mean? All it really means is that the person who can stick with a particular job until it is satisfactorily completed is the one who will probably receive credit for doing the work. It is usually very easy to begin a job. It takes diligence and effort, though, to stay with a job until it is successfully finished. The person who can initiate a job is a valuables asset to a company, of course. But the person who can follow through is of much greater importance.\n" +
                        "     Do not accept a job if you think that you cannot finish it. But once you have accepted a job, you should take advantage of every opportunity to turn out a completely finished product. You should do this no matter how long it takes or how much effort you must expend. When you follow through on every project you undertake, you will soon be recognized as a person who gets things done. In addition, you will be well on your way up the ladder of success.",
                1
        ));
        // Lesson 37 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                37,
                1,
                R.drawable.lesson37_assessment_group_a,
                "Gentlemen: I am embarrassed. I am, in fact, exceedingly embarrassed. I just discovered your letter of Friday, November 18, requesting our opinion of the financial condition of the Bryant Furniture Manufacturing Company of Des Moines. I assure you that ordinarily I acknowledge my correspondence more promptly. \n" +
                        "\n" +
                        "Throughout the period of time that we have dealt with several of the senior executives of this company, we have never had an unpleasant experience. These executives are practical, forceful, and responsible men and women.\n" +
                        " The company is outstandingly successful. Since they entered the furniture field about fifteen years ago, their financial situation has improved satisfactorily every year. If there is any other information I can add to this short report, please let me know. Sincerely your",
                1
        ));
        assessmentList.add(new AssessmentItem(
                37,
                2,
                R.drawable.lesson37_assessment_group_b,
                "Dear Mr. Jennings: Thank you for sending to us for possible publication your manual entitled Improving Employee Morale. I read it yesterday afternoon and found it to be interestingly, convincingly, and impartially written. I was genuinely impressed with it. \n" +
                        "\n" +
                        "I wish I could tell you that there is an exceedingly large market for a manual of this character. Your manual, unfortunately, cannot be sold in quantity lots. It would have to be sold by correspondence on a single-copy basis and, obviously, would have to be priced accordingly. Because of the serious and difficult marketing situation your manual presents, Mr. Jennings, I must decline to publish it.\n" +
                        " If you should do any writing in some other subject-matter area, please give us an opportunity to review your work. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                37,
                3,
                R.drawable.lesson37_assessment_group_c,
                "Dear Mr. Dexter: If you are not presently employed but would seriously like a job in business, in government, or in industry, read the classified ads in our newspaper, World News. We think you will be particularly interested in the jobs offered by the various private employment agencies. \n" +
                        "\n" +
                        "Employment agencies are the suppliers for personnel. Thousands of employers rely on them increasingly to find good clerical assistants. Employment agencies depend on World News for job applicants. They run more listings in our news-paper than in all other public news-papers in this area combined. They recognize the unusual impact and pulling power of World News, and they are impressed by our ads, which are convincingly written and expertly displayed. \n" +
                        "If your immediate objective is a better job, turn to the classified ads in World News, one of the state's leading newspapers. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                37,
                4,
                R.drawable.lesson37_assessment_group_d,
                "Dear Ms. Worth: If you are to improve your financial condition, it is important that you save regularly. It is surprisingly easy to save at the Empire Bank for Savings. You will be exceedingly impressed as you watch your account grow steadily, almost from day to day, with the addition of large quarterly dividends. If you want to provide for your financial future, stop at the Empire Bank for Savings the next time you are in the State Street area. We are one of the most progressive financial organizations in Chicago. If you come in, a friendly, courteous, and genuine welcome by our employees awaits you, Ms. Worth. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                37,
                5,
                R.drawable.lesson37_assessment_group_e,
                " Mrs. Overton: The personnel department has informed me that on Tuesday, November 15, you will complete twenty-five years of service with the General Envelope Company. This is genuinely hard for me to believe because your outlook is so refreshingly young. I well remember what a good impression you made the day you were employed and were introduced to me by Dr. Underwood. You have certainly made a very definite mark on our organization with your exceedingly valuable suggestions and your practical ideas on how to improve our advertising. I know I speak for everyone when I say that you have unquestionably won the high regard, affection, and respect of all our employees. \n" +
                        "I hope, Mrs. Overton, that you will be with us for many years to come. If you have an opportunity, stop by my office for a personal conversation sometime soon. I will be glad to see you. James J. Quinn",
                1
        ));
        assessmentList.add(new AssessmentItem(
                37,
                6,
                R.drawable.lesson37_assessment_group_f,
                "Dear Friend: This is the fourth report of the year on the very difficult problems we have experienced operating The Empire State Railroad.\n" +
                        "Like everyone else, we are affected by the nation's serious energy shortage. Although we genuinely believe that railroads are one of the most efficient forms of travel, our supplies of fuel for our engines have, unfortunately, been subjected to the same pressures as other travel industries.\n" +
                        "While we had willingly under-taken measures to conserve energy some time ago, we are now considering implementing more stringent measures. We hope that these new measures will not cause any major reduction of essential services.\n" +
                        "You may be sure that we are always working to make travel on our trains more enjoyable and comfortable. The Empire State Railroad",
                1
        ));
        // Lesson 38 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                38,
                1,
                R.drawable.lesson38_assessment_group_a,
                "Dear Mrs. Billings: As you will see by the enclosed circular that was issued by the government several days ago, the Suburban Monthly was one of the fastest growing major publications during the past two quarterly periods. Of the 47 publications tabulated, 22 declined in circulation, 13 had an increase of less than 5 percent in circulation, and the rest had substantial losses in readership ranging from 10 to almost 20 percent. While the majority of publications decreased in readership, the Suburban Monthly enjoyed an exceedingly impressive 31 percent increase in circulation. \n" +
                        "If you are an advertising executive who would be interested in sampling our publication, we will be glad to give you a special introductory subscription for only $5. Simply submit your request on your regular company letterhead and send it to us in the enclosed envelope. We will fill your order immediately. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                38,
                2,
                R.drawable.lesson38_assessment_group_b,
                "Dear Mrs. Jennings: Which publication has a greater readership than any other publication among the people who live and work in the suburbs? Suburban Monthly is read by a majority of the people in suburban areas. \n" +
                        "\tOur readership is actually 25 percent more than that of the second place periodical. We are the acknowledged leader in the field. The reason is that most persons find something impressive, interesting, and stimulating in every issue of our publication. This is something they cannot usually find in other magazines. They also know that our experienced editors have a reputation for honesty and integrity in advertising and reporting. \n" +
                        "\tFor a copy of our advertising rates, fill out, sign, and return the enclosed card. As you will see, our rates are amazingly low. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                38,
                3,
                R.drawable.lesson38_assessment_group_c,
                "Dear Mr. White: It is my pleasant duty as corresponding secretary to tell you that your application for membership in the International Chemical Association has been accepted. May 12 congratulate you, Mr. White. Your embossed membership card and a booklet containing our rules and regulations are enclosed. \n" +
                        "\tAs you may know, one of the advantages of your membership is a subscription to our quarterly magazine, The World Chemist. In each issue of this periodical you will find objective, helpful, and stimulating articles by recognized authorities on many subjects of interest to chemists. \n" +
                        "\tIt is my opinion that your membership in our organization will be a source of many everlasting friendships and satisfying, successful business relationships in the years ahead. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                38,
                4,
                R.drawable.lesson38_assessment_group_d,
                "Dear Dr. Fenton: If you are like the majority of people, you probably don't know how much your social security is worth under present government regulations. Most people don't think much about social security until it is time to retire.\n" +
                        "\n" +
                        "\tSocial security regulations are steadily changing. As 15 am sure you have read in the newspapers, a bill recently became law that will again raise social security tax deductions for both employer and employee.\n" +
                        "\n" +
                        "\tIf you want to know what you may eventually receive when you retire, may I suggest that you stop in soon at the Suburban Township branch of the Miami Bank on Field Street and speak to one of our experienced social security officers. He or she will be glad to calculate the approximate amount you will receive when you retire or if you should become disabled. It is surprisingly easy to get this valuable information. You will have it in a matter of only a few minutes time, Dr. Fenton. Yours very truly\n",
                1
        ));
        // Lesson 39 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                39,
                1,
                R.drawable.lesson39_assessment_group_a,
                "Dear Mr. Brooks: After almost a year of work, a special committee consisting of responsible business executives of our town completed the most objective transportation survey in our history. The survey cost the town government about $25,000.\n" +
                        "The transportation committee submitted a comprehensive memorandum offering four alternative solutions to our transportation needs, together with supporting statistics and photographs. We think the time has definitely arrived when we must decide what the character of our transportation system should be in the future.\n" +
                        "On Saturday morning, November 18, we will hold an important public meeting in the faculty lounge of the Fifth Street School to discuss the survey. We hope you will find it convenient to attend. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                39,
                2,
                R.drawable.lesson39_assessment_group_b,
                "Dear Ms. Temple: Here is an opportunity that an opera lover like you should not pass by. Never before have we been able to make such an attractive offer to our subscribers.\n" +
                        "     Enclosed is an advertising circular listing 50 high-fidelity recordings. Each recording features recognized, well-known, and outstanding opera personalities. We will send you as many of these high-fidelity recordings as you wish for the surprisingly low price of only $2 apiece. Simply indicate on the order blank at the bottom of the circular the high-fidelity recordings you want and return the blank to us, together with your check, in the envelope we have provided.\n" +
                        "     Take advantage of this unusual offer today, Ms. Temple. When you receive the recordings, play them for five days. If after that time you have not convinced yourself that these recordings are the finest in tonal quality and clarity, you may return them. We will speedily refund your money. Very truly yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                39,
                3,
                R.drawable.lesson39_assessment_group_c,
                "Dear Mrs. Gates: Thank you for registering at the Ryan Hotel, Mrs. Gates.\n" +
                        "     Did you ever stop to think what a\n" +
                        "compact facility the Ryan Hotel actually is? When you walk around the hotel, you will find that it is almost a small city in itself.\n" +
                        "     Some of our employees you see every day, but many are unseen. Even though they work hard behind the scenes, they themselves are seldom seen. The enclosed descriptive booklet will introduce you to all those who serve you and will tell you about their particular responsibilities.\n" +
                        "     As you probably know, the Ryan Hotel has a personality of its own. It has won for itself an impressive reputation for the quality of its services.\n" +
                        "     We hope that you enjoy yourself while you are here with us. If we can do anything to make your stay more satisfying, do not hesitate to call us. Sincerely  yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                39,
                4,
                R.drawable.lesson39_assessment_group_d,
                "Gentlemen: When I talked with Mr. Knox a short time ago. I discussed with him the possibility that the faculty of the World Cortespondence Schools might hold its general annual meeting at the Hotel Worth. The faculty members voted yesterday to avail themselves of your facilities on October 15, 16, and 17.\n" +
                        "     Would you be good enough, therefore, to reserve single rooms for the faculty members I have listed on the enclosed form. I will also need a large suite for myself in which I can hold faculty conferences and entertain guests between the hours of nine and eleven each morning.\n" +
                        "     I would appreciate it if you would acknowledge these reservations as soon as possible. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                39,
                5,
                R.drawable.lesson39_assessment_group_e,
                "Dear Mr. Underwood: As I am sure you will agree, there are times when you critically need objective information on a particular subject, like insurance. You might need this information in order to formulate plans or to make an extremely important business decision, but you might not know just where to find a it. When this happens, come to us, the Public Insurance Information Company.\n" +
                        "     We have substantial quantities of information in our files. In all probability, we can supply the information you want immediately. If we ourselves don't have the information we know where to look for it. We have overnight access from several other sources to any ordinary business information that has been published in newspapers and other publications in the last ten years.\n" +
                        "     Call us, Mr. Underwood, when you have a question regarding any aspect of casualty, liability, or other type of insurance, Yours very truly,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                39,
                6,
                R.drawable.lesson39_assessment_group_f,
                "Dear Mr. Stern: Many people want to live in the suburbs, but they are not thinking of actual home ownership. They are interested in living in an efficiency apartment. These people do not wish to be tied down to a house with its hardships, difficulties, and troubles. They do not want the responsibility of having to write checks each month for utility bills. They do not want the responsibility of maintaining a yard. They want to be able to write one check to cover all expenses.\n" +
                        "     As you probably know, these people have substantially different interests from those who own houses. They ordinarily have more time and money too purchase the things they desire.\n" +
                        "     Our magazine, The Apartment Dweller, is published for people of this character. Our present circulation is about 500,000. If you haven't seen a copy, we will be glad t o send you one. Just fill out the enclosed card and return it to us. When we receive it, we will send you your first copy immediately. It won't take long, Mr. Stern, for you to prove to yourself that The Apartment Dweller is unquestionably the medium in which you should advertise. Sincerely yours,",
                1
        ));
        // Lesson 40 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                40,
                1,
                R.drawable.lesson40_assessment_group_a,
                "Dear Ms. Poland: Have you always wished to acquire the ability to speak a foreign language but felt you didn't have the aptitude to do so? Here is an unusual opportunity for you to discover whether you have that aptitude with no financial expenditure on your part. \n" +
                        "Come to the Empire Language Institute on Banks Street, where we will give you, without charge, your first lesson in the language of your choice. If you enroll and are not satisfied with your progress at any time, we will immediately refund your money on request.\n" +
                        " After you have completed one of our valuable courses, we think you will congratulate yourself on your ability to speak a second language easily and fluently. Sincerely yours, PS. A complete listing of all our courses is included in the enclosed circular.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                40,
                2,
                R.drawable.lesson40_assessment_group_b,
                "Dear Ms. Gates: Would you like to learn a second language, taught with the most modern and successful methods? At the Imperial Language Institute, you will study with Dr. Carlos Lopez, dean of our faculty, and his staff.\n" +
                        " As you have probably read in the newspapers, Dr. Lopez has developed psychologically sound methods for language teaching and has contributed several articles of major importance to educational publications on the subject. After only two or three hours' work, you should be able to carry on an elementary conversation in a second language without any difficulty. \n" +
                        "We hope you will come in soon, Ms. Gates, and let us give you your first lesson free of charge. You will be glad you did. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                40,
                3,
                R.drawable.lesson40_assessment_group_c,
                "Dear Mr. Dwyer: As a well-deserved tribute to the inspiration and leadership of Henry Trenton, chief executive of the Trenton Hotel Corporation, our board of directors decided yesterday to change the name of the famous Interboro Hotel to the Trenton Hotel.\n" +
                        "\n" +
                        " In addition to being one of the world's largest hotels, the Trenton has won for itself an enviable reputation throughout the world as a friendly and most efficient public institution. The Trenton contains more than 3,000 rooms and requires the services of over 2,000 people. All of them are anxious to make your stay as pleasant as possible.\n" +
                        " We hope, Mr. Dwyer, that you will stay with us frequently. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                40,
                4,
                R.drawable.lesson40_assessment_group_d,
                "Dear Dr. Knox: I was particularly sorry to learn in this morning's newspaper that you intend to submit your resignation as president of the Interboro Psychological Institute shortly after the first of the year. I wish I could persuade you to change your mind. I understand, though, that your decision to leave the institute is final.\n" +
                        "\n" +
                        "\n" +
                        " As everyone will acknowledge, you have introduced many extraordinary, useful, and important ideas to the Interboro Psychological Institute and have guided it throughout its difficult formative years. Your influence will be felt for a long time. The citizens of our township owes you a public debt of gratitude. \n" +
                        "\n" +
                        "Even though you will retire, I know that you will never hesitate to give the institute the advantage of your experience, your ideas, and your opinions in the future. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                40,
                5,
                R.drawable.lesson40_assessment_group_e,
                "Dear Mrs. Stern: If you have already been approached for a contribution to the Stamford Neurological Institute fund, please forgive this letter. Sometimes because of the shortage of general clerical help and the increase in the quantity of paperwork in our correspondence department, contributions are not entered on the books as promptly as they might be.\n" +
                        " As you may know, the institute is in the process of conducting a capital fund drive, the object of which is to build the new facilities that we will require. In the fourteen years since the present building was built, the character of Stamford and the surrounding areas has changed substantially, and its population has almost doubled. Consequently, we must expand, and we want your help. \n" +
                        "\n" +
                        "Any contribution, Mrs. Stern. will be accepted with gratitude. Please use the stamped, addressed envelope that is enclosed. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                40,
                6,
                R.drawable.lesson40_assessment_group_f,
                "Gentlemen: I wish I could write you that I am enclosing a check as the World Advertising Company's contribution to the annual fund drive of the Wilson Institute of Technology. But because our organization frequently receives requests from almost every state in the union in which we contribute to responsible charities and other worthy causes. our board of governors now requires us to maintain a budgetary limit on the contributions we can make. Unfortunately, our funds for the present year were exhausted several months ago. \n" +
                        "We recognize the fact that Wilson Institute's cause is an exceedingly good one. but we regret that it will be impossible for us to contribute to it. Sincerely yours",
                1
        ));
        // Lesson 41 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                41,
                1,
                R.drawable.lesson41_assessment_group_a,
                "Dear Mr. Dixon: Friday, August 18, is a significant day for the Empire Photographic Institute. A memorandum in our files reveals that this day represents the tenth anniversary of your initial transaction with us. It has been a privilege to take care of your photographic needs over the years. Please accept our gratitude and thanks for your business and your friendship, which have contributed so substantially to our success.\n" +
                        "\n" +
                        "While you have frequently purchased large quantities of photographic supplies, you have never requested us to open a regular account for you. A charge account at the Empire Photographic Institute is a genuine convenience, Mr. Dixon. We recommend that you have one. When you have a charge account, you can actually complete each business transaction in a fraction of the time it would ordinarily take. You can even place orders by telephone.\n" +
                        " If you wish to avail yourself of the advantages of our credit facilities, simply fill out and return the en closed form. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                41,
                2,
                R.drawable.lesson41_assessment_group_a,
                "Dear Mr. Garcia: At the meeting of the transportation committee of Westport on November 26, Dr. James Swift will present an interesting, entertaining, and enlightening slide report on the transportation needs of our town. Dr. Swift actually performed all the significant statistical research work involved in this report.\n" +
                        "If you have not heard Dr. Swift speak before, you are in for a special treat. He is recognized throughout the world as an acknowledged leader in the field of transportation. Immediately following Dr. Swift's report, everyone will have an opportunity to ask pertinent questions, to voice opinions, and to suggest new ideas.\n" +
                        "As you are a recognized authority in the transportation area, we hope you will attend and give us the benefit of your experience. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                41,
                3,
                R.drawable.lesson41_assessment_group_a,
                "Dear Mr. Dixon: Friday, August 18, is a significant day for the Empire Photographic Institute. A memorandum in our files reveals that this day represents the tenth anniversary of your initial transaction with us. It has been a privilege to take care of your photographic needs over the years. Please accept our gratitude and thanks for your business and your friendship, which have contributed so substantially to our success.\n" +
                        "\n" +
                        "While you have frequently purchased large quantities of photographic supplies, you have never requested us to open a regular account for you. A charge account at the Empire Photographic Institute is a genuine convenience, Mr. Dixon. We recommend that you have one. When you have a charge account, you can actually complete each business transaction in a fraction of the time it would ordinarily take. You can even place orders by telephone.\n" +
                        " If you wish to avail yourself of the advantages of our credit facilities, simply fill out and return the en closed form. Sincerely yours,\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                41,
                4,
                R.drawable.lesson41_assessment_group_a,
                "Mr. Cummings: Yesterday I purchased a transistor radio and phonograph combination which I had planned to present to Mr. Harold Franklin on the occasion of his twenty-fifth anniversary as head of the statistics department of the Transworld Manufacturing Company. Unfortunately, I will not be able to make this presentation. \n" +
                        "A few hours ago I received from the president of our organization a memorandum asking me to take part in several exceedingly critical advertising meetings in London on August 15, the date of Mr. Franklin's anniversary. I am very reluctant to miss the anniversary celebration, of course, but I have no choice. \n" +
                        "Could you arrange a dinner in Mr. Franklin's honor and present him with his gift? If you can, my secretary will send the transistor radio and phonograph combination to you as soon as it arrives. Please let me know by July 25% if you can arrange to do this. James J. Green",
                1
        ));
        assessmentList.add(new AssessmentItem(
                41,
                5,
                R.drawable.lesson41_assessment_group_a,
                "Dear Ms. Worth: May I congratulate you on your latest publication,The Psychology of Business Correspondence. It is no ordinary book. It is the most significant contribution you have ever made to the field of general education. Your publications in the subject areas of philosophy, statistics, and transaction analysis are highly regarded, but I think The Psychology of Business Correspondence is easily your finest work. Please accept my sincere congratulations. \n" +
                        "When I am in New York, I want you to autograph my copy. Sincerely yours,",
                1
        ));
        // Lesson 42 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                42,
                1,
                R.drawable.lesson41_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 43 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                43,
                1,
                R.drawable.lesson43_assessment_group_a,
                "Gentlemen: When I talked to you on November 10, you promised to print and deliver to us by Monday, December 15, an order of 3,000 copies of the circular advertising our book, Misunderstandings and Misconceptions of Supervisors in Business. The circulars were delivered yesterday morning, but several hours ago my assistant, Mr. Under-wood, discovered a major difficulty. The name of the author has been misspelled throughout the circular. How this exceedingly embarrassing mistake occurred is a mystery to me, for his name is spelled correctly on the manuscript from which you set the circular. As photographic copy is enclosed.\n" +
                        "\n" +
                        " Because of this misprint, we will not be able to use the circulars. Consequently, we must request that you do them over immediately. We hope you will do this speedily and efficiently. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                43,
                2,
                R.drawable.lesson43_assessment_group_b,
                "Dear Commuter: In September we plan to introduce a new service. We plan to place a \"commuting classroom on our commuter train. You will have an excellent opportunity to earn the equivalent of a master's degree in office supervision while you are commuting between your home community and your office. The class will be supervised by Dr. James Langdon, and all work will be completed while you are on the commuter train\n" +
                        "College graduates should find this commuting classroom of particular interest to them. They can now work on an advanced degree while continuing to earn income and gain significant business experience.\n" +
                        " The commuting classroom is fully described in the enclosed circular. If you have any questions about the course, you may call our main office at 555-1026. We will be glad to answer them. \n" +
                        "Take advantage of this superb opportunity and enroll without delay. Just fill out, sign, and return the enclosed form in the envelope that we have provided. You will be making no mistake. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                43,
                3,
                R.drawable.lesson43_assessment_group_c,
                "Mr. Hastings: I am transmitting to you with this memorandum a manuscript entitled \"Music for the Masses\" by Ms. Lydia Lyons, supervisor of music at the Municipal Institute of Fine Arts 2 in Westport. If I am not mistaken, this is the same Ms. Lyons who submitted to us a manuscript on government support of the arts more than ten years ago. We rejected that manuscript at that time\n" +
                        " In my opinion, this manuscript is superior, and we should seriously consider publishing it next year. I have had my good opinion confirmed by Dr. Charles H. Radford, superintendent of the National Music Academy in Easton. After reviewing the manuscript, Dr. Radford made this comment: \"I have neverread such an interesting, entertaining manuscript. You will be making no mistake if you publish it.\"\n" +
                        " If you agree that we should publish \"Music for the Masses,\" please acknowledge receipt of the manuscript and send Ms. Lyons our regular royalty contract. Max C. Rogers",
                1
        ));
        assessmentList.add(new AssessmentItem(
                43,
                4,
                R.drawable.lesson43_assessment_group_d,
                "Dear Mrs. Lincoln: John C. Swanson, president of International Enterprises, which is one of the largest manufacturers of calculators in the world, pays this tribute to the Nelson Correspondence Course for Supervisory Personnel: \"In this continuing education course our men and women learn about modern supervisory procedures. They learn how to play a leadership role that is mutually beneficial to the company and to themselves. All our present supervisors, superintendents, and other supervisory executives have taken this valuable course, and we plan to continue to have all new executives take it.\" \n" +
                        "Why not give your executives an opportunity to improve themselves by taking our correspondence course. If you want further details regarding the Nelson Correspondence Course for Supervisory Personnel, call us at 555-8164. You will be making no mistake, Mrs. Lincoln. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                43,
                5,
                R.drawable.lesson43_assessment_group_e,
                "Dear Mr. Smith: What will the Mutual Insurance Company course for supervisors do for your execu-tives? Here are a few things:\n" +
                        " 1. It will increase their under standing of the functions and importance of insurance and correct any misapprehensions and misconceptions they may have about it.\n" +
                        " 2. It will develop their ability to build teamwork, stimulate interest. and create enthusiasm among their employees. \n" +
                        "Would you like to learn more about the Mutual Insurance Company course for supervisors? We will be glad to give you full information about this management course if you will call us at 555-9263.6 Sincerely yours,\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                43,
                6,
                R.drawable.lesson43_assessment_group_f,
                "Dear Ms. Worth: It was ex-tremely thoughtful of you to write me a congratulatory note regarding my appointment as superintendent of the Albany Public School System. As I am sure you realize, the job of superintendent is one of very great responsibility. I say with all sincerity that definitely need the support and backing of everyone. \n" +
                        "I genuinely appreciate receiving your practical suggestions, and I hope that you will continue to let me have the benefit of your ideas in the days ahead. Sincerely yours",
                1
        ));
        // Lesson 44 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                44,
                1,
                R.drawable.lesson44_assessment_group_a,
                "Ladies and Gentlemen: Making satisfactory progress in the business world requires more than just knowing the technical part of your job superbly well. It also requires the ability to work efficiently with others.\n" +
                        "You can develop this very important ability by enrolling in the special self-improvement course of the Municipal Technological Institute. In this short course you will build self-confidence, self-reliance, and self-sufficiency. You will learn to lis-ten to others with more genuine interest and to recognize the things that usually motivate them. You will improve your ability to communicate successfully with executives and to avoid annoying, embarrassing misunderstandings. You will dis cover your own hidden, undeveloped aptitudes and qualifications.\n" +
                        "After you have completed the course, you will look upon your self-improvement with gratification and self-appreciation. Under the circumstances, don't you think you should investigate what our self-improvement course offers you? Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                44,
                2,
                R.drawable.lesson44_assessment_group_b,
                "Dear Ms. Quinn: Under the Self-Employment Retirement Law in effect today, self-employed persons now have the opportunity to claim a deduction of up to 15 percent of their yearly gross income for federal tax purposes. If you fall into this self-employment classification. you should set up a self-retirement plan for yourself.\n" +
                        "When you make a direct contribution to your self-retirement account in our bank, you receive almost 8 percent interest if you agree to keep $1,000 or more on deposit for four to seven years. All interest on your contributions is tax-deferred until your retirement. There is, of course, a substantial penalty for early withdrawal.\n" +
                        "Under the circumstances, Ms. Quinn, shouldn't you, as a matter of your own self-interest and future prosperity, let us set up a regular self-retirement plan for you without delay? It is the sensible, practical thing to do. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                44,
                3,
                R.drawable.lesson44_assessment_group_c,
                "Dear Mrs. Dempsey: Every day good things seem to happen at the Freeport Savings Bank. Now you can pay all your outstanding bills by check, and it won't cost you anything, thanks to our new, absolutely free checking account service. Under no circumstances will you be charged for checking privileges regardless of the quantity of checks you actually write.\n" +
                        "In addition, you will have the advantage of being able to cash checks in any of our more than 200 branches throughout the state. All you need to do is present your personal identification card to the teller, and your checks will be cashed without delay.\n" +
                        "Enjoy the self-satisfaction that comes with the knowledge that you own a valuable, expense-free checking account. If you want further information, please request it on the enclosed form and mail it to us in the stamped, self-addressed envelope we have provided for your convenience. We will send it to you promptly, Mrs. Dempsey. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                44,
                4,
                R.drawable.lesson44_assessment_group_d,
                "Dear Mr. Garcia: When you buy a United water heater, you buy the best. We say this with complete justification because government statistics show that the United is made under substantially more rigid specifications than those observed throughout the rest of the home appliance industry. Our heater is lined with glass, and it has insulation that is two inches thick. Furthermore, the United is guaranteed by our organization for the first five years of ownership against manufacturing defects.\n" +
                        "\n" +
                        "But do not take our word for it: prove it to yourself. When the circumstances arise, purchase a well-made, economical water heater: get a United. You will have the self-confidence that comes in knowing you have purchased the very best. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                44,
                5,
                R.drawable.lesson44_assessment_group_e,
                "Dear Dr. Sweet: Haven't you often dreamed of having all the hot water you want when you want it? A Yale heater, which is made by the General Manufacturing Company. the acknowledged leader in the home appliance field, will easily supply you with more hot water than you will ever actually need under ordinary circumstances - and probably under even extraordinary circumstances.\n" +
                        "If you want to save money on your water heating bills, send for our self-explanatory advertising circular immediately. It will answer objectively whatever questions you may have about the specifications of our equipment. A self-addressed, stamped envelope is enclosed, Dr. Sweet. Sincerely yours",
                1
        ));
        assessmentList.add(new AssessmentItem(
                44,
                6,
                R.drawable.lesson44_assessment_group_f,
                "Dear Mr. Long: Subject: Your Bill of December 18.\n" +
                        " Yesterday morning Dr. White. head of our publications division, transmitted to me your bill of December 18 together with his correspondence with you regarding the modifications of the specifications for our faculty library at the Transportation Building at 3104 West 18 Street. As you know, he wrote you that in his opinion the modifications made were of a minor character and that the figure of $550 is exceedingly high. \n" +
                        "Under the circumstances, I will need an itemized memorandum of the work you did in order to be able to present your bill to our controller for payment. Would you be good enough to supply the information on the form that is enclosed. A self-addressed, stamped envelope is provided for your convenience. Sincerely yours",
                1
        ));
        // Lesson 45 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                45,
                1,
                R.drawable.lesson45_assessment_group_a,
                "Ladies and Gentlemen: Beginning Monday, June 15, Ms. Mary Edwards will be responsible for supervising the development of a marketing plan for the Quinn Anthology for which we have recently contracted. As I wrote you in my memorandum of April 15, we expect that this important acquisition will ultimately sell over 5 million copies and will bring in somewhere in the neighborhood of $45 million in revenue.\n" +
                        "     We feel that the Quinn Anthology is in good, capable hands. Fred Farmers",
                1
        ));
        assessmentList.add(new AssessmentItem(
                45,
                2,
                R.drawable.lesson45_assessment_group_b,
                "Ladies and Gentlemen: Beginning Monday, June 15, Ms. Mary Edwards will be responsible for supervising the development of a marketing plan for the Quinn Anthology for which we have recently contracted. As I wrote you in my memorandum of April 15, we expect that this important acquisition will ultimately sell over 5 million copies and will bring in somewhere in the neighborhood of $45 million in revenue.\n" +
                        "     We feel that the Quinn Anthology is in good, capable hands. Fred Farmers",
                1
        ));
        assessmentList.add(new AssessmentItem(
                45,
                3,
                R.drawable.lesson45_assessment_group_c,
                "Ladies and Gentlemen: Beginning Monday, June 15, Ms. Mary Edwards will be responsible for supervising the development of a marketing plan for the Quinn Anthology for which we have recently contracted. As I wrote you in my memorandum of April 15, we expect that this important acquisition will ultimately sell over 5 million copies and will bring in somewhere in the neighborhood of $45 million in revenue.\n" +
                        "     We feel that the Quinn Anthology is in good, capable hands. Fred Farmers",
                1
        ));
        assessmentList.add(new AssessmentItem(
                45,
                4,
                R.drawable.lesson45_assessment_group_d,
                "Dear Ms. Torres: You will be interested in the results of a statistical study recently conducted by the Empire Opinion Research Company for the government. The study included important executives, superintendents, and general managers in 42 business neighborhoods throughout the state. The study indicated that 75 percent of those who were consulted regularly read The World Telegraph. We are, with some justification, exceedingly proud of this extraordinary record.\n" +
                        "     When you advertise in our newspaper, you can expect substantial results because it is the place where forward-thinking business executives look for facts and figures that ultimately influence their business decisions.\n" +
                        "     Place your advertising messages in The World Telegraph, Ms. Torres. They will be read not only by those who have won their way to the top but also by those who are on their\n" +
                        "way upward.\n" +
                        "     We look forward to receiving your first advertisement. Sincerely yours,",
                1
        ));
        assessmentList.add(new AssessmentItem(
                45,
                5,
                R.drawable.lesson45_assessment_group_e,
                "Dear Mr. Poland: As you know, one of our consultants, Dr. Lee Chang, had an opportunity to examine the lawn in the backyard of your manufacturing plant on Market Street yesterday morning. He forwarded his report directly to us today. On the basis of his complete report, we are very happy to give you an estimate in the neighborhood of $500 for reconditioning your lawn as follows:\n" +
                        "     1. Reseed the lawn, which is 40 feet by 100 feet.\n" +
                        "     2. Furnish the 40 pounds of Grade A grass seed required for the job.\n" +
                        "     3. Continue to care for the lawn for a period of two complete years.\n" +
                        "     If this estimate meets with your approval, we suggest that you let us know immediately. We will then begin work promptly so that your lawn will have the advantage of the early spring growing weather. Incidentally, we guarantee that the actual cost will never exceed the estimate by more than 10 percent under any circumstances. Sincerely yours,\n",
                1
        ));
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }
}
