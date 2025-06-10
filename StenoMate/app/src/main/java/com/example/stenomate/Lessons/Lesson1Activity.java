package com.example.stenomate.Lessons;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stenomate.ContentActivity;
import com.example.stenomate.R;

public class Lesson1Activity extends AppCompatActivity {

    TextView ContentText;

    ImageView[] stenoImageHolders = new ImageView[54];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson1);

        for (int i = 0; i < 54; i++) {
            int resId = getResources().getIdentifier("stenoImageHolder" + (i + 1), "id", getPackageName());
            stenoImageHolders[i] = findViewById(resId);

            int finalI = i; // required for lambda to access
            stenoImageHolders[i].setOnClickListener(v -> showFullScreenImage(stenoImageHolders[finalI]));
        }

        ContentText = findViewById(R.id.contentText);
        String formattedText = "<b>S-Z</b> Perhaps the most frequently used consonant in the English language is s, partly because of the great many plurals that end with s. The shorthand s is a tiny downward curve that resembles the longhand comma in shape.";
        ContentText.setText(Html.fromHtml(formattedText));

    }

    private void showFullScreenImage(ImageView imageView) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_fullscreen_image);

        ImageView fullImage = dialog.findViewById(R.id.fullscreenImage);
        fullImage.setImageDrawable(imageView.getDrawable());

        fullImage.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

}