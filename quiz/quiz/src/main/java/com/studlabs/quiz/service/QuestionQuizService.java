package com.studlabs.quiz.service;

import com.studlabs.quiz.controller.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class QuestionQuizService {

    private QuestionQuizRepository questionQuizRepository;

    public QuestionQuizService(QuestionQuizRepository questionQuizRepository) {
        this.questionQuizRepository = questionQuizRepository;
    }

    public List<QuestionQuiz> findAllByQuizID(int idQuiz) {
        return questionQuizRepository.findAllByQuizId(idQuiz);
    }

    public List<QuestionQuiz> findAll() {
        return questionQuizRepository.findAll();
    }

    public void assignQuestionToQuiz(int idQuiz, int idQuestion) {
        questionQuizRepository.assignQuestionToQuiz(idQuiz, idQuestion);
    }

    public void deleteQuestionFromQuiz(int idQuiz, int idQuestion) {
        questionQuizRepository.deleteQuestionFromQuiz(idQuiz, idQuestion);
    }

    public boolean exists(int idQuestion, int idQuiz) {
        return questionQuizRepository.exists(idQuestion, idQuiz);
    }

    public boolean checkIfValidNumberOfQuestions(int idQuiz) {
        return questionQuizRepository.findAllByQuizId(idQuiz).size() < QuestionQuizController.MAX_NUMBER_OF_QUESTIONS;
    }
}