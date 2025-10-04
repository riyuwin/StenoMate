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

import com.example.stenomate.LessonMenu;
import com.example.stenomate.R;

public class Lesson4Activity extends AppCompatActivity {

    ImageView BackIcon;

    ImageView[] stenoImageHolders = new ImageView[64];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson4);

        for (int i = 0; i < 64; i++) {
            int resId = getResources().getIdentifier("stenoImageHolder" + (i + 1), "id", getPackageName());
            stenoImageHolders[i] = findViewById(resId);

            int finalI = i; // required for lambda to access
            stenoImageHolders[i].setOnClickListener(v -> showFullScreenImage(stenoImageHolders[finalI]));
        }

        TextView ContentText = findViewById(R.id.contentText);
        String formattedText = "1 <b>Alphabet Review</b> You have already studied 17 strokes in the Gregg Shorthand alphabet. How fast can you read them?";
        ContentText.setText(Html.fromHtml(formattedText));

        TextView ContentText1 = findViewById(R.id.contentText1);
        String formattedText1 = "2 <b>OO </b> The shorthand stroke for the sound of oo, as in to, is a tiny upward hook.";
        ContentText1.setText(Html.fromHtml(formattedText1));

        TextView ContentText2 = findViewById(R.id.contentText2);
        String formattedText2 = "3 <b>W,</b> Sw At the beginning of words w, as in we, is represented by the oo hook; sw, as in sweet, by s-oo.";
        ContentText2.setText(Html.fromHtml(formattedText2));

        TextView ContentText3 = findViewById(R.id.contentText3);
        String formattedText3 = "4 <b>Wh</b> Wh, as in why and while, is also represented by the oo hook.";
        ContentText3.setText(Html.fromHtml(formattedText3));

        TextView ContentText4 = findViewById(R.id.contentText4);
        String formattedText4 = "5 <b>Useful Phrases</b> Here are a number of useful phrases that use the oo hook.";
        ContentText4.setText(Html.fromHtml(formattedText4));

        Intent get_intent = getIntent();
        String lesson_type = get_intent.getStringExtra("lesson_type");

        BackIcon = findViewById(R.id.backIcon);
        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson4Activity.this, LessonMenu.class);
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