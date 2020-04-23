package com.studlabs.quiz.model;

import java.util.*;

public class Answer {

    private int idAnswer;
    private int idQuestion;
    private boolean isCorrect;
    private String answerText;

    public Answer() {
    }

    public Answer(int idQuestion, boolean isCorrect, String answerText) {
        this.idQuestion = idQuestion;
        this.isCorrect = isCorrect;
        this.answerText = answerText;
    }

    public Answer(int idAnswer, int idQuestion, boolean isCorrect, String answerText) {
        this(idQuestion, isCorrect, answerText);
        this.idAnswer = idAnswer;
    }

    public int getIdAnswer() {
        return idAnswer;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public String getText() {
        return answerText;
    }

    public void setIdAnswer(int idAnswer) {
        this.idAnswer = idAnswer;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public void setText(String text) {
        this.answerText = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer)) return false;
        Answer answer = (Answer) o;
        return idAnswer == answer.idAnswer &&
                idQuestion == answer.idQuestion &&
                isCorrect == answer.isCorrect &&
                Objects.equals(answerText, answer.answerText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAnswer, idQuestion, isCorrect, answerText);
    }
}
