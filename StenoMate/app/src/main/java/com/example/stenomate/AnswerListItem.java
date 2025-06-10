package com.example.stenomate;

public class AnswerListItem {

    public int imageResId;
    public int LessonNumber;
    public int QuestionNumber;
    public String AnswerKey;
    public String ChoiceA;
    public String ChoiceB;
    public String ChoiceC;
    public String ChoiceD;

    public AnswerListItem(int imageResId, int LessonNumber, int QuestionNumber, String AnswerKey, String ChoiceA, String ChoiceB, String ChoiceC, String ChoiceD) {
        this.imageResId = imageResId;
        this.LessonNumber = LessonNumber;
        this.QuestionNumber = QuestionNumber;
        this.AnswerKey = AnswerKey;
        this.ChoiceA = ChoiceA;
        this.ChoiceB = ChoiceB;
        this.ChoiceC = ChoiceC;
        this.ChoiceD = ChoiceD;

    }

    public int getLessonNumber() {
        return LessonNumber;
    }

    public void setLessonNumber(int lessonNumber) {
        LessonNumber = lessonNumber;
    }

    public int getQuestionNumber() {
        return QuestionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        QuestionNumber = questionNumber;
    }

    public String getAnswerKey() {
        return AnswerKey;
    }

    public void setAnswerKey(String answerKey) {
        AnswerKey = answerKey;
    }
    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getChoiceA() {
        return ChoiceA;
    }

    public void setChoiceA(String choiceA) {
        ChoiceA = choiceA;
    }

    public String getChoiceB() {
        return ChoiceB;
    }

    public void setChoiceB(String choiceB) {
        ChoiceB = choiceB;
    }

    public String getChoiceC() {
        return ChoiceC;
    }

    public void setChoiceC(String choiceC) {
        ChoiceC = choiceC;
    }

    public String getChoiceD() {
        return ChoiceD;
    }

    public void setChoiceD(String choiceD) {
        ChoiceD = choiceD;
    }
}
