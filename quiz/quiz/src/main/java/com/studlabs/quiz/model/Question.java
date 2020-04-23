package com.studlabs.quiz.model;

import java.util.*;

public class Question {

    private int idQuestion;
    private String description;
    private String image;
    private String category;
    private QuestionDifficulty difficulty;
    private boolean isDeprecated;

    public Question() {
    }

    public Question(String description, String category, String image, QuestionDifficulty difficulty, boolean isDeprecated) {
        this.description = description;
        this.category = category;
        this.image = image;
        this.difficulty = difficulty;
        this.isDeprecated = isDeprecated;
    }

    public Question(int idQuestion, String description, String category, String image, QuestionDifficulty difficulty, boolean isDeprecated) {
        this.idQuestion = idQuestion;
        this.description = description;
        this.category = category;
        this.image = image;
        this.difficulty = difficulty;
        this.isDeprecated = isDeprecated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return idQuestion == question.idQuestion &&
                isDeprecated == question.isDeprecated &&
                Objects.equals(description, question.description) &&
                Objects.equals(image, question.image) &&
                Objects.equals(category, question.category) &&
                difficulty == question.difficulty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idQuestion, description, image, category, difficulty, isDeprecated);
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