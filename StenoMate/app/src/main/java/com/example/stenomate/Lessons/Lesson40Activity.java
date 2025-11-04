package com.example.stenomate.Lessons;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stenomate.LessonMenu;
import com.example.stenomate.R;

public class Lesson40Activity extends AppCompatActivity {

    ImageView BackIcon;
    TextView ContentText;

    ImageView[] stenoImageHolders = new ImageView[61];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson40);


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

        /*ContentText = findViewById(R.id.contentText);
        String formattedText = "<b>S-Z</b> Perhaps the most frequently used consonant in the English language is s, partly because of the great many plurals that end with s. The shorthand s is a tiny downward curve that resembles the longhand comma in shape.";
        ContentText.setText(Html.fromHtml(formattedText));*/


        Intent get_intent = getIntent();
        String lesson_type = get_intent.getStringExtra("lesson_type");

        BackIcon = findViewById(R.id.backIcon);
        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson40Activity.this, LessonMenu.class);
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