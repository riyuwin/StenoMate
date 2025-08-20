package com.example.stenomate;

public class AssessmentItem {
    private int imageResId;
    private String answerKey;
    private int timerMinutes;
    private int lessonNumber;
    private int itemNumber;

    public AssessmentItem(int lessonNumber, int itemNumber, int imageResId, String answerKey, int timerMinutes) {
        this.lessonNumber = lessonNumber;
        this.itemNumber = itemNumber;
        this.imageResId = imageResId;
        this.answerKey = answerKey;
        this.timerMinutes = timerMinutes;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getAnswerKey() {
        return answerKey;
    }

    public int getTimerMinutes() {
        return timerMinutes;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public int getItemNumber() {
        return itemNumber;
    }
}
