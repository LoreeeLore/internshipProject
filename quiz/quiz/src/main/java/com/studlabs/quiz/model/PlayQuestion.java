package com.studlabs.quiz.model;

import java.time.*;
import java.util.*;

public class PlayQuestion {

    private int idPlayQuestion;
    private int idQuestion;
    private int idPlayQuiz;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isCorrect;

    public PlayQuestion() {
    }

    public PlayQuestion(int idQuestion, int idPlayQuiz) {
        this.idQuestion = idQuestion;
        this.idPlayQuiz = idPlayQuiz;
    }

    public PlayQuestion(int idQuestion, int idPlayQuiz, LocalDateTime startTime, LocalDateTime endTime, boolean isCorrect) {
        this.idQuestion = idQuestion;
        this.idPlayQuiz = idPlayQuiz;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCorrect = isCorrect;
    }

    public PlayQuestion(int idPlayQuestion, int idQuestion, int idPlayQuiz, LocalDateTime startTime, LocalDateTime endTime, boolean isCorrect) {
        this.idPlayQuestion = idPlayQuestion;
        this.idQuestion = idQuestion;
        this.idPlayQuiz = idPlayQuiz;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCorrect = isCorrect;
    }

    public int getIdPlayQuestion() {
        return idPlayQuestion;
    }

    public void setIdPlayQuestion(int idPlayQuestion) {
        this.idPlayQuestion = idPlayQuestion;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public int getIdPlayQuiz() {
        return idPlayQuiz;
    }

    public void setIdPlayQuiz(int idPlayQuiz) {
        this.idPlayQuiz = idPlayQuiz;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayQuestion that = (PlayQuestion) o;
        return idPlayQuestion == that.idPlayQuestion &&
                idQuestion == that.idQuestion &&
                idPlayQuiz == that.idPlayQuiz &&
                isCorrect == that.isCorrect &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlayQuestion, idQuestion, idPlayQuiz, startTime, endTime, isCorrect);
    }
}
