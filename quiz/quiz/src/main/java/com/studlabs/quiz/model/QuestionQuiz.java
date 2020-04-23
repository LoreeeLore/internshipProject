package com.studlabs.quiz.model;

import java.util.*;

public class QuestionQuiz {

    private int idQuestion;
    private int idQuiz;

    public QuestionQuiz() {
    }

    public QuestionQuiz(int idQuestion, int idQuiz) {
        this.idQuestion = idQuestion;
        this.idQuiz = idQuiz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionQuiz that = (QuestionQuiz) o;
        return idQuestion == that.idQuestion &&
                idQuiz == that.idQuiz;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idQuestion, idQuiz);
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public int getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }
}
