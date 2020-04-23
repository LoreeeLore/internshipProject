package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class AnswerMapper {

    public AnswerDto convertToDTO(Answer answer) {
        AnswerDto answerDto = new AnswerDto();

        answerDto.setIdAnswer(answer.getIdAnswer());
        answerDto.setIdQuestion(answer.getIdQuestion());
        answerDto.setCorrect(answer.isCorrect());
        answerDto.setText(answer.getText());

        return answerDto;
    }

    public Answer convertToEntity(AnswerDto answerDto) {
        Answer answer = new Answer();

        answer.setIdAnswer(answerDto.getIdAnswer());
        answer.setIdQuestion(answerDto.getIdQuestion());
        answer.setCorrect(answerDto.isCorrect());
        answer.setText(answerDto.getText());

        return answer;
    }
}
