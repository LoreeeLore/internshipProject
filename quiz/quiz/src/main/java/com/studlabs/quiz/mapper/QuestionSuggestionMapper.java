package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class QuestionSuggestionMapper {

    public QuestionSuggestionDto convertToDTO(QuestionSuggestion questionSuggestion) {
        QuestionSuggestionDto questionSuggestionDto = new QuestionSuggestionDto();

        questionSuggestionDto.setIdUser(questionSuggestion.getIdUser());
        questionSuggestionDto.setIdQuestionSuggestion(questionSuggestion.getIdQuestionSuggestion());
        questionSuggestionDto.setDescription(questionSuggestion.getDescription());
        questionSuggestionDto.setType(questionSuggestion.getType());
        questionSuggestionDto.setCategory(questionSuggestion.getCategory());
        questionSuggestionDto.setDifficulty(questionSuggestion.getDifficulty());
        questionSuggestionDto.setImage(questionSuggestion.getImage());

        return questionSuggestionDto;
    }

    public QuestionSuggestion convertToEntity(QuestionSuggestionDto questionSuggestionDto) {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion();

        questionSuggestion.setIdUser(questionSuggestionDto.getIdUser());
        questionSuggestion.setIdQuestionSuggestion(questionSuggestionDto.getIdQuestionSuggestion());
        questionSuggestion.setDescription(questionSuggestionDto.getDescription());
        questionSuggestion.setType(questionSuggestionDto.getType());
        questionSuggestion.setCategory(questionSuggestionDto.getCategory());
        questionSuggestion.setDifficulty(questionSuggestionDto.getDifficulty());
        questionSuggestion.setImage(questionSuggestionDto.getImage());

        return questionSuggestion;
    }
}
