package com.example.stenomate;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ContentActivity extends AppCompatActivity {

    TextView TitleText, ContentText;
    ImageView ImageHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        int imageResId = getIntent().getIntExtra("item_image", R.drawable.stenomate_logo); // default fallback
        String title = getIntent().getStringExtra("item_title");
        String content = getIntent().getStringExtra("item_content");

        TitleText = findViewById(R.id.nameContentId);
        ContentText = findViewById(R.id.contentText);
        ImageHolder = findViewById(R.id.stenoImageHolder);

        TitleText.setText(title);
        ContentText.setText(content);
        ImageHolder.setImageResource(imageResId); // Set the image from intent

        ImageHolder.setOnClickListener(v -> {
            Dialog dialog = new Dialog(ContentActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_fullscreen_image);

            ImageView fullImage = dialog.findViewById(R.id.fullscreenImage);
            fullImage.setImageResource(imageResId); // Set same image in fullscreen

            fullImage.setOnClickListener(view -> dialog.dismiss()); // tap to close
            dialog.show();
        });
    }
}
