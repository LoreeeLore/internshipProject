package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class QuestionRatingService {

    private QuestionRatingRepository questionRatingRepository;

    @Autowired
    public QuestionRatingService(QuestionRatingRepository questionRatingRepository) {
        this.questionRatingRepository = questionRatingRepository;
    }

    public void insert(QuestionRating questionRating) {
        questionRatingRepository.insert(questionRating);
    }

    public void delete(String idUser, int idQuestion) {
        questionRatingRepository.delete(idUser, idQuestion);
    }

    public List<QuestionRating> findAll() {
        return questionRatingRepository.findAll();
    }

    public boolean exists(String idUser, int idQuestion) {
        return questionRatingRepository.exists(idUser, idQuestion);
    }

}
