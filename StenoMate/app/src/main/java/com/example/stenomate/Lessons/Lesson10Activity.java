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

public class Lesson10Activity extends AppCompatActivity {

    ImageView BackIcon;
    ImageView[] stenoImageHolders = new ImageView[61];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson10);


        for (int i = 0; i < 61; i++) {
            int resId = getResources().getIdentifier("stenoImageHolder" + (i + 1), "id", getPackageName());
            stenoImageHolders[i] = findViewById(resId);

            if (stenoImageHolders[i] == null) {
                Log.e("Lesson10Activity", "Missing ImageView: stenoImageHolder" + (i + 1));
            } else {
                int finalI = i;
                stenoImageHolders[i].setOnClickListener(v -> showFullScreenImage(stenoImageHolders[finalI]));
            }
        }

        TextView ContentText = findViewById(R.id.contentText);
        String formattedText = "1<b>Nd</b> The shorthand strokes for nd are joined without an angle to form the nd blend, as in signed.";
        ContentText.setText(Html.fromHtml(formattedText));

        TextView ContentText1 = findViewById(R.id.contentText1);
        String formattedText1 = "2 <b>Nt </b> The stroke that represents nd also represents nt, as in sent.";
        ContentText1.setText(Html.fromHtml(formattedText1));

        TextView ContentText2 = findViewById(R.id.contentText2);
        String formattedText2 = "3 <b>Ses </b> The sound of ses, as in senses, is represented by joining the two forms of s.";
        ContentText2.setText(Html.fromHtml(formattedText2));

        TextView ContentText3 = findViewById(R.id.contentText3);
        String formattedText3 = "4 <b>Sis, Sus </b> The similar sounds of sis, as in sister, and sus, as in versus, are also represented by joining the two forms of s.";
        ContentText3.setText(Html.fromHtml(formattedText3));

        Intent get_intent = getIntent();
        String lesson_type = get_intent.getStringExtra("lesson_type");

        BackIcon = findViewById(R.id.backIcon);
        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Lesson10Activity.this, LessonMenu.class);
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