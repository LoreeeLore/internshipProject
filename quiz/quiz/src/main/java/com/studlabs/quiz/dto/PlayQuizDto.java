package com.studlabs.quiz.dto;

import com.fasterxml.jackson.annotation.*;
import com.studlabs.quiz.model.*;
import org.springframework.format.annotation.*;

import java.time.*;

@JsonRootName(value = "PlayQuiz")
public class PlayQuizDto {

    private int idPlayQuiz;
    private int idQuiz;
    private String idUser;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalDateTime startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalDateTime endTime;
    private double rate;
    private PlayQuizStatus status;

    public PlayQuizDto() {
    }

    public int getIdPlayQuiz() {
        return idPlayQuiz;
    }

    public void setIdPlayQuiz(int idPlayQuiz) {
        this.idPlayQuiz = idPlayQuiz;
    }

    public int getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public PlayQuizStatus getStatus() {
        return status;
    }

    public void setStatus(PlayQuizStatus status) {
        this.status = status;
    }
}
