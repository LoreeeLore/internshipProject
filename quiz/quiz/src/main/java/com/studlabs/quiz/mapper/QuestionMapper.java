package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class QuestionMapper {

    public QuestionDto convertToDTO(Question question) {
        QuestionDto questionDto = new QuestionDto();

        questionDto.setIdQuestion(question.getIdQuestion());
        questionDto.setDescription(question.getDescription());
        questionDto.setCategory(question.getCategory());
        questionDto.setImage(question.getImage());
        questionDto.setDifficulty(question.getDifficulty());
        questionDto.setDeprecated(question.isDeprecated());

        return questionDto;
    }

    public Question convertToEntity(QuestionDto questionDto) {
        Question question = new Question();

        question.setIdQuestion(questionDto.getIdQuestion());
        question.setDescription(questionDto.getDescription());
        question.setCategory(questionDto.getCategory());
        question.setImage(questionDto.getImage());
        question.setDifficulty(questionDto.getDifficulty());
        question.setDeprecated(questionDto.isDeprecated());

        return question;
    }
}
