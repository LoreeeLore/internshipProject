package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class AnswerUserMapper {

    public AnswerUserDto convertToDTO(AnswerUser answerUser) {
        AnswerUserDto answerUserDto = new AnswerUserDto();

        answerUserDto.setIdAnswerUser(answerUser.getIdAnswerUser());
        answerUserDto.setIdUser(answerUser.getIdUser());
        answerUserDto.setIdQuiz(answerUser.getIdQuiz());
        answerUserDto.setIdQuestion(answerUser.getIdQuestion());
        answerUserDto.setIdAnswer(answerUser.getIdAnswer());
        answerUserDto.setAnswerText(answerUser.getAnswerText());

        return answerUserDto;
    }

    public AnswerUser convertToEntity(AnswerUserDto answerUserDto) {
        AnswerUser answerUser = new AnswerUser();

        answerUser.setIdAnswerUser(answerUserDto.getIdAnswerUser());
        answerUser.setIdUser(answerUserDto.getIdUser());
        answerUser.setIdQuiz(answerUserDto.getIdQuiz());
        answerUser.setIdQuestion(answerUserDto.getIdQuestion());
        answerUser.setIdAnswer(answerUserDto.getIdAnswer());
        answerUser.setAnswerText(answerUserDto.getAnswerText());

        return answerUser;
    }
}
