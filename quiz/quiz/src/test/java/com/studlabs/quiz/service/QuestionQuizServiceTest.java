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
public class QuestionQuizServiceTest {

    private QuestionQuizService questionQuizService;

    @Mock
    private QuestionQuizRepository questionQuizRepository;

    @Before
    public void setUp() {
        questionQuizService = new QuestionQuizService(questionQuizRepository);
    }

    @Test
    public void assignQuestionToQuiz() {
        questionQuizService.assignQuestionToQuiz(1, 1);
        questionQuizService.assignQuestionToQuiz(1, 1);
        questionQuizService.assignQuestionToQuiz(2, 2);

        verify(questionQuizRepository, times(2)).assignQuestionToQuiz(1, 1);
        verify(questionQuizRepository, times(1)).assignQuestionToQuiz(2, 2);
    }

    @Test
    public void deleteQuestionFromQuiz() {
        questionQuizService.deleteQuestionFromQuiz(1, 1);

        verify(questionQuizRepository, times(1)).deleteQuestionFromQuiz(1, 1);
    }

    @Test
    public void testQuestionQuizExists() {
        questionQuizService.exists(1, 1);

        verify(questionQuizRepository).exists(1, 1);
    }

    @Test
    public void testQuestionQuizCheckIfTooManyQuestions() {
        List<QuestionQuiz> questionQuizList = new ArrayList<>();

        when(questionQuizRepository.findAllByQuizId(1)).thenReturn(questionQuizList);

        assertTrue(questionQuizService.checkIfValidNumberOfQuestions(1));
        verify(questionQuizRepository).findAllByQuizId(1);
    }
}
