package com.example.stenomate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DictionaryActivity extends AppCompatActivity {

    EditText SearchEditTextId;

    List<DictionaryListItem> itemList;

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> searchAdapter;
    List<String> searchWords; // for index 1 only
    ImageView BackIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        BackIcon = findViewById(R.id.backIcon);

        SearchEditTextId = findViewById(R.id.searchEditTextId);


        PopulateDictionary();

        // Para sa search

        // existing findViewById
        autoCompleteTextView = findViewById(R.id.searchEditTextId);

        // Populate Dictionary
        PopulateDictionary();
        OnSearchItems();



        ListView listView = findViewById(R.id.dictionaryListView);
        List<DictionaryListItem> itemList = new ArrayList<>();
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_34_aid, "aid", "a-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_38_day, "day", "d-a"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_39_deed, "deed", "d-e-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_54_easy, "easy", "e-s-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_16_ease, "ease", "e-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_28_eat, "eat", "e-t"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_46_question_mark, "Dave", ""));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_35_feed, "feed", "f-e-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_52_fee, "fee", "f-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_53_fees, "fees", "f-e-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_48_hyphen, "Mae", ""));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_25_mean, "mean", "m-e-n"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_29_meet, "meet", "m-e-t"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_37_made, "made", "m-a-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_24_may, "may", "m-a"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_31_neat, "neat", "n-e-t"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_40_need, "need", "n-e-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_23_knee, "knee", "n-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_44_paragraph, "paragraph", ""));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_45_parentheses, "parentheses", ""));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_43_period, "period", ""));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_19_say, "say", "s-a"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_3_say, "say", "s-a"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_30_stay, "stay", "s-t-a"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_36_stayed, "stayed", "s-t-a-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_22_sane, "sane", "s-a-n"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_5_safe, "safe", "s-a-f"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_7_safes, "safes", "s-a-f-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_8_save, "save", "s-a-v"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_10_saves, "saves", "s-a-v-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_42_saved, "saved", "s-a-v-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_14_see, "see", "s-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_15_sees, "sees", "s-e-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_26_seem, "seem", "s-e-m"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_21_seen, "seen", "s-e-n"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_33_safety, "safety", "s-a-f-t-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_6_face, "face", "f-a-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_20_vain, "vain", "v-a-n"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_9_vase, "vase", "v-a-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_32_tea, "tea", "t-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_41_date, "date", "d-a-t"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_47_dash, "Fay", ""));


        CustomDictionaryAdapter adapter = new CustomDictionaryAdapter(this, itemList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent1, view, position, id) -> {
            Intent intent = new Intent(DictionaryActivity.this, ContentActivity.class);
            intent.putExtra("item_image", itemList.get(position).imageResId);
            intent.putExtra("item_title", itemList.get(position).Title); // optionally send title or content
            intent.putExtra("item_content", itemList.get(position).Content);
            startActivity(intent);
        });

        BackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DictionaryActivity.this, MainMenu.class);
            startActivity(intent);
        });

    }

    public void OnSearchItems(){
        // Extract index 1 values from itemList
        searchWords = new ArrayList<>();
        for (DictionaryListItem item : itemList) {
            searchWords.add(item.getTitle()); // index 1 lang
        }

        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, searchWords);
        autoCompleteTextView.setAdapter(searchAdapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedWord = (String) parent.getItemAtPosition(position);

            // Hanapin ang buong DictionaryListItem gamit ang selectedWord
            for (DictionaryListItem item : itemList) {
                if (item.getTitle().equalsIgnoreCase(selectedWord)) {
                    Intent intent = new Intent(DictionaryActivity.this, ContentActivity.class);
                    intent.putExtra("item_image", item.getImageResId());
                    intent.putExtra("item_title", item.getTitle());
                    intent.putExtra("item_content", item.getContent());
                    startActivity(intent);
                    break;
                }
            }
        });
    }

    public void PopulateDictionary(){
        itemList = new ArrayList<>();
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_34_aid, "aid", "a-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_38_day, "day", "d-a"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_39_deed, "deed", "d-e-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_54_easy, "easy", "e-s-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_16_ease, "ease", "e-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_28_eat, "eat", "e-t"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_46_question_mark, "Dave", ""));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_35_feed, "feed", "f-e-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_52_fee, "fee", "f-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_53_fees, "fees", "f-e-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_48_hyphen, "Mae", ""));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_25_mean, "mean", "m-e-n"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_29_meet, "meet", "m-e-t"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_37_made, "made", "m-a-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_24_may, "may", "m-a"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_31_neat, "neat", "n-e-t"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_40_need, "need", "n-e-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_23_knee, "knee", "n-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_44_paragraph, "paragraph", ""));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_45_parentheses, "parentheses", ""));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_43_period, "period", ""));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_19_say, "say", "s-a"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_3_say, "say", "s-a"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_30_stay, "stay", "s-t-a"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_36_stayed, "stayed", "s-t-a-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_22_sane, "sane", "s-a-n"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_5_safe, "safe", "s-a-f"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_7_safes, "safes", "s-a-f-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_8_save, "save", "s-a-v"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_10_saves, "saves", "s-a-v-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_42_saved, "saved", "s-a-v-d"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_14_see, "see", "s-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_15_sees, "sees", "s-e-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_26_seem, "seem", "s-e-m"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_21_seen, "seen", "s-e-n"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_33_safety, "safety", "s-a-f-t-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_6_face, "face", "f-a-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_20_vain, "vain", "v-a-n"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_9_vase, "vase", "v-a-s"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_32_tea, "tea", "t-e"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_41_date, "date", "d-a-t"));
        itemList.add(new DictionaryListItem(R.drawable.lesson1_asset_47_dash, "Fay", ""));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DictionaryActivity.this, MainMenu.class);
        startActivity(intent);
    }
}