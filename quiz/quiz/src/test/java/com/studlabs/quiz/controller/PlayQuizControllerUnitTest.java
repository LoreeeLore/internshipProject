package com.studlabs.quiz.controller;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.exception.*;
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
public class PlayQuizControllerUnitTest {

    @Mock
    private PlayQuizService playQuizService;

    @Mock
    private QuizService quizService;

    private PlayQuizController playQuizController;

    private PlayQuizMapper playQuizMapper = new PlayQuizMapper();

    @Before
    public void setUp() {
        playQuizController = new PlayQuizController(playQuizService, quizService);
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    @Test
    public void testInsertPlayQuizValid() {
        when(quizService.exists(1)).thenReturn(true);
        when(playQuizService.hasAccess("1", 1)).thenReturn(true);

        playQuizController.insert(1);

        verify(playQuizService, times(1)).insert("1", 1);
    }

    @Test(expected = DataTransactionException.class)
    public void testInsertPlayQuizTransactionException() {
        when(quizService.exists(1)).thenReturn(true);
        when(playQuizService.hasAccess("1", 1)).thenReturn(true);

        doThrow(DataTransactionException.class).when(playQuizService).insert("1", 1);

        playQuizController.insert(1);
    }

    @Test
    public void testInsertPlayQuizInvalidQuiz() {
        when(quizService.exists(1)).thenReturn(false);

        playQuizController.insert(1);

        verify(playQuizService, times(0)).insert("1", 1);
    }

    @Test
    public void testInsertPlayQuizInvalidAccess() {
        when(quizService.exists(1)).thenReturn(true);
        when(playQuizService.hasAccess("1", 1)).thenReturn(false);

        playQuizController.insert(1);

        verify(playQuizService, times(0)).insert("1", 1);
    }

    @Test
    public void testFindPlayQuizByIdValid() {
        PlayQuiz playQuiz = new PlayQuiz();

        when(playQuizService.exists(1)).thenReturn(true);
        when(playQuizService.findById(1)).thenReturn(playQuiz);

        Assert.assertEquals(playQuizMapper.convertToEntity(playQuizController.findById(1).getBody()), playQuiz);
    }

    @Test
    public void testFindPlayQuizByIdInvalid() {
        when(playQuizService.exists(1)).thenReturn(false);

        playQuizController.findById(1);

        verify(playQuizService, times(0)).findById(1);
    }

    @Test
    public void testFindByUserIdAndQuizId() {
        List<PlayQuiz> playQuizzes = new ArrayList<>();
        PlayQuiz playQuiz1 = new PlayQuiz(1, 1, "1", null, null, 0, PlayQuizStatus.IN_PROGRESS);
        PlayQuiz playQuiz2 = new PlayQuiz(2, 1, "1", null, null, 0, PlayQuizStatus.IN_PROGRESS);

        playQuizzes.add(playQuiz1);
        playQuizzes.add(playQuiz2);

        when(playQuizService.findAllByUserIdAndQuizId("1", 1)).thenReturn(playQuizzes);

        List<PlayQuizDto> playQuizDtos = playQuizController.findByUserIdAndQuizId(1).getBody();
        List<PlayQuiz> resultQuizzes = new ArrayList<>();

        if (playQuizDtos != null) {
            for (PlayQuizDto playQuizDto : playQuizDtos) {
                resultQuizzes.add(playQuizMapper.convertToEntity(playQuizDto));
            }
        }

        assertEquals(playQuizzes.size(), resultQuizzes.size());
        assertEquals(playQuizzes.get(0).getIdPlayQuiz(), resultQuizzes.get(0).getIdPlayQuiz());
    }

    @Test
    public void testGetAllQuizzesForAUser() {
        List<PlayQuiz> playQuizzes = new ArrayList<>();

        PlayQuiz playQuiz1 = new PlayQuiz(1, "1");
        PlayQuiz playQuiz2 = new PlayQuiz(2, "1");

        playQuizzes.add(playQuiz1);
        playQuizzes.add(playQuiz2);

        when(playQuizService.findAllQuizzesForAUser("1")).thenReturn(playQuizzes);

        List<PlayQuizDto> playQuizDtos = playQuizController.findAllQuizzesForAUser().getBody();
        List<PlayQuiz> resultQuizzes = new ArrayList<>();

        if (playQuizDtos != null) {
            for (PlayQuizDto playQuizDto : playQuizDtos) {
                resultQuizzes.add(playQuizMapper.convertToEntity(playQuizDto));
            }
        }

        assertEquals(resultQuizzes, playQuizzes);
    }

    @Test
    public void testFinishQuizWhenExist() {
        when(playQuizService.exists(1)).thenReturn(true);
        when(playQuizService.getUser(1)).thenReturn("1");

        playQuizController.finishQuiz(1);

        verify(playQuizService, times(1)).exists(1);
        verify(playQuizService, times((1))).finishQuiz(1);
    }

    @Test
    public void testFinishQuizWhenDoesNotExist() {
        when(playQuizService.exists(1)).thenReturn(false);

        playQuizController.finishQuiz(1);

        verify(playQuizService, times(1)).exists(1);
        verify(playQuizService, times((0))).finishQuiz(1);

    }

    @Test
    public void testUpdatePlayQuizValid() {
        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setIdPlayQuiz(1);

        when(playQuizService.exists(1)).thenReturn(true);
        when(playQuizService.getUser(1)).thenReturn("1");
        when(playQuizService.findById(1)).thenReturn(playQuiz);

        playQuizController.updateStartTime(1);

        verify(playQuizService, Mockito.times(1)).updateStartTime(1);
    }

    @Test
    public void testUpdatePlayQuestionInvalid() {
        when(playQuizService.exists(1)).thenReturn(false);

        playQuizController.updateStartTime(1);

        verify(playQuizService, Mockito.times(0)).updateStartTime(1);
    }


}
