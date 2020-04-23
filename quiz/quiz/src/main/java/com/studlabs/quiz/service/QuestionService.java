package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class QuestionService {

    private QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public int insert(Question question) {
        return questionRepository.insert(question);
    }

    public void delete(int id) {
        questionRepository.delete(id);
    }

    public void updateQuestion(Question question) {
        questionRepository.updateQuestion(question);
    }

    public Question findQuestionById(int id) {
        return questionRepository.findQuestionById(id);
    }

    public List<Question> findQuestionsByCategory(String category) {
        return questionRepository.findQuestionsByCategory(category);
    }

    public void setDeprecated(int id, boolean isDeprecated) {
        questionRepository.setDeprecated(id, isDeprecated);
    }

    public boolean exists(int id) {
        return questionRepository.exists(id);
    }
}
