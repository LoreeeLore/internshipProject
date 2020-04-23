package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class QuestionCorrectionMapper {

    public QuestionCorrectionDto convertToDTO(QuestionCorrection questionCorrection) {
        QuestionCorrectionDto questionCorrectionDto = new QuestionCorrectionDto();

        questionCorrectionDto.setIdUser(questionCorrection.getIdUser());
        questionCorrectionDto.setIdQuestion(questionCorrection.getIdQuestion());
        questionCorrectionDto.setCorrectionText(questionCorrection.getCorrectionText());

        return questionCorrectionDto;
    }

    public QuestionCorrection convertToEntity(QuestionCorrectionDto questionCorrectionDto) {
        QuestionCorrection questionCorrection = new QuestionCorrection();

        questionCorrection.setIdUser(questionCorrectionDto.getIdUser());
        questionCorrection.setIdQuestion(questionCorrectionDto.getIdQuestion());
        questionCorrection.setCorrectionText(questionCorrectionDto.getCorrectionText());

        return questionCorrection;
    }
}
