package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class QuizRatingService {

    private QuizRatingRepository quizRatingRepository;

    @Autowired
    public QuizRatingService(QuizRatingRepository quizRatingRepository) {
        this.quizRatingRepository = quizRatingRepository;
    }

    public List<QuizRating> findAll() {
        return quizRatingRepository.findAll();
    }

    public void updateQuizRating(QuizRating quizRating) {
        quizRatingRepository.updateQuizRating(quizRating);
    }

    public QuizRating findQuizRatingById(int idQuiz, String idUser) {
        return quizRatingRepository.findQuizRatingById(idQuiz, idUser);
    }

    public void insertQuizRating(QuizRating quizRating) {
        quizRatingRepository.addQuizRating(quizRating);
    }

    public void deleteQuizRating(int idQuiz, String idUser) {
        quizRatingRepository.deleteQuizRating(idQuiz, idUser);
    }

    public boolean exists(String idUser, int idQuiz) {
        return quizRatingRepository.exists(idUser, idQuiz);
    }
}
