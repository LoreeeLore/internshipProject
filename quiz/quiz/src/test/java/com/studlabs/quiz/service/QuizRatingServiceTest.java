package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class QuizRatingServiceTest {

    private QuizRatingService quizRatingService;

    @Mock
    private QuizRatingRepository quizRatingRepository;

    @Before
    public void setUp() {
        quizRatingService = new QuizRatingService(quizRatingRepository);
    }

    @Test
    public void testGetAllQuizRatings() {
        List<QuizRating> ratings = new ArrayList<>();

        QuizRating quizRating1 = new QuizRating("1", 4, true);
        QuizRating quizRating2 = new QuizRating("1", 17, false);

        ratings.add(quizRating1);
        ratings.add(quizRating2);

        when(quizRatingRepository.findAll()).thenReturn(ratings);

        List<QuizRating> result = quizRatingService.findAll();

        assertEquals(ratings, result);
    }

    @Test
    public void testAddQuizRating() {
        QuizRating quizRating = new QuizRating("13", 116, true);

        quizRatingService.insertQuizRating(quizRating);
        quizRatingService.insertQuizRating(quizRating);

        verify(quizRatingRepository, times(2)).addQuizRating(quizRating);
    }

    @Test
    public void testUpdateQuizRating() {
        QuizRating quizRating = new QuizRating("13", 116, true);
        quizRatingService.updateQuizRating(quizRating);
        verify(quizRatingRepository).updateQuizRating(quizRating);
    }

    @Test
    public void testFindQuizRatingById() {
        QuizRating quizRating = new QuizRating("13", 116, true);

        when(quizRatingRepository.findQuizRatingById(quizRating.getIdQuiz(), quizRating.getIdUser())).thenReturn(quizRating);

        QuizRating result = quizRatingService.findQuizRatingById(quizRating.getIdQuiz(), quizRating.getIdUser());
        assertEquals(result.getIdQuiz(), quizRating.getIdQuiz());
        assertEquals(result.getIdUser(), quizRating.getIdUser());
    }

    @Test
    public void testDeleteQuizRating() {
        QuizRating quizRating = new QuizRating("13", 116, true);
        quizRatingService.deleteQuizRating(quizRating.getIdQuiz(), quizRating.getIdUser());
        verify(quizRatingRepository).deleteQuizRating(quizRating.getIdQuiz(), quizRating.getIdUser());
    }

    @Test
    public void testExitsQuizRating() {
        quizRatingService.exists("1", 1);

        verify(quizRatingRepository, times(1)).exists("1", 1);
    }
}
