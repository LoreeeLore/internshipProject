package com.studlabs.quiz.model;

import java.util.*;

public class QuizAccess {
    private int idQuiz;
    private String idUser;

    public QuizAccess() {
    }

    public QuizAccess(int idQuiz, String idUser) {
        this.idQuiz = idQuiz;
        this.idUser = idUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizAccess)) return false;
        QuizAccess that = (QuizAccess) o;
        return idQuiz == that.idQuiz &&
                Objects.equals(idUser, that.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idQuiz, idUser);
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
}
