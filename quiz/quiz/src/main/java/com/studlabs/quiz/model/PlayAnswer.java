package com.studlabs.quiz.model;

import java.util.*;

public class PlayAnswer {

    private int idPlayAnswer;
    private int idPlayQuestion;
    private int idAnswer;
    private String text;

    public PlayAnswer(int idPlayQuestion, int idAnswer, String text) {
        this.idPlayQuestion = idPlayQuestion;
        this.idAnswer = idAnswer;
        this.text = text;
    }

    public PlayAnswer(int idPlayAnswer, int idPlayQuestion, int idAnswer, String text) {
        this(idPlayQuestion, idAnswer, text);
        this.idPlayAnswer = idPlayAnswer;
        this.idPlayQuestion = idPlayQuestion;
        this.idAnswer = idAnswer;
        this.text = text;
    }

    public PlayAnswer(int idAnswer, String text) {
        this.idAnswer = idAnswer;
        this.text = text;
    }

    public PlayAnswer() {

    }

    public int getIdPlayAnswer() {
        return idPlayAnswer;
    }

    public void setIdPlayAnswer(int idPlayAnswer) {
        this.idPlayAnswer = idPlayAnswer;
    }

    public int getIdPlayQuestion() {
        return idPlayQuestion;
    }

    public void setIdPlayQuestion(int idPlayQuestion) {
        this.idPlayQuestion = idPlayQuestion;
    }

    public int getIdAnswer() {
        return idAnswer;
    }

    public void setIdAnswer(int idAnswer) {
        this.idAnswer = idAnswer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayAnswer that = (PlayAnswer) o;
        return idPlayAnswer == that.idPlayAnswer &&
                idPlayQuestion == that.idPlayQuestion &&
                idAnswer == that.idAnswer &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlayAnswer, idPlayQuestion, idAnswer, text);
    }
}
