package com.example.stenomate;

public class AssessmentItem {
    private int imageResId;
    private String answerKey;
    private float timerMinutes; // changed from int → float
    private int lessonNumber;
    private int itemNumber;

    public AssessmentItem(int lessonNumber, int itemNumber, int imageResId, String answerKey, float timerMinutes) {
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

    public float getTimerMinutes() {
        return timerMinutes;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public int getItemNumber() {
        return itemNumber;
    }
}
