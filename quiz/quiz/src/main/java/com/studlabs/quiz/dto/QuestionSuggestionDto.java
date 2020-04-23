package com.studlabs.quiz.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.*;
import com.studlabs.quiz.model.*;

import java.time.*;

@JsonRootName(value = "questionSuggestion")
@JsonInclude(Include.NON_NULL)
public class QuestionSuggestionDto {

    private int idQuestionSuggestion;
    private String idUser;
    private String description;
    private String image;
    private String category;
    private String type;
    private QuestionDifficulty difficulty;
    private LocalTime time;

    public QuestionSuggestionDto() {
    }

    public QuestionSuggestionDto(int idQuestionSuggestion, String idUser, String description, String image, String category, String type, QuestionDifficulty difficulty, LocalTime time) {
        this.idQuestionSuggestion = idQuestionSuggestion;
        this.idUser = idUser;
        this.description = description;
        this.image = image;
        this.category = category;
        this.type = type;
        this.difficulty = difficulty;
        this.time = time;
    }

    public int getIdQuestionSuggestion() {
        return idQuestionSuggestion;
    }

    public void setIdQuestionSuggestion(int idQuestionSuggestion) {
        this.idQuestionSuggestion = idQuestionSuggestion;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
