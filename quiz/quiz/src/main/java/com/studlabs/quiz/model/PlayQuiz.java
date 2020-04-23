package com.studlabs.quiz.model;

import java.time.*;
import java.util.*;

public class PlayQuiz {

    private int idPlayQuiz;
    private int idQuiz;
    private String idUser;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double rate;
    private PlayQuizStatus status;

    public PlayQuiz() {
    }

    public PlayQuiz(int idPlayQuiz, int idQuiz, String idUser, LocalDateTime startTime, LocalDateTime endTime, double rate, PlayQuizStatus status) {
        this.idPlayQuiz = idPlayQuiz;
        this.idQuiz = idQuiz;
        this.idUser = idUser;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rate = rate;
        this.status = status;
    }

    public PlayQuiz(int idQuiz, String idUser) {
        this.idQuiz = idQuiz;
        this.idUser = idUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayQuiz playQuiz = (PlayQuiz) o;
        return idPlayQuiz == playQuiz.idPlayQuiz &&
                idQuiz == playQuiz.idQuiz &&
                idUser == playQuiz.idUser &&
                Double.compare(playQuiz.rate, rate) == 0 &&
                Objects.equals(startTime, playQuiz.startTime) &&
                Objects.equals(endTime, playQuiz.endTime) &&
                status == playQuiz.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlayQuiz, idQuiz, idUser, startTime, endTime, rate, status);
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

    @Override
    public String toString() {
        return "PlayQuiz{" +
                "idPlayQuiz=" + idPlayQuiz +
                ", idQuiz=" + idQuiz +
                ", idUser='" + idUser + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", rate=" + rate +
                ", status=" + status +
                '}';
    }
}
