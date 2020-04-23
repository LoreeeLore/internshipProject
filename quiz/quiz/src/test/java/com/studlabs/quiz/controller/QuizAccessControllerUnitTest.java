package com.studlabs.quiz.controller;

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
public class QuizAccessControllerUnitTest {

    private QuizAccessController quizAccessController;

    @Mock
    private QuizAccessService quizAccessService;

    @Mock
    private QuizService quizService;

    @Before
    public void setUp() {
        quizAccessController = new QuizAccessController(quizAccessService, quizService);
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    @Test
    public void testFindAllQuizAccess() {
        List<QuizAccess> quizzes = new ArrayList<>();
        QuizAccess quizAccess1 = new QuizAccess(1, "1");
        QuizAccess quizAccess2 = new QuizAccess(2, "2");

        quizzes.add(quizAccess1);
        quizzes.add(quizAccess2);

        when(quizAccessService.findAll()).thenReturn(quizzes);

        List<QuizAccess> result = quizAccessController.findAll().getBody();

        assertEquals(quizzes, result);
    }

    @Test
    public void assignQuizToUserWithValidIdsAndQuizPrivate() {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,120, false, true);

        when(quizService.exists(1)).thenReturn(true);
        when(quizService.findById(1)).thenReturn(quiz);
        when(quizAccessService.exists("1", 1)).thenReturn(false);

        quizAccessController.assignQuizToUser(quiz.getIdQuiz());

        verify(quizAccessService, times(1)).assignQuizToUser("1", 1);
    }

    @Test
    public void assignQuizToUserWithInvalidIdsAndQuizPrivate() {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,120, false, true);

        when(quizService.exists(1)).thenReturn(false);

        quizAccessController.assignQuizToUser(quiz.getIdQuiz());

        verify(quizAccessService, times(0)).assignQuizToUser("1", 1);
    }

    @Test
    public void assignQuizToUserWithValidIdsAndQuizPublic() {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0, 120,true, true);

        when(quizService.exists(1)).thenReturn(true);
        when(quizService.findById(1)).thenReturn(quiz);

        quizAccessController.assignQuizToUser(quiz.getIdQuiz());

        verify(quizAccessService, times(0)).assignQuizToUser("1", 1);
    }

    @Test
    public void assignQuizToUserWithInvalidIdsAndQuizPublic() {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0, 100,true, true);

        when(quizService.exists(1)).thenReturn(false);
        when(quizService.findById(1)).thenReturn(quiz);

        quizAccessController.assignQuizToUser(quiz.getIdQuiz());

        verify(quizAccessService, times(0)).assignQuizToUser("1", 1);
    }

    @Test
    public void deleteQuizFromUserWithValidIds() {
        when(quizAccessService.exists("1", 1)).thenReturn(true);

        quizAccessController.deleteUserFromQuiz(1);

        verify(quizAccessService, times(1)).deleteUserFromQuiz("1", 1);
    }

    @Test
    public void deleteQuizFromUserWithInvalidIds() {
        when(quizAccessService.exists("1", 1)).thenReturn(false);

        quizAccessController.deleteUserFromQuiz(1);

        verify(quizAccessService, times(0)).deleteUserFromQuiz("1", 1);
    }


}
