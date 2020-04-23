package com.studlabs.quiz.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.*;

@JsonRootName(value = "quizRating")
@JsonInclude(Include.NON_NULL)
public class QuizRatingDto {

    private String idUser;
    private int idQuiz;
    private boolean isLike;

    public QuizRatingDto(String idUser, int idQuiz, boolean isLike) {
        this.idUser = idUser;
        this.idQuiz = idQuiz;
        this.isLike = isLike;
    }

    public QuizRatingDto() {
    }

    public String getIdUser() {
        return idUser;
    }

    public int getIdQuiz() {
        return idQuiz;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
}
