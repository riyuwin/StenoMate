package com.example.stenomate;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutUs extends AppCompatActivity {

    ImageView BackIcon;

    ImageView[] profileImageHolders = new ImageView[8];

    LinearLayout NicoleInfoContainer, NicoleOriginalLayout, KristianInfoContainer, KristianOriginalLayout, MiraInfoContainer, MiraOriginalLayout, CrisantoInfoContainer, CrisantoOriginalLayout;

    TextView NicoleSeeMoreBtn, NicoleLessInfoBtn, KristianSeeMoreBtn, KristianLessInfoBtn, MiraSeeMoreBtn, MiraLessInfoBtn, CrisantoSeeMoreBtn, CrisantoLessInfoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        NicoleInfoContainer = findViewById(R.id.nicoleInfoContainer);
        NicoleOriginalLayout = findViewById(R.id.nicoleOriginalLayout);
        NicoleSeeMoreBtn = findViewById(R.id.nicolSeeMoreBtn);
        NicoleLessInfoBtn = findViewById(R.id.nicolLessInfoBtn);

        KristianInfoContainer = findViewById(R.id.kristianInfoContainer);
        KristianOriginalLayout = findViewById(R.id.kristianOriginalLayout);
        KristianSeeMoreBtn = findViewById(R.id.kristianSeeMoreBtn);
        KristianLessInfoBtn = findViewById(R.id.kristianLessInfoBtn);

        MiraInfoContainer = findViewById(R.id.miraInfoContainer);
        MiraOriginalLayout = findViewById(R.id.miraOriginalLayout);
        MiraSeeMoreBtn = findViewById(R.id.miraSeeMoreBtn);
        MiraLessInfoBtn = findViewById(R.id.miraLessInfoBtn);


        CrisantoInfoContainer = findViewById(R.id.crisantoInfoContainer);
        CrisantoOriginalLayout = findViewById(R.id.crisantoOriginalLayout);
        CrisantoSeeMoreBtn = findViewById(R.id.crisantoSeeMoreBtn);
        CrisantoLessInfoBtn = findViewById(R.id.crisantoLessInfoBtn);

        BackIcon = findViewById(R.id.backIcon);

        for (int i = 0; i < 8; i++) {
            int resId = getResources().getIdentifier("profileImageHolders" + (i + 1), "id", getPackageName());
            profileImageHolders[i] = findViewById(resId);

            int finalI = i;
            profileImageHolders[i].setOnClickListener(v -> showFullScreenImage(profileImageHolders[finalI]));
        }

        CrisantoSeeMoreBtn.setOnClickListener(v -> {
            CrisantoInfoContainer.setVisibility(View.VISIBLE);
            CrisantoOriginalLayout.setVisibility(View.GONE);
        });

        CrisantoLessInfoBtn.setOnClickListener(v -> {
            CrisantoInfoContainer.setVisibility(View.GONE);
            CrisantoOriginalLayout.setVisibility(View.VISIBLE);
        });

        MiraSeeMoreBtn.setOnClickListener(v -> {
            MiraInfoContainer.setVisibility(View.VISIBLE);
            MiraOriginalLayout.setVisibility(View.GONE);
        });

        MiraLessInfoBtn.setOnClickListener(v -> {
            MiraInfoContainer.setVisibility(View.GONE);
            MiraOriginalLayout.setVisibility(View.VISIBLE);
        });

        KristianSeeMoreBtn.setOnClickListener(v -> {
            KristianInfoContainer.setVisibility(View.VISIBLE);
            KristianOriginalLayout.setVisibility(View.GONE);
        });

        KristianLessInfoBtn.setOnClickListener(v -> {
            KristianInfoContainer.setVisibility(View.GONE);
            KristianOriginalLayout.setVisibility(View.VISIBLE);
        });


        NicoleSeeMoreBtn.setOnClickListener(v -> {
            NicoleInfoContainer.setVisibility(View.VISIBLE);
            NicoleOriginalLayout.setVisibility(View.GONE);
        });

        NicoleLessInfoBtn.setOnClickListener(v -> {
            NicoleInfoContainer.setVisibility(View.GONE);
            NicoleOriginalLayout.setVisibility(View.VISIBLE);
        });

        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(AboutUs.this, MainMenu.class);
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AboutUs.this, MainMenu.class);
        startActivity(intent);
    }

}