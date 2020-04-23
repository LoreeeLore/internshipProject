package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;

public class QuizMapper {

    public QuizDto convertToDTO(Quiz quiz) {
        QuizDto quizDto = new QuizDto();

        quizDto.setIdQuiz(quiz.getIdQuiz());
        quizDto.setCategory(quiz.getCategory());
        quizDto.setDifficulty(quiz.getDifficulty());
        quizDto.setCompletionRate(quiz.getCompletionRate());
        quizDto.setTimeInMinutes(quiz.getTimeInMinutes());
        quizDto.setPublic(quiz.isPublic());
        quizDto.setRandom(quiz.isRandom());

        return quizDto;
    }

    public Quiz convertToEntity(QuizDto quizDto) {
        Quiz quiz = new Quiz();

        quiz.setIdQuiz(quizDto.getIdQuiz());
        quiz.setCategory(quizDto.getCategory());
        quiz.setDifficulty(quizDto.getDifficulty());
        quiz.setCompletionRate(quizDto.getCompletionRate());
        quiz.setTimeInMinutes((int) quizDto.getTimeInMinutes());
        quiz.setPublic(quizDto.isPublic());
        quiz.setRandom(quizDto.isRandom());

        return quiz;
    }
}
