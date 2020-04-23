package com.studlabs.quiz.service;

import com.studlabs.quiz.controller.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public List<Answer> findAllAnswers() {
        return answerRepository.findAllAnswers();
    }

    public Answer findAnswerById(int answerId) {
        return answerRepository.findAnswerById(answerId);
    }

    public int insertAnswer(Answer answer) {
        return answerRepository.insertAnswer(answer);
    }

    public void updateAnswer(Answer answer) {
        answerRepository.updateAnswer(answer);
    }

    public void deleteAnswer(int answerId) {
        answerRepository.deleteAnswer(answerId);
    }

    public boolean exists(int id) {
        return answerRepository.exists(id);
    }

    public boolean checkIfTooManyAnswers(int questionId) {
        return answerRepository.findAllByQuestionId(questionId).size() < AnswerController.MAX_NUMBER_OF_ANSWERS;
    }
}
