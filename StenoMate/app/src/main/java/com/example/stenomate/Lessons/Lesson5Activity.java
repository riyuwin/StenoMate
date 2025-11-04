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

public class Lesson5Activity extends AppCompatActivity {

    ImageView BackIcon;

    ImageView[] stenoImageHolders = new ImageView[86];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson5);

        for (int i = 0; i < 86; i++) {
            int resId = getResources().getIdentifier("stenoImageHolder" + (i + 1), "id", getPackageName());
            stenoImageHolders[i] = findViewById(resId);

            int finalI = i; // required for lambda to access
            stenoImageHolders[i].setOnClickListener(v -> showFullScreenImage(stenoImageHolders[finalI]));
        }

        TextView ContentText = findViewById(R.id.contentText);
        String formattedText = "<b>1 Alphabet Review</b> In Lessons 1 through 4 you studied 20 shorthand strokes. See how fast you can read them.";
        ContentText.setText(Html.fromHtml(formattedText));

        TextView ContentText1 = findViewById(R.id.contentText1);
        String formattedText1 = "<b>2 A, A</b> The large circle that represents the long sound of a, as in main. also represents the ";
        ContentText1.setText(Html.fromHtml(formattedText1));

        TextView ContentText2 = findViewById(R.id.contentText2);
        String formattedText2 = "<b>3   E, I, Obscure Vowel</b> The tiny circle that represents the sound of e, as in heat, also represents the vowel sounds heard in let, him, and the obscure vowel sound (called \"schwa\" in some dictionaries) in her, hurt.";
        ContentText2.setText(Html.fromHtml(formattedText2));

        TextView ContentText3 = findViewById(R.id.contentText3);
        String formattedText3 = "<b>3   E, I, Obscure Vowel</b> The tiny circle that represents the sound of e, as in heat, also represents the vowel sounds heard in let, him, and the obscure vowel sound (called \"schwa\" in some dictionaries) in her, hurt.";
        ContentText3.setText(Html.fromHtml(formattedText3));

        TextView ContentText4 = findViewById(R.id.contentText4);
        String formattedText4 = "<b>5   Brief Forms    </b> Here is another group of brief forms for frequently used words. You will be wise to learn them well.";
        ContentText4.setText(Html.fromHtml(formattedText4));

        TextView ContentText5 = findViewById(R.id.contentText5);
        String formattedText5 = "<b>6   Common Phrases    </b> Here are some useful phrases employing these brief forms.";
        ContentText5.setText(Html.fromHtml(formattedText5));

        Intent get_intent = getIntent();
        String lesson_type = get_intent.getStringExtra("lesson_type");

        BackIcon = findViewById(R.id.backIcon);
        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson5Activity.this, LessonMenu.class);
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