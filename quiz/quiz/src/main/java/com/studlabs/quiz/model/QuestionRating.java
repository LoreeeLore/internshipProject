package com.studlabs.quiz.model;

import java.util.*;

public class QuestionRating {

    private String idUser;
    private int idQuestion;
    private boolean isLike;

    public QuestionRating(String idUser, int idQuestion, boolean isLike) {
        this.idUser = idUser;
        this.idQuestion = idQuestion;
        this.isLike = isLike;
    }

    public QuestionRating() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionRating)) return false;
        QuestionRating that = (QuestionRating) o;
        return idQuestion == that.idQuestion &&
                isLike == that.isLike &&
                Objects.equals(idUser, that.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, idQuestion, isLike);
    }

    public String getIdUser() {
        return idUser;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
}
