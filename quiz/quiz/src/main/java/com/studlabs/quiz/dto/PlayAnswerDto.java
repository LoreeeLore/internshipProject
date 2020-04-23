package com.studlabs.quiz.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

@JsonRootName(value = "playanswer")
public class PlayAnswerDto {

    private int idPlayAnswer;
    private int idPlayQuestion;
    private int idAnswer;
    private String text;

    public PlayAnswerDto() {

    }

    public PlayAnswerDto(int id, int idPlayQuestion, int idAnswer, String text) {
        this.idPlayAnswer = id;
        this.idPlayQuestion = idPlayQuestion;
        this.idAnswer = idAnswer;
        this.text = text;
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
        PlayAnswerDto that = (PlayAnswerDto) o;
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
