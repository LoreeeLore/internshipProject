package com.studlabs.quiz.dto;

import com.fasterxml.jackson.annotation.*;

import java.time.*;

@JsonRootName(value = "playQuestion")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayQuestionDto {

    private int idPlayQuestion;
    private int idQuestion;
    private int idPlayQuiz;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isCorrect;

    public PlayQuestionDto(int idPlayQuestion, int idQuestion, int idPlayQuiz, LocalDateTime startTime, LocalDateTime endTime, boolean isCorrect) {
        this.idPlayQuestion = idPlayQuestion;
        this.idQuestion = idQuestion;
        this.idPlayQuiz = idPlayQuiz;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCorrect = isCorrect;
    }

    public PlayQuestionDto() {
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
}
