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
public class QuizAccessServiceTest {

    private QuizAccessService quizAccessService;

    @Mock
    private QuizAccessRepository quizAccessRepository;

    @Before
    public void setUp() {
        quizAccessService = new QuizAccessService(quizAccessRepository);
    }

    @Test
    public void testFindAllQuizAccess() {
        List<QuizAccess> quizzes = new ArrayList<>();
        QuizAccess quizAccess1 = new QuizAccess(1, "1");
        QuizAccess quizAccess2 = new QuizAccess(2, "2");

        quizzes.add(quizAccess1);
        quizzes.add(quizAccess2);

        when(quizAccessRepository.findAll()).thenReturn(quizzes);

        assertEquals(quizzes, quizAccessService.findAll());
    }

    @Test
    public void assignQuizToUser() {
        quizAccessService.assignQuizToUser("1", 1);

        verify(quizAccessRepository, times(1)).assignQuizToUser("1", 1);
    }

    @Test
    public void deleteQuizFromUser() {
        quizAccessService.deleteUserFromQuiz("1", 1);

        verify(quizAccessRepository, times(1)).deleteUserFromQuiz("1", 1);
    }

    @Test
    public void testQuizAccessExists() {
        quizAccessService.exists("1", 1);

        verify(quizAccessRepository).exists("1", 1);
    }

}
