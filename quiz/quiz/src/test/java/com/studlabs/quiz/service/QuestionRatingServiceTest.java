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


@RunWith(MockitoJUnitRunner.class)
public class QuestionRatingServiceTest {

    private QuestionRatingService questionRatingService;

    @Mock
    private QuestionRatingRepository questionRatingRepository;

    @Before
    public void setUp() {
        questionRatingService = new QuestionRatingService(questionRatingRepository);
    }

    @Test
    public void testAddQuestion() {
        QuestionRating questionRating = new QuestionRating("6", 6, false);

        questionRatingService.insert(questionRating);

        verify(questionRatingRepository, times(1)).insert(questionRating);
    }

    @Test
    public void testDeleteQuestion() {
        QuestionRating questionRating = new QuestionRating("6", 6, false);

        questionRatingService.delete(questionRating.getIdUser(), questionRating.getIdQuestion());

        verify(questionRatingRepository).delete(questionRating.getIdUser(), questionRating.getIdQuestion());
    }

    @Test
    public void testGetAllQuestions() {
        List<QuestionRating> questionRatings = new ArrayList<>();
        QuestionRating questionRating1 = new QuestionRating("5", 6, true);
        QuestionRating questionRating2 = new QuestionRating("7", 3, true);

        questionRatings.add(questionRating1);
        questionRatings.add(questionRating2);

        when(questionRatingRepository.findAll()).thenReturn(questionRatings);

        assertEquals(questionRatings, questionRatingService.findAll());
    }

    @Test
    public void testQuestionRatingExists() {
        questionRatingService.exists("1", 1);

        verify(questionRatingRepository).exists("1", 1);
    }
}
