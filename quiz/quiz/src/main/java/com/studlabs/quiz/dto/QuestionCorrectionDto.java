package com.studlabs.quiz.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.*;

@JsonRootName(value = "questionCorrection")
@JsonInclude(Include.NON_NULL)
public class QuestionCorrectionDto {

    private String idUser;
    private int idQuestion;
    private String correctionText;

    public QuestionCorrectionDto() {
    }

    public QuestionCorrectionDto(String idUser, int idQuestion, String correctionText) {
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
}
