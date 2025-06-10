package com.example.stenomate;

import android.content.Context;
import android.graphics.pdf.models.ListItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CustomDictionaryAdapter extends ArrayAdapter<DictionaryListItem> {

    private final Context context;
    private final List<DictionaryListItem> items;

    public CustomDictionaryAdapter(Context context, List<DictionaryListItem> items) {
        super(context, R.layout.dictionary_list_item, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.dictionary_list_item, parent, false);

        ImageView imageView = rowView.findViewById(R.id.imageView);
        TextView TitleText = rowView.findViewById(R.id.titleText);
        TextView ContentText = rowView.findViewById(R.id.contetntText);

        DictionaryListItem item = items.get(position);
        imageView.setImageResource(item.imageResId);
        TitleText.setText(item.Title);
        ContentText.setText(item.Content);

        return rowView;
    }
}
