package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;
import org.junit.*;

import static org.junit.Assert.*;

public class QuizMapperTest {

    private QuizMapper quizMapper = new QuizMapper();

    @Test
    public void convertToDTO() {
        Quiz quiz = new Quiz(1, "General culture", QuizDifficulty.MODERATE, 20,120, true, false);
        QuizDto quizDto = quizMapper.convertToDTO(quiz);

        assertEquals(quizDto.getIdQuiz(), 1);
        assertEquals(quizDto.getCategory(), "General culture");
        Assert.assertEquals(quizDto.getDifficulty(), QuizDifficulty.MODERATE);
        assertEquals(quizDto.getCompletionRate(), 20, 0.01);
        assertEquals(quizDto.getTimeInMinutes(),120);
        assertTrue(quizDto.isPublic());
        assertFalse(quizDto.isRandom());
    }

    @Test
    public void convertToEntity() {
        QuizDto quizDto = new QuizDto(1, "General culture", QuizDifficulty.MODERATE, 20, 120,true, false);
        Quiz quiz = quizMapper.convertToEntity(quizDto);

        assertEquals(quiz.getIdQuiz(), 1);
        assertEquals(quiz.getCategory(), "General culture");
        assertEquals(quiz.getDifficulty(), QuizDifficulty.MODERATE);
        assertEquals(quiz.getCompletionRate(), 20, 0.01);
        assertEquals(quiz.getTimeInMinutes(),120);
        assertTrue(quiz.isPublic());
        assertFalse(quiz.isRandom());
    }
}
