package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class PlayAnswerMapper {
    public PlayAnswerDto convertToDTO(PlayAnswer playAnswer) {
        PlayAnswerDto playAnswerDto = new PlayAnswerDto();

        playAnswerDto.setIdPlayAnswer(playAnswer.getIdPlayAnswer());
        playAnswerDto.setIdAnswer(playAnswer.getIdAnswer());
        playAnswerDto.setIdPlayQuestion(playAnswer.getIdPlayQuestion());
        playAnswerDto.setText(playAnswer.getText());

        return playAnswerDto;
    }

    public PlayAnswer convertToEntity(PlayAnswerDto playAnswerDto) {
        PlayAnswer playAnswer = new PlayAnswer();

        playAnswer.setIdPlayAnswer(playAnswerDto.getIdPlayAnswer());
        playAnswer.setIdAnswer(playAnswerDto.getIdAnswer());
        playAnswer.setIdPlayQuestion(playAnswerDto.getIdPlayQuestion());
        playAnswer.setText(playAnswerDto.getText());

        return playAnswer;
    }
}
