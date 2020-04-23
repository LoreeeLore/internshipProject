package com.studlabs.quiz.model;

import java.time.*;
import java.util.*;

public class QuestionSuggestion {

    private int idQuestionSuggestion;
    private String idUser;
    private String description;
    private String image;
    private String category;
    private String type;
    private QuestionDifficulty difficulty;

    public QuestionSuggestion() {
    }

    public QuestionSuggestion(String idUser, String description) {
        this.idUser = idUser;
        this.description = description;
    }

    public QuestionSuggestion(String idUser, String description, String image, String category, String type, QuestionDifficulty difficulty) {
        this.idUser = idUser;
        this.description = description;
        this.image = image;
        this.category = category;
        this.type = type;
        this.difficulty = difficulty;
    }

    public QuestionSuggestion(int idQuestionSuggestion, String idUser, String description, String image, String category, String type, QuestionDifficulty difficulty) {
        this(idUser, description, image, category, type, difficulty);
        this.idQuestionSuggestion = idQuestionSuggestion;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionSuggestion)) return false;
        QuestionSuggestion that = (QuestionSuggestion) o;
        return idQuestionSuggestion == that.idQuestionSuggestion &&
                Objects.equals(idUser, that.idUser) &&
                Objects.equals(description, that.description) &&
                Objects.equals(image, that.image) &&
                Objects.equals(category, that.category) &&
                Objects.equals(type, that.type) &&
                difficulty == that.difficulty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idQuestionSuggestion, idUser, description, image, category, type, difficulty);
    }
}
