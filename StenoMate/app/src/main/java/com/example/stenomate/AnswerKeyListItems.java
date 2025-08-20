package com.example.stenomate;

public class AnswerKeyListItems {

    public int imageResId;
    public int LessonNumber;
    public int QuestionNumber;
    public String AnswerKey;


    public AnswerKeyListItems(int imageResId, int LessonNumber, int QuestionNumber, String AnswerKey) {
        this.imageResId = imageResId;
        this.LessonNumber = LessonNumber;
        this.QuestionNumber = QuestionNumber;
        this.AnswerKey = AnswerKey;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getLessonNumber() {
        return LessonNumber;
    }

    public int getQuestionNumber() {
        return QuestionNumber;
    }

    public String getAnswerKey() {
        return AnswerKey;
    }

}
