package com.studlabs.quiz.model;

import java.util.*;

public class QuestionCorrection {

    private String idUser;
    private int idQuestion;
    private String correctionText;

    public QuestionCorrection() {
    }

    public QuestionCorrection(String idUser, int idQuestion, String correctionText) {
        this.idUser = idUser;
        this.idQuestion = idQuestion;
        this.correctionText = correctionText;
    }

    public String getIdUser() {
        return idUser;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public String getCorrectionText() {
        return correctionText;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public void setCorrectionText(String correctionText) {
        this.correctionText = correctionText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionCorrection)) return false;
        QuestionCorrection that = (QuestionCorrection) o;
        return idQuestion == that.idQuestion &&
                Objects.equals(idUser, that.idUser) &&
                Objects.equals(correctionText, that.correctionText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, idQuestion, correctionText);
    }
}
