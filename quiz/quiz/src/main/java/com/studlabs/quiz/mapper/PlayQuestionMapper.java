package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class PlayQuestionMapper {

    public PlayQuestionDto convertToDTO(PlayQuestion playQuestion) {
        PlayQuestionDto playQuestionDto = new PlayQuestionDto();

        playQuestionDto.setIdPlayQuestion(playQuestion.getIdPlayQuestion());
        playQuestionDto.setIdQuestion(playQuestion.getIdQuestion());
        playQuestionDto.setIdPlayQuiz(playQuestion.getIdPlayQuiz());
        playQuestionDto.setStartTime(playQuestion.getStartTime());
        playQuestionDto.setEndTime(playQuestion.getEndTime());
        playQuestionDto.setCorrect(playQuestion.isCorrect());

        return playQuestionDto;
    }

    public PlayQuestion convertToEntity(PlayQuestionDto playQuestionDto) {
        PlayQuestion playQuestion = new PlayQuestion();

        playQuestion.setIdPlayQuestion(playQuestionDto.getIdPlayQuestion());
        playQuestion.setIdQuestion(playQuestionDto.getIdQuestion());
        playQuestion.setIdPlayQuiz(playQuestionDto.getIdPlayQuiz());
        playQuestion.setStartTime(playQuestionDto.getStartTime());
        playQuestion.setEndTime(playQuestionDto.getEndTime());
        playQuestion.setCorrect(playQuestionDto.isCorrect());

        return playQuestion;
    }
}
