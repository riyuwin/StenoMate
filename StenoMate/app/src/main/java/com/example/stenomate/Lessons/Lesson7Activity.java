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

public class Lesson7Activity extends AppCompatActivity {

    ImageView BackIcon;

    ImageView[] stenoImageHolders = new ImageView[35];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson7);

        for (int i = 0; i < 35; i++) {
            int resId = getResources().getIdentifier("stenoImageHolder" + (i + 1), "id", getPackageName());
            stenoImageHolders[i] = findViewById(resId);

            if (stenoImageHolders[i] == null) {
                Log.e("Lesson7Activity", "Missing ImageView: stenoImageHolder" + (i + 1));
            } else {
                int finalI = i;
                stenoImageHolders[i].setOnClickListener(v -> showFullScreenImage(stenoImageHolders[finalI]));
            }
        }

        TextView ContentText = findViewById(R.id.contentText);
        String formattedText = "<b>1 Sh, Ch, J</b> These three sounds are represented by downward straight lines.";
        ContentText.setText(Html.fromHtml(formattedText));

        TextView ContentText1 = findViewById(R.id.contentText1);
        String formattedText1 = "<b>Sh</b> The shorthand stroke for sh (called \"ish\") is a very short downward straight stroke.";
        ContentText1.setText(Html.fromHtml(formattedText1));

        TextView ContentText2 = findViewById(R.id.contentText2);
        String formattedText2 = "<b>Ch</b> The shorthand stroke for ch (called \"chay\") is a somewhat longer straight downward stroke approximately one-half the height of the space between the lines in your shorthand notebook.";
        ContentText2.setText(Html.fromHtml(formattedText2));

        TextView ContentText3 = findViewById(R.id.contentText3);
        String formattedText3 = "<b>J</b> The shorthand stroke for the sound of j, as in age and jury, is a long downward straight stroke almost the full height of the space between the lines in your shorthand notebook.";
        ContentText3.setText(Html.fromHtml(formattedText3));

        TextView ContentText4 = findViewById(R.id.contentText4);
        String formattedText4 = "2 <b>O, Aw </b> The small deep hook that represents the sound of o, as in no, also represents the vowel sounds heard in hot and all.";
        ContentText4.setText(Html.fromHtml(formattedText4));

        Intent get_intent = getIntent();
        String lesson_type = get_intent.getStringExtra("lesson_type");

        BackIcon = findViewById(R.id.backIcon);
        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson7Activity.this, LessonMenu.class);
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