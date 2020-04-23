package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class QuestionRatingMapper {

    public QuestionRatingDto convertToDTO(QuestionRating questionRating) {
        QuestionRatingDto questionRatingDto = new QuestionRatingDto();

        questionRatingDto.setIdQuestion(questionRating.getIdQuestion());
        questionRatingDto.setIdUser(questionRating.getIdUser());
        questionRatingDto.setLike(questionRating.isLike());

        return questionRatingDto;
    }

    public QuestionRating convertToEntity(QuestionRatingDto questionRatingDto) {
        QuestionRating questionRating = new QuestionRating();

        questionRating.setIdQuestion(questionRatingDto.getIdQuestion());
        questionRating.setIdUser(questionRatingDto.getIdUser());
        questionRating.setLike(questionRatingDto.isLike());

        return questionRating;
    }
}
