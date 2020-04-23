package com.studlabs.quiz.dto;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

@JsonRootName(value = "answer")
public class AnswerDto {

    private int idAnswer;
    private int idQuestion;
    private boolean isCorrect;
    private String answerText;

    public AnswerDto() {
    }

    public AnswerDto(int idAnswer, int idQuestion, boolean isCorrect, String answerText) {
        this.idAnswer = idAnswer;
        this.idQuestion = idQuestion;
        this.isCorrect = isCorrect;
        this.answerText = answerText;
    }

    public int getIdAnswer() {
        return idAnswer;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public String getText() {
        return answerText;
    }

    public void setIdAnswer(int idAnswer) {
        this.idAnswer = idAnswer;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public void setText(String text) {
        this.answerText = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerDto)) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return idAnswer == answerDto.idAnswer &&
                idQuestion == answerDto.idQuestion &&
                isCorrect == answerDto.isCorrect &&
                Objects.equals(answerText, answerDto.answerText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAnswer, idQuestion, isCorrect, answerText);
    }
}
