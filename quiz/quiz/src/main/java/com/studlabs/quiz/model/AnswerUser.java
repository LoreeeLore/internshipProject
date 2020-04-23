package com.studlabs.quiz.model;

public class AnswerUser {

    private int idAnswerUser;
    private String idUser;
    private int idQuiz;
    private int idQuestion;
    private int idAnswer;
    private String answerText;

    public AnswerUser() {
    }

    public AnswerUser(int idAnswerUser, String idUser, int idQuiz, int idQuestion, int idAnswer, String answerText) {
        this.idAnswerUser = idAnswerUser;
        this.idUser = idUser;
        this.idQuiz = idQuiz;
        this.idQuestion = idQuestion;
        this.idAnswer = idAnswer;
        this.answerText = answerText;
    }

    public int getIdAnswerUser() {
        return idAnswerUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public int getIdQuiz() {
        return idQuiz;
    }

    public int getIdQuestion() {
        return idQuestion;
    }

    public int getIdAnswer() {
        return idAnswer;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setIdAnswerUser(int idAnswerUser) {
        this.idAnswerUser = idAnswerUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setIdQuiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public void setIdAnswer(int idAnswer) {
        this.idAnswer = idAnswer;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
}
