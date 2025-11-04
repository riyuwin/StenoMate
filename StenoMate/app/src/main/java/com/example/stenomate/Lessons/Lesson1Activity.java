package com.example.stenomate.Lessons;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stenomate.AboutUs;
import com.example.stenomate.ContentActivity;
import com.example.stenomate.LessonMenu;
import com.example.stenomate.MainMenu;
import com.example.stenomate.R;

public class Lesson1Activity extends AppCompatActivity {

    ImageView BackIcon;

    ImageView[] stenoImageHolders = new ImageView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson1);

        for (int i = 1; i < 4; i++) {
            int resId = getResources().getIdentifier("stenoImageHolder" + (i + 1), "id", getPackageName());
            stenoImageHolders[i] = findViewById(resId);

            int finalI = i; // required for lambda to access
            stenoImageHolders[i].setOnClickListener(v -> showFullScreenImage(stenoImageHolders[finalI]));
        }

        TextView ContentText = findViewById(R.id.contentText);
        String formattedText = "<b>S-Z</b> Perhaps the most frequently used consonant in the English language is s, partly because of the great many plurals that end with s. The shorthand s is a tiny downward curve that resembles the longhand comma in shape.";
        ContentText.setText(Html.fromHtml(formattedText));

        TextView ContentText1 = findViewById(R.id.contentText1);
        String formattedText1 = "2 <b>A</b> A very important sound in the English language is a. In Gregg Shorthand a is simply the longhand a with the final connecting stroke omitted.";
        ContentText1.setText(Html.fromHtml(formattedText1));

        TextView ContentText2 = findViewById(R.id.contentText2);
        String formattedText2 = "3 <b>Silent Letters</b> In English, there are many words containing letters that are not pronounced. In Gregg Shorthand, these silent letters are omitted; only the sounds that you actually hear are written. Example: the word say would be written s-a; the y would not be written because it is not pronounced. The word face would be represented by the shorthand characters f-a-s; the e would be omitted because it is not pronounced, and the c would be represented by the shorthand s because it is pronounced s. By omitting silent letters, we save a great deal of writing time.";
        ContentText2.setText(Html.fromHtml(formattedText2));

        TextView ContentText3 = findViewById(R.id.contentText3);
        String formattedText3 = "4 <b>S-A</b> Word With the strokes for s and a, you can form the shorthand outline for the word say.";
        ContentText3.setText(Html.fromHtml(formattedText3));

        TextView ContentText4 = findViewById(R.id.contentText4);
        String formattedText4 = "5 <b>F, V</b> The next two shorthand strokes you will learn are f and v.";
        ContentText4.setText(Html.fromHtml(formattedText4));

        TextView ContentText5 = findViewById(R.id.contentText5);
        String formattedText5 = "<b>F</b> The shorthand stroke for f is a downward curve the same shape as s, but it is somewhat longer−approximately half the height of the space between the lines of your shorthand notebook.";
        ContentText5.setText(Html.fromHtml(formattedText5));

        TextView ContentText6 = findViewById(R.id.contentText6);
        String formattedText6 = "<b>V</b> The shorthand stroke for v is also a downward curve the same shape as s and f, but it is very large¬−approximately the full height of the space between the lines of your shorthand notebook.";
        ContentText6.setText(Html.fromHtml(formattedText6));

        TextView ContentText7 = findViewById(R.id.contentText7);
        String formattedText7 = "6 <b>E</b> Another very important vowel in the English language is e. In shorthand, e is represented by a tiny circle. It is simply the longhand e with the two connecting strokes omitted. The circle may be written in either direction.";
        ContentText7.setText(Html.fromHtml(formattedText7));

        TextView ContentText8 = findViewById(R.id.contentText8);
        String formattedText8 = "7 <b>N, M</b> The shorthand stroke for n is a very short forward straight line. The shorthand stroke for m is a longer forward straight line.";
        ContentText8.setText(Html.fromHtml(formattedText8));

        TextView ContentText9 = findViewById(R.id.contentText9);
        String formattedText9 = "8 <b>T, D</b> The shorthand stroke for t is a short upward straight line. The shorthand stroke for d is a longer upward straight line.";
        ContentText9.setText(Html.fromHtml(formattedText9));

        Intent get_intent = getIntent();
        String lesson_type = get_intent.getStringExtra("lesson_type");

        BackIcon = findViewById(R.id.backIcon);
        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson1Activity.this, LessonMenu.class);
            intent.putExtra("lesson_type", lesson_type);
            startActivity(intent);
        });
    }
    private void showFullScreenImage(ImageView imageView) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_fullscreen_image);

        // set background to white (or use any color resource)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);

        ImageView fullImage = dialog.findViewById(R.id.fullscreenImage);
        fullImage.setImageDrawable(imageView.getDrawable());

        fullImage.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


}