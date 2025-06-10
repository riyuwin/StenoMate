package com.example.stenomate;

public class DictionaryListItem {
    public int imageResId;

    public String Title;
    public String Content;

    public DictionaryListItem(int imageResId, String Title, String Content) {
        this.imageResId = imageResId;
        this.Title = Title;
        this.Content = Content;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return Title;
    }

    public String getContent() {
        return Content;
    }

}
