package com.studlabs.quiz.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.*;
import com.studlabs.quiz.model.*;

@JsonRootName(value = "question")
@JsonInclude(Include.NON_NULL)
public class QuestionDto {

    private int idQuestion;
    private String description;
    private String image;
    private String category;
    private QuestionDifficulty difficulty;
    private boolean isDeprecated;

    public QuestionDto() {
    }

    public QuestionDto(int idQuestion, String description, String category, String image, QuestionDifficulty difficulty, boolean isDeprecated) {
        this.idQuestion = idQuestion;
        this.description = description;
        this.category = category;
        this.image = image;
        this.difficulty = difficulty;
        this.isDeprecated = isDeprecated;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isDeprecated() {
        return isDeprecated;
    }

    public void setDeprecated(boolean deprecated) {
        isDeprecated = deprecated;
    }
}