package com.studlabs.quiz.dto;

import com.fasterxml.jackson.annotation.*;
import com.studlabs.quiz.model.*;

import java.util.*;

@JsonRootName(value = "quiz")
public class QuizDto {

    private int idQuiz;
    private String category;
    private QuizDifficulty difficulty;
    private double completionRate;
    private long timeInMinutes;
    private boolean isPublic;
    private boolean isRandom;

    public QuizDto() {
    }

    public QuizDto(int idQuiz, String category, QuizDifficulty difficulty, double completionRate, long timeInMinutes, boolean isPublic, boolean isRandom) {
        this.idQuiz = idQuiz;
        this.category = category;
        this.difficulty = difficulty;
        this.completionRate = completionRate;
        this.timeInMinutes = timeInMinutes;
        this.isPublic = isPublic;
        this.isRandom = isRandom;
    }

    public int getIdQuiz() {
        return idQuiz;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public QuizDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuizDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean publicAccess) {
        isPublic = publicAccess;
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public long getTimeInMinutes() {
        return timeInMinutes;
    }

    public void setTimeInMinutes(long timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuizDto quizDto = (QuizDto) o;
        return idQuiz == quizDto.idQuiz &&
                Double.compare(quizDto.completionRate, completionRate) == 0 &&
                Objects.equals(quizDto.timeInMinutes,timeInMinutes) &&
                isPublic == quizDto.isPublic &&
                isRandom == quizDto.isRandom &&
                Objects.equals(category, quizDto.category) &&
                difficulty == quizDto.difficulty;
    }

    @Override
    public String toString() {
        return "QuizDto{" +
                "idQuiz=" + idQuiz +
                ", category='" + category + '\'' +
                ", difficulty=" + difficulty +
                ", completionRate=" + completionRate +
                ", timeInMinutes=" + timeInMinutes +
                ", isPublic=" + isPublic +
                ", isRandom=" + isRandom +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(idQuiz, category, difficulty, completionRate, timeInMinutes, isPublic, isRandom);
    }

}
