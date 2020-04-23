package com.studlabs.quiz.controller;

import com.studlabs.quiz.security.*;
import com.studlabs.quiz.service.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;

import static com.studlabs.quiz.util.TestAuthenticationProvider.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class QuestionQuizControllerTest {

    private QuestionQuizController questionQuizController;

    @Mock
    private QuestionQuizService questionQuizService;
    private QuestionService questionService;
    @Before
    public void setUp() {
        questionQuizController = new QuestionQuizController(questionQuizService, questionService);
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    @Test
    public void assignQuestionToQuizValidIds() {
        when(questionQuizService.exists(1, 1)).thenReturn(false);
        when(questionQuizService.checkIfValidNumberOfQuestions(1)).thenReturn(true);

        questionQuizController.assignQuestionToQuiz(1, 1);

        verify(questionQuizService).assignQuestionToQuiz(1, 1);
    }

    @Test
    public void assignQuestionToQuizInvalidIds() {
        when(questionQuizService.exists(1, 1)).thenReturn(true);

        questionQuizController.assignQuestionToQuiz(1, 1);

        verify(questionQuizService, times(0)).assignQuestionToQuiz(1, 1);
    }

    @Test
    public void assignQuestionToQuizTooManyQuestions() {
        when(questionQuizService.exists(1, 1)).thenReturn(false);
        when(questionQuizService.checkIfValidNumberOfQuestions(1)).thenReturn(false);

        questionQuizController.assignQuestionToQuiz(1, 1);

        verify(questionQuizService, times(0)).assignQuestionToQuiz(1, 1);
    }

    @Test
    public void deleteQuestionFromQuizValidIds() {
        when(questionQuizService.exists(1, 1)).thenReturn(true);

        questionQuizController.deleteQuestionFromQuiz(1, 1);

        verify(questionQuizService, times(1)).deleteQuestionFromQuiz(1, 1);
    }

    @Test
    public void deleteQuestionFromQuizInvalidIds() {
        when(questionQuizService.exists(1, 1)).thenReturn(false);

        questionQuizController.deleteQuestionFromQuiz(1, 1);

        verify(questionQuizService, times(0)).deleteQuestionFromQuiz(1, 1);
    }
}
