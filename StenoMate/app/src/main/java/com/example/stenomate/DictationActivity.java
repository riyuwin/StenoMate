package com.example.stenomate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stenomate.Sqlite.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.Locale;

public class DictationActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private LinearLayout btnSpeak;
    private HeartBeatView heartbeat;
    ArrayList<AssessmentItem> assessmentList;
    int lesson_number, assessment_list_group_name;

    private ListView listAttempts;
    private ArrayAdapter<String> attemptsAdapter;
    private ArrayList<String> attemptsList = new ArrayList<>();
    private MyDatabaseHelper dbHelper;

    private TextView DictationNumber;
    private int attemptCounter = 0; // optional counter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictation);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        DictationNumber = findViewById(R.id.questionNoId);

        Intent intent = getIntent();
        lesson_number = intent.getIntExtra("lesson_number", 0);
        assessment_list_group_name = intent.getIntExtra("dictation_list_group_name", 0);

        btnSpeak = findViewById(R.id.btnSpeak);
        heartbeat = findViewById(R.id.heartbeat);

        DictationNumber.setText("Dictation No. " + lesson_number + " - " + assessment_list_group_name);

        // To generate dictation list
        DictationListGenerator();

        // Init TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.CANADA); // pwede mo baguhin (e.g., Locale.UK, Locale.CANADA, Locale("fil", "PH"))
                tts.setSpeechRate(0.5f); // mas mabagal (default = 1.0f)
                tts.setPitch(1.0f);      // pitch normal (pwede rin baguhin)
            }
        });

        btnSpeak.setOnClickListener(v -> {
            // disable button so it can only be pressed once until done
            btnSpeak.setEnabled(false);

            AssessmentItem item = getAssessmentItem(lesson_number, assessment_list_group_name);

            if (item != null) {
                String dictationText = item.getAnswerKey();

                MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
                boolean inserted = dbHelper.insertDictation(
                        lesson_number,
                        assessment_list_group_name
                );

                if (inserted) {
                    Toast.makeText(this, "Your dictation attempt has been recorded.", Toast.LENGTH_SHORT).show();
                    loadAttempts(); // refresh attempts list
                } else {
                    Toast.makeText(this, "Failed to save attempt.", Toast.LENGTH_SHORT).show();
                }

                // start speaking
                tts.speak(dictationText, TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_ID");
                heartbeat.startSpeaking();

            } else {
                tts.speak("No dictation available.", TextToSpeech.QUEUE_FLUSH, null, "UTTERANCE_ID");
            }
        });

        // handle when TTS finishes
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {}

            @Override
            public void onDone(String utteranceId) {
                runOnUiThread(() -> {
                    heartbeat.stopSpeaking();
                    showResultDialog(); // 👉 show dialog after speaking
                });
            }

            @Override
            public void onError(String utteranceId) {}
        });

        dbHelper = new MyDatabaseHelper(this);
        listAttempts = findViewById(R.id.listAttempts);

        // setup adapter
        attemptsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attemptsList);
        listAttempts.setAdapter(attemptsAdapter);

        loadAttempts(); // load history at start

    }

    private void loadAttempts() {
        attemptsList.clear();

        Cursor cursor = dbHelper.getAttemptsByLessonAndGroup(lesson_number, assessment_list_group_name);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int lessonNum = cursor.getInt(cursor.getColumnIndexOrThrow("dictation_lesson_number"));
                int groupNum = cursor.getInt(cursor.getColumnIndexOrThrow("dictation_lesson_group_number"));
                int dictation_attempt = cursor.getInt(cursor.getColumnIndexOrThrow("dictation_attempt"));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("datetime"));

                String record = "Attempt " + dictation_attempt + " | " + timestamp;
                attemptsList.add(record);
            } while (cursor.moveToNext());
            cursor.close();
        }

        attemptsAdapter.notifyDataSetChanged();
    }



    // dialog method
    private void showResultDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Dictation Finished")
                .setMessage("Would you like to retry or go back?")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> {
                    // enable button again for retry
                    btnSpeak.setEnabled(true);
                })
                .setNegativeButton("Back", (dialog, which) -> {
                    Intent intent = new Intent(DictationActivity.this, DictationList.class);
                    if (lesson_number >= 1 && lesson_number <= 23){
                        intent.putExtra("lesson_type", "Short");
                    } else if (lesson_number >= 24 && lesson_number <= 45){
                        intent.putExtra("lesson_type", "Advance");
                    }
                    startActivity(intent);
                })
                .show();
    }

    private AssessmentItem getAssessmentItem(int lessonNum, int groupNum) {
        for (AssessmentItem item : assessmentList) {
            if (item.getLessonNumber() == lessonNum && item.getItemNumber() == groupNum) {
                return item;
            }
        }
        return null;
    }

    public void DictationListGenerator() {
        assessmentList = new ArrayList<>();
        // Lesson 1 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                1,
                1,
                R.drawable.dictionary_asset,
                "Hello World!",
                1
        ));
        assessmentList.add(new AssessmentItem(
                1,
                2,
                R.drawable.lesson2_assessment_group_a,
                "1 Ray Stone may phone me at my home.  2 Lee may fail writing. 3 My whole right side is sore. 4 My train leaves at seven; Dale Reeve's train leaves later. 5 Steven may buy a mail meter.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                1,
                3,
                R.drawable.lesson2_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 2 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                2,
                1,
                R.drawable.lesson2_assessment_group_a,
                "1 Ray Stone may phone me at my home.  2 Lee may fail writing. 3 My whole right side is sore. 4 My train leaves at seven; Dale Reeve's train leaves later. 5 Steven may buy a mail meter.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                2,
                2,
                R.drawable.lesson2_assessment_group_b,
                "6 Lee Reed stayed home last evening. 7 He made me a $5 loan. 8 My reading rate is low. Is Dave's reading rate high? 9 Ray may rely on me. 10 My final rating is 80.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                2,
                3,
                R.drawable.lesson2_assessment_group_c,
                "11 I hear he might fly to Rome in a day or so. 12 I need more light in my retail store on Vail Drive. 13 Ray is leaving home; Lee is remaining here. 14 My writing style is fair. 15 Dale wrote a fine story. 16 He drove the whole night. 17 The freight train is late.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                2,
                4,
                R.drawable.lesson2_assessment_group_d,
                "18 Is it snowing or raining or hailing? 19 Is my trian late? 20 He notified me he may fly home later. 21 I may sign my lease. 22 Dale may fly home later.",
                1
        ));
        assessmentList.add(new AssessmentItem(
                2,
                5,
                R.drawable.lesson2_assessment_group_e,
                "23 Dave is feeling fine. 24 Fay dyed her hair; Mary might dye her hair too. 25 He notified me my radio was stolen.",
                1
        ));
        // Lesson 3 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                3,
                1,
                R.drawable.lesson2_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 4 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                4,
                1,
                R.drawable.lesson2_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 5 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                5,
                1,
                R.drawable.lesson5_assessment_group_a,
                "Mrs. Sweet: Your letter of April 10 to Mr. Keith Booth gives the facts relating to his clothing bill, but I am afraid that your letter is a little too terse. It will hurt his pride. \n" +
                        "Can you rewrite your letter so that the tone is not so severe? Remember, we have to keep Mr. Booth happy with our service.\n" +
                        " Please mail me a carbon of the rewrite that you prepare. Ethel Parks\n",
                1
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
                1
        ));
        assessmentList.add(new AssessmentItem(
                5,
                3,
                R.drawable.lesson5_assessment_group_c,
                "Dear Neighbor: We are happy to write you that the new Weaver cars have arrived. The new cars are well built as well as attractive.\n" +
                        " You can buy the car you like or you can lease it. If you buy it, we will help you finance it. But if you prefer to lease it, we will prepare a lease that will appeal to you. \n" +
                        "See these fine cars during your noon hour or in the evening. We are open till eight. Your Weaver Car Dealer\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                5,
                4,
                R.drawable.lesson5_assessment_group_d,
                "Dear Edith: Harry Smith wrote me that in April you addressed a meeting of our dealers in Dallas. He said that you spoke with the help of notes but that you spoke like a veteran. \n" +
                        "I am indeed happy that you did so well. I plan to ask you to address the new members of our sales staff the last week of May. Are you free the last week of May? J. C. Farmer\n",
                1
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
                1
        ));
        assessmentList.add(new AssessmentItem(
                5,
                6,
                R.drawable.lesson5_assessment_group_f,
                "Dear Ted: As you may know, my clerk, Bill Smith, will celebrate his twentieth birthday the last week of May. We plan to give Bill a pair of theater tickets as a surprise, but we need a little help that I have a feeling you can supply. We do not know the plays that Bill has seen. I know that with your tact, you can get me a list of four or five plays that Bill has not seen.\n" +
                        "I know you will help me surprise Bill Willis\n",
                1
        ));
        // Lesson 6 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                6,
                1,
                R.drawable.lesson5_assessment_group_f,
                "Hi!",
                1
        ));
        // Lesson 7 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                7,
                1,
                R.drawable.lesson5_assessment_group_f,
                "Hi!",
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
                1
        ));
        assessmentList.add(new AssessmentItem(
                8,
                2,
                R.drawable.lesson8_assessment_group_b,
                "Mr. Parks: I do not relish writing you this letter because I have to tell you that we are increasing the price of our letter pads slightly. I believe you know that our costs have increased greatly. In March they increased by nearly 5 percent. In April they increased by nearly 8 percent. We have absorbed these increased costs, but we cannot go on absorbing them. I am, therefore, with a good deal of regret, making a price increase, but only a small increase of 8 percent. \n" +
                        "Beginning July 15, the following price changes will be in effect: Style 153 pads will be $80 a gross. Style 20 pads will be $90.50 a gross. Style 26 pads will be $98 a gross.\n" +
                        "\tI sincerely hope. Mr. Parks, that this price increase will not affect your sales adversely. James R. Baker\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                8,
                3,
                R.drawable.lesson8_assessment_group_c,
                " Mrs. Charles: Last night at 6 o'clock I met for an hour with Jack Sweet urging him earnestly not to leave his job as head of our mailing room. I even promised to give him an increase of 20 percent, which would raise his salary to $15,000, but he would not accept it. He will leave on July 18.\n" +
                        " I am sincerely sorry to lose Jack because he did a good job operating the mailing room. As you well know, good people are not easy to get these days. Mary Farmer\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                8,
                4,
                R.drawable.lesson8_assessment_group_d,
                " Dear Madam: Early in March 1 wrote you to the effect that the trees in back of your home need a good spraying because the insects will start to attack them late in May or early in June. I have not had an answer to my letter. Did it reach you?\n" +
                        " If you would like my staff to take care of spraying your trees, please call me collect at (300) 555-1171. The cost of spraying your trees properly with a new type of spray that will not harm animals or trees will be only $150. Yours very truly\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                8,
                5,
                R.drawable.lesson8_assessment_group_e,
                " Dear Fred: There is a fairly good chance that I will be asked to talk to our 100 office-machines sales trainees at their meeting at the Hotel White in Dallas on June 15.\n" +
                        " As the meeting will close well before 3 o'clock, 2 there will still be at least four hours of daylight in which we can play 18 holes of golf.\n" +
                        " Will you be free on June 15? It would give me great pleasure to treat you to a fine steak following our golf game. \n" +
                        "I sincerely hope you will be free. Sincerely yours\n",
                1
        ));
        // Lesson 9 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                9,
                1,
                R.drawable.lesson5_assessment_group_f,
                "Hi!",
                1
        ));
        // Lesson 10 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                10,
                1,
                R.drawable.lesson5_assessment_group_f,
                "Hi!",
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
                1
        ));
        assessmentList.add(new AssessmentItem(
                11,
                2,
                R.drawable.lesson11_assessment_group_b,
                "give a good talk entitled \"Children and Their Problems. \"After she finished. I asked her if we could print her talk in our magazine, Child Care. She offered to send me two copies. When I get them from her, should I send them to you, or should I send them to Mr. Sweet at his office address? \n" +
                        "I told Mrs. Fields that if all goes well, we should be able to print her talk in our July issue. Can we do this? Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                11,
                3,
                R.drawable.lesson11_assessment_group_c,
                " Dear Friend: If you have been trying to find the offices of Coastal Airlines at 49th Street and Garden Road but have not been able to locate them, here is the answer to the problem: We have moved from our old location to a new building at 415 Third Street, which is across the street from the National Insurance Building.\n" +
                        " Our new offices are bigger, and we have hired more people who will be able to help you plan all your trips. \n" +
                        "When you again have occasion to fly to a city that we serve, visit our Third Street offices and let our people take care of your needs. They are eager to help. Yours very truly\n",
                1
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
                1
        ));
        assessmentList.add(new AssessmentItem(
                11,
                5,
                R.drawable.lesson11_assessment_group_e,
                " Dear Madam: As you know, on April 15 you bought two handbags from our ladies' store on Baker Street and charged them.\n" +
                        " You were billed for these handbags, and our bill should have been paid by June 1. \n" +
                        "Mr. James, a member of our billing section, has called you on the phone on three occasions, but he has not been able to reach you. We are, therefore, writing to ask you to send your check for $40 to pay for these bags. \n" +
                        "Won't you please take care of this matter. Yours very truly\n",
                1
        ));
        // Lesson 12 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                12,
                1,
                R.drawable.lesson11_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 13 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                13,
                1,
                R.drawable.lesson11_assessment_group_a,
                "Hi!",
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
                1
        ));
        assessmentList.add(new AssessmentItem(
                14,
                2,
                R.drawable.lesson14_assessment_group_b,
                "Mr. Baldwin: Today I was invited to address a meeting of the National School Book Editors on March 20, and I quickly accepted. Their meeting will be held in the Twin Cities Motel. I have selected the topic \"The Study Habits of Teenage Children.\" The details of the meeting are listed in the enclosed folder. \n" +
                        "This means that I will not be able to chair our weekly production meeting. Will you be free on March 204 so that you could fill in for me?\n" +
                        " If you are \n" +
                        "not free, we may have to cancel the production meeting. Gwen Sweet\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                14,
                3,
                R.drawable.lesson14_assessment_group_c,
                "Dear Professor Dwight: You will recall that on June 22 vou asked me to quote you a price on the building¹ of a study on the east side of your dwelling at 600 Park Street. After I visited your premises.  I quoted you a price of $5,000 if you would authorize us to begin work by July 15, but I have not had an answer to my offer. \n" +
                        "As you well know, inflation is taking its toll, and if I cannot get started by July 15, I will have to withdraw the price I quoted you and increase it by 105 percent. \n" +
                        "Why not call me today, Professor Dwight, and authorize me to pro-ceed with the building of your study. Very truly yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                14,
                4,
                R.drawable.lesson14_assessment_group_d,
                "Dear Friend: If you can give a positive answer to the following queries, you qualify for a car loan at the National Credit Corporation:\n" +
                        " 1. Are you 18 or older? \n" +
                        "2. Do you have a steady job at which you earn at least $90 a week? If so, we are ready to finance a new car for you when you need it. If you prefer, we will hold your loan for three weeks. Meanwhile, you can look for just the car that meets your needs. \n" +
                        "Send for our booklet listing all our loan plans. Better still, stop in at the National Credit Corporation today. There is no red tape involved in arranging a loan. It is quick and easy. Sincerely yours\n",
                1
        ));
        assessmentList.add(new AssessmentItem(
                14,
                5,
                R.drawable.lesson14_assessment_group_e,
                "Mr. White: Yesterday I visited the Broadway Office Supplies Shop and selected two desk lamps for the office of Mr. Smith, our chief editor. He has needed these lamps for weeks. They should help him increase his efficiency. The lamps were on sale, and I was able to get both for only $85. \n" +
                        "The lamps have been shipped from the shop and should arrive at our office soon. \n" +
                        "The bill for $85 is attached. Grace Baldwin",
                1
        ));
        assessmentList.add(new AssessmentItem(
                14,
                6,
                R.drawable.lesson14_assessment_group_f,
                "Dear Madam: When you have a National air travel credit card, buying tickets on Coastal Airlines is quick and quite simple. All you need do is pick up your phone, call 555-8702, tell our efficient clerk the  city you plan to visit, and give him or her your credit card number. Your tickets will be mailed the same day. \n" +
                        "If you would like us to provide this credit card service to your officers, fill in and send us the enclosed card. We will take care of all the details. Very truly yours",
                1
        ));
        // Lesson 15 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                15,
                1,
                R.drawable.lesson14_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 16 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                16,
                1,
                R.drawable.lesson14_assessment_group_a,
                "Hi!",
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
                R.drawable.lesson14_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 19 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                19,
                1,
                R.drawable.lesson14_assessment_group_a,
                "Hi!",
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
                R.drawable.lesson14_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 22 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                22,
                1,
                R.drawable.lesson14_assessment_group_a,
                "Hi!",
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
                R.drawable.lesson23_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 25 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                25,
                1,
                R.drawable.lesson23_assessment_group_a,
                "Hi!",
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
                R.drawable.lesson26_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 28 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                28,
                1,
                R.drawable.lesson26_assessment_group_a,
                "Hi!",
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
                R.drawable.lesson29_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 31 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                30,
                1,
                R.drawable.lesson29_assessment_group_a,
                "Hi!",
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
                R.drawable.lesson32_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 34 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                34,
                1,
                R.drawable.lesson32_assessment_group_a,
                "Hi!",
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
                R.drawable.lesson35_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 37 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                37,
                1,
                R.drawable.lesson35_assessment_group_a,
                "Hi!",
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
                R.drawable.lesson38_assessment_group_a,
                "Dear Mr. Brooks: After almost a year of work, a special committee consisting of responsible business executives of our town completed the most objective transportation survey in our history. The survey cost the town government about $25,000.\n" +
                        "The transportation committee submitted a comprehensive memorandum offering four alternative solutions to our transportation needs, together with supporting statistics and photographs. We think the time has definitely arrived when we must decide what the character of our transportation system should be in the future.\n" +
                        "On Saturday morning, November 18, we will hold an important public meeting in the faculty lounge of the Fifth Street School to discuss the survey. We hope you will find it convenient to attend. Sincerely yours",
                1
        ));
        // Lesson 40 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                40,
                1,
                R.drawable.lesson38_assessment_group_a,
                "Dear Mr. Garcia: At the meeting of the transportation committee of Westport on November 26, Dr. James Swift will present an interesting, entertaining, and enlightening slide report on the transportation needs of our town. Dr. Swift actually performed all the significant statistical research work involved in this report.\n" +
                        "If you have not heard Dr. Swift speak before, you are in for a special treat. He is recognized throughout the world as an acknowledged leader in the field of transportation. Immediately following Dr. Swift's report, everyone will have an opportunity to ask pertinent questions, to voice opinions, and to suggest new ideas.\n" +
                        "As you are a recognized authority in the transportation area, we hope you will attend and give us the benefit of your experience. Sincerely yours",
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
                41,
                1,
                R.drawable.lesson41_assessment_group_a,
                "Hi!",
                1
        ));
        // Lesson 43 -----------------------------------------------------------------------------
        assessmentList.add(new AssessmentItem(
                43,
                1,
                R.drawable.lesson41_assessment_group_a,
                "Hi!",
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
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}