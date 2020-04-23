package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class PlayQuizMapper {

    public PlayQuizDto convertToDTO(PlayQuiz playQuiz) {
        PlayQuizDto playQuizDto = new PlayQuizDto();

        playQuizDto.setIdPlayQuiz(playQuiz.getIdPlayQuiz());
        playQuizDto.setIdQuiz(playQuiz.getIdQuiz());
        playQuizDto.setIdUser(playQuiz.getIdUser());
        playQuizDto.setStartTime(playQuiz.getStartTime());
        playQuizDto.setEndTime(playQuiz.getEndTime());
        playQuizDto.setRate(playQuiz.getRate());
        playQuizDto.setStatus(playQuiz.getStatus());

        return playQuizDto;
    }

    public PlayQuiz convertToEntity(PlayQuizDto playQuizDto) {
        PlayQuiz playQuiz = new PlayQuiz();

        playQuiz.setIdPlayQuiz(playQuizDto.getIdPlayQuiz());
        playQuiz.setIdQuiz(playQuizDto.getIdQuiz());
        playQuiz.setIdUser(playQuizDto.getIdUser());
        playQuiz.setStartTime(playQuizDto.getStartTime());
        playQuiz.setEndTime(playQuizDto.getEndTime());
        playQuiz.setRate(playQuizDto.getRate());
        playQuiz.setStatus(playQuizDto.getStatus());

        return playQuiz;
    }
}
