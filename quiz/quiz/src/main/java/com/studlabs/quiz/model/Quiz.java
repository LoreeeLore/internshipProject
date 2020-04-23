package com.studlabs.quiz.model;

import java.util.*;

public class Quiz {

    private int idQuiz;
    private String category;
    private QuizDifficulty difficulty;
    private double completionRate;
    private int timeInMinutes;
    private boolean isPublic;
    private boolean isRandom;

    public Quiz() {
    }

    public Quiz(int idQuiz, String category, QuizDifficulty difficulty, double completionRate, int timeInMinutes, boolean isPublic, boolean isRandom) {
        this.idQuiz = idQuiz;
        this.category = category;
        this.difficulty = difficulty;
        this.completionRate = completionRate;
        this.timeInMinutes=timeInMinutes;
        this.isPublic = isPublic;
        this.isRandom = isRandom;
    }

    public Quiz(String category, QuizDifficulty difficulty, double completionRate, int timeInMinutes, boolean isPublic, boolean isRandom) {
        this.category = category;
        this.difficulty = difficulty;
        this.completionRate = completionRate;
        this.timeInMinutes=timeInMinutes;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return idQuiz == quiz.idQuiz &&
                Double.compare(quiz.completionRate, completionRate) == 0 &&
                Objects.equals(timeInMinutes, quiz.timeInMinutes) &&
                isPublic == quiz.isPublic &&
                isRandom == quiz.isRandom &&
                Objects.equals(category, quiz.category) &&
                difficulty == quiz.difficulty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idQuiz, category, difficulty, completionRate, timeInMinutes, isPublic, isRandom);
    }

    public int getTimeInMinutes() {
        return timeInMinutes;
    }

    public void setTimeInMinutes(int timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }
}
