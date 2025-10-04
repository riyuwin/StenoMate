package com.example.stenomate.Lessons;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stenomate.LessonMenu;
import com.example.stenomate.R;

public class Lesson8Activity extends AppCompatActivity {

    ImageView BackIcon;
    ImageView[] stenoImageHolders = new ImageView[33];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson8);

        for (int i = 0; i < 33; i++) {
            int resId = getResources().getIdentifier("stenoImageHolder" + (i + 1), "id", getPackageName());
            stenoImageHolders[i] = findViewById(resId);

            if (stenoImageHolders[i] == null) {
                Log.e("Lesson8Activity", "Missing ImageView: stenoImageHolder" + (i + 1));
            } else {
                int finalI = i;
                stenoImageHolders[i].setOnClickListener(v -> showFullScreenImage(stenoImageHolders[finalI]));
            }
        }

        TextView ContentText = findViewById(R.id.contentText);
        String formattedText = "<b>1   Brief Forms </b>    Here is another group of nine brief forms for very common words. Learn them well!";
        ContentText.setText(Html.fromHtml(formattedText));

        TextView ContentText1 = findViewById(R.id.contentText1);
        String formattedText1 = "<b>2    Words Ending</b> -ly   The common word ending -ly is represented by the e circle. ";
        ContentText1.setText(Html.fromHtml(formattedText1));

        TextView ContentText3 = findViewById(R.id.contentText3);
        String formattedText3 = "<b>3    Amounts and Quantities</b>    When you take dictation in the business office, you will frequently have occasion to write amounts and quantities. Here are some devices that will enable you to write them rapidly.";
        ContentText3.setText(Html.fromHtml(formattedText3));

        Intent get_intent = getIntent();
        String lesson_type = get_intent.getStringExtra("lesson_type");

        BackIcon = findViewById(R.id.backIcon);
        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson8Activity.this, LessonMenu.class);
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