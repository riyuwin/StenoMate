package com.example.stenomate.Lessons;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stenomate.LessonMenu;
import com.example.stenomate.R;

public class Lesson11Activity extends AppCompatActivity {

    ImageView BackIcon;
    TextView ContentText;

    ImageView[] stenoImageHolders = new ImageView[61];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson11);


        for (int i = 0; i < 61; i++) {
            int resId = getResources().getIdentifier("stenoImageHolder" + (i + 1), "id", getPackageName());
            stenoImageHolders[i] = findViewById(resId);

            if (stenoImageHolders[i] == null) {
                Log.e("", "Missing ImageView: stenoImageHolder" + (i + 1));
            } else {
                int finalI = i;
                stenoImageHolders[i].setOnClickListener(v -> showFullScreenImage(stenoImageHolders[finalI]));
            }
        }

        TextView ContentText = findViewById(R.id.contentText);
        String formattedText = "<b>1    Brief Forms</b> Here are nine more brief forms for very common words. ";
        ContentText.setText(Html.fromHtml(formattedText));

        TextView ContentText1 = findViewById(R.id.contentText1);
        String formattedText1 = "<b>2    Rd</b> The combination rd is represented by writing the r with an upward turn at the finish ";
        ContentText1.setText(Html.fromHtml(formattedText1));

        TextView ContentText2 = findViewById(R.id.contentText2);
        String formattedText2 = "<b>3    Ld</b> The combination ld is represented by writing the l with an upward turn at the finish.";
        ContentText2.setText(Html.fromHtml(formattedText2));

        TextView ContentText3 = findViewById(R.id.contentText3);
        String formattedText3 = "<b>4    Been in Phrases</b> The word been is represented by b after have, has, had.";
        ContentText3.setText(Html.fromHtml(formattedText3));

        TextView ContentText4 = findViewById(R.id.contentText4);
        String formattedText4 = "<b>5    Able in Phrases </b> The word able is represented by a after be or been.";
        ContentText4.setText(Html.fromHtml(formattedText4));

        Intent get_intent = getIntent();
        String lesson_type = get_intent.getStringExtra("lesson_type");

        BackIcon = findViewById(R.id.backIcon);
        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson11Activity.this, LessonMenu.class);
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