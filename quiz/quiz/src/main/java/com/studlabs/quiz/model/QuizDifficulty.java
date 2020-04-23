package com.studlabs.quiz.model;

public enum QuizDifficulty {

    EASY(50),
    MODERATE(60),
    HARD(70);

    private int neededCompletionRate;

    QuizDifficulty(int neededCompletionRate) {
        this.neededCompletionRate = neededCompletionRate;
    }

    public int getNeededCompletionRate() {
        return this.neededCompletionRate;
    }


}