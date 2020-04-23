package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class QuestionSuggestionService {

    private QuestionSuggestionRepository questionSuggestionRepository;

    public QuestionSuggestionService(QuestionSuggestionRepository questionSuggestionRepository) {
        this.questionSuggestionRepository = questionSuggestionRepository;
    }

    public boolean exists(int idSuggestion) {
        return questionSuggestionRepository.exists(idSuggestion);
    }

    public List<QuestionSuggestion> getAll() {
        return questionSuggestionRepository.findAll();
    }

    public int insert(QuestionSuggestion questionSuggestion) {
        return questionSuggestionRepository.insertSuggestion(questionSuggestion);
    }

    public void update(int idSuggestion, QuestionSuggestion questionSuggestion) {
        questionSuggestionRepository.update(idSuggestion, questionSuggestion);
    }

    public void delete(int idSuggestion) {
        questionSuggestionRepository.delete(idSuggestion);
    }
}
