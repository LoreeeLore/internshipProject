package com.studlabs.quiz.model;

import java.util.*;

public class QuizRating {

    private String idUser;
    private int idQuiz;
    private boolean isLike;

    public QuizRating() {
    }

    public QuizRating(String idUser, int idQuiz, boolean isLike) {
        this.idUser = idUser;
        this.idQuiz = idQuiz;
        this.isLike = isLike;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizRating)) return false;
        QuizRating that = (QuizRating) o;
        return idQuiz == that.idQuiz &&
                isLike == that.isLike &&
                Objects.equals(idUser, that.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, idQuiz, isLike);
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public int getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
}
