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
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QuizRatingControllerUnitTest {

    @Mock
    private QuizRatingService quizRatingService;

    private QuizRatingMapper quizRatingMapper;

    private QuizRatingController quizRatingController;

    @Before
    public void setUp() {
        quizRatingController = new QuizRatingController(quizRatingService);
        quizRatingMapper = new QuizRatingMapper();
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    @Test
    public void testGetAllQuizRatings() {
        List<QuizRating> ratings = new ArrayList<>();

        QuizRating quizRating1 = new QuizRating("1", 4, true);
        QuizRating quizRating2 = new QuizRating("1", 17, false);

        ratings.add(quizRating1);
        ratings.add(quizRating2);

        when(quizRatingService.findAll()).thenReturn(ratings);

        List<QuizRatingDto> quizRatingDtos = quizRatingController.findAll().getBody();
        List<QuizRating> resultRatings = new ArrayList<>();

        if (quizRatingDtos != null) {
            for (QuizRatingDto quizRatingDto : quizRatingDtos) {
                resultRatings.add(quizRatingMapper.convertToEntity(quizRatingDto));
            }
        }

        assertEquals(resultRatings, ratings);
    }

    @Test
    public void testAddQuizRatingValidIds() {
        QuizRatingDto quizRating = new QuizRatingDto("13", 116, true);

        when(quizRatingService.exists(quizRating.getIdUser(), quizRating.getIdQuiz())).thenReturn(false);
        quizRatingController.insertQuizRating(quizRating);

        verify(quizRatingService, times(1)).insertQuizRating(quizRatingMapper.convertToEntity(quizRating));
    }

    @Test
    public void testAddQuizRatingInvalidIds() {
        QuizRatingDto quizRating = new QuizRatingDto("13", 116, true);

        when(quizRatingService.exists(quizRating.getIdUser(), quizRating.getIdQuiz())).thenReturn(true);
        quizRatingController.insertQuizRating(quizRating);

        verify(quizRatingService, times(0)).insertQuizRating(quizRatingMapper.convertToEntity(quizRating));
    }

    @Test
    public void testUpdateQuizRatingValidIds() {
        QuizRatingDto quizRating = new QuizRatingDto("1", 116, false);

        when(quizRatingService.exists(quizRating.getIdUser(), quizRating.getIdQuiz())).thenReturn(true);
        quizRatingController.updateQuizRating(116, quizRating);

        verify(quizRatingService).updateQuizRating(quizRatingMapper.convertToEntity(quizRating));
    }

    @Test
    public void testUpdateQuizRatingInvalidIds() {
        QuizRatingDto quizRating = new QuizRatingDto("1", 116, false);

        when(quizRatingService.exists(quizRating.getIdUser(), quizRating.getIdQuiz())).thenReturn(false);
        quizRatingController.updateQuizRating(116, quizRating);

        verify(quizRatingService, times(0)).updateQuizRating(quizRatingMapper.convertToEntity(quizRating));
    }

    @Test
    public void testFindQuizRatingByIdValid() {
        when(quizRatingService.exists("1", 1)).thenReturn(true);
        when(quizRatingService.findQuizRatingById(1, "1")).thenReturn(new QuizRating());

        quizRatingController.findQuizRatingById(1);

        verify(quizRatingService, times(1)).findQuizRatingById(1, "1");
    }

    @Test
    public void testFindQuizRatingByIdInvalid() {
        when(quizRatingService.exists("1", 1)).thenReturn(false);
        when(quizRatingService.findQuizRatingById(1, "1")).thenReturn(new QuizRating());

        quizRatingController.findQuizRatingById(1);

        verify(quizRatingService, times(0)).findQuizRatingById(1, "1");
    }

    @Test
    public void testDeleteQuizRatingValidIds() {
        when(quizRatingService.exists("1", 1)).thenReturn(true);

        quizRatingController.deleteQuizRating(1);

        verify(quizRatingService, times(1)).deleteQuizRating(1, "1");
    }

    @Test
    public void testDeleteQuizRatingInvalidIds() {
        when(quizRatingService.exists("1", 1)).thenReturn(false);

        quizRatingController.deleteQuizRating(1);

        verify(quizRatingService, times(0)).deleteQuizRating(1, "1");
    }
}
