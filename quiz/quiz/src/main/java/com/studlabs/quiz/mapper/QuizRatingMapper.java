package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class QuizRatingMapper {

    public QuizRatingDto convertToDTO(QuizRating quizRating) {
        QuizRatingDto quizRatingDto = new QuizRatingDto();

        quizRatingDto.setIdQuiz(quizRating.getIdQuiz());
        quizRatingDto.setIdUser(quizRating.getIdUser());
        quizRatingDto.setLike(quizRating.isLike());

        return quizRatingDto;
    }

    public QuizRating convertToEntity(QuizRatingDto quizRatingDto) {
        QuizRating quizRating = new QuizRating();

        quizRating.setIdQuiz(quizRatingDto.getIdQuiz());
        quizRating.setIdUser(quizRatingDto.getIdUser());
        quizRating.setLike(quizRatingDto.isLike());

        return quizRating;
    }
}
