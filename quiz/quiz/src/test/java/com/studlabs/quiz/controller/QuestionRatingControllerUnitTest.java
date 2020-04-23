package com.studlabs.quiz.controller;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.mapper.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.security.*;
import com.studlabs.quiz.service.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;

import java.util.*;

import static com.studlabs.quiz.util.TestAuthenticationProvider.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QuestionRatingControllerUnitTest {

    @Mock
    private QuestionRatingService questionRatingService;

    private QuestionRatingController questionRatingController;

    private QuestionRatingMapper questionRatingMapper = new QuestionRatingMapper();

    @Before
    public void setUp() {
        questionRatingController = new QuestionRatingController(questionRatingService);
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    @Test
    public void testAddQuestionRatingValidIds() {
        QuestionRating questionRating = new QuestionRating("1", 5, true);

        when(questionRatingService.exists("1", 5)).thenReturn(false);
        questionRatingController.rateQuestion(5, questionRatingMapper.convertToDTO(questionRating));

        verify(questionRatingService, times(1)).insert(questionRating);
    }

    @Test
    public void testAddQuestionRatingAlreadyExists() {
        QuestionRating questionRating = new QuestionRating("1", 5, true);

        when(questionRatingService.exists("1", 5)).thenReturn(true);
        questionRatingController.rateQuestion(5, questionRatingMapper.convertToDTO(questionRating));

        verify(questionRatingService, times(0)).insert(questionRating);
    }

    @Test
    public void testDeleteQuestionRatingValidIds() {
        when(questionRatingService.exists("1", 5)).thenReturn(true);

        questionRatingController.deleteRating(5);

        verify(questionRatingService).delete("1", 5);
    }

    @Test
    public void testDeleteQuestionRatingInvalidIds() {
        when(questionRatingService.exists("5", 5)).thenReturn(false);

        questionRatingController.deleteRating(5);

        verify(questionRatingService, times(0)).delete("5", 5);
    }

    @Test
    public void testGetAllQuestionRatings() {
        List<QuestionRating> questionRatings = new ArrayList<>();
        QuestionRating questionRating1 = new QuestionRating("5", 6, true);
        QuestionRating questionRating2 = new QuestionRating("7", 3, true);

        questionRatings.add(questionRating1);
        questionRatings.add(questionRating2);

        when(questionRatingService.findAll()).thenReturn(questionRatings);

        List<QuestionRatingDto> questionDtos = questionRatingController.findAll().getBody();
        List<QuestionRating> resultQuestions = new ArrayList<>();

        if (questionDtos != null) {
            for (QuestionRatingDto questionDto : questionDtos) {
                resultQuestions.add(questionRatingMapper.convertToEntity(questionDto));
            }
        }

        assertEquals(resultQuestions, questionRatings);
    }
}
