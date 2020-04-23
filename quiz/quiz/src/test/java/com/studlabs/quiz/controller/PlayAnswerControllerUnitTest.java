package com.studlabs.quiz.controller;

import com.studlabs.quiz.exception.*;
import com.studlabs.quiz.mapper.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.security.*;
import com.studlabs.quiz.service.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;

import static com.studlabs.quiz.util.TestAuthenticationProvider.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PlayAnswerControllerUnitTest {

    @Mock
    private PlayAnswerService playAnswerService;

    @Mock
    private PlayQuestionService playQuestionService;

    private PlayAnswerController playAnswerController;

    private PlayAnswerMapper playAnswerMapper;

    @Before
    public void setUp() {
        playAnswerController = new PlayAnswerController(playAnswerService, playQuestionService);
        playAnswerMapper = new PlayAnswerMapper();
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    @Test
    public void testInsertValid() {
        PlayQuestion playQuestion=new PlayQuestion();
        playQuestion.setEndTime(null);

        when(playQuestionService.exists(1)).thenReturn(true);
        when(playQuestionService.findPlayQuestionById(1)).thenReturn(playQuestion);
        when(playQuestionService.getUser(1)).thenReturn("1");

        playAnswerController.insert(1, null, null);

        verify(playAnswerService, times(1)).insert(null, 1, null);
    }

    @Test
    public void testInsertInvalid() {
        when(playQuestionService.exists(1)).thenReturn(false);

        playAnswerController.insert(1, null, null);

        verify(playAnswerService, times(0)).insert(null, 1, null);
    }

    @Test(expected = DataTransactionException.class)
    public void testInsertInvalidTransactionException() {
        PlayQuestion playQuestion=new PlayQuestion();
        playQuestion.setEndTime(null);

        when(playQuestionService.exists(1)).thenReturn(true);
        when(playQuestionService.findPlayQuestionById(1)).thenReturn(playQuestion);
        when(playQuestionService.getUser(1)).thenReturn("1");

        doThrow(DataTransactionException.class).when(playAnswerService).insert(null, 1, null);

        playAnswerController.insert(1, null, null);
    }

    @Test
    public void testFindAnswerByPlayQuestionIdWhenIdIsValid() {
        PlayQuestion playQuestion = new PlayQuestion();
        PlayAnswer playAnswer = new PlayAnswer();

        when(playQuestionService.exists(1)).thenReturn(true);
        when(playQuestionService.findPlayQuestionById(1)).thenReturn(playQuestion);
        when(playAnswerService.findAnswerByIdPlayQuestion(1)).thenReturn(playAnswer);

        Assert.assertEquals(playAnswerMapper.convertToEntity(playAnswerController.findPlayAnswerByIdPlayQuestion(1).getBody()), playAnswer);
    }

    @Test
    public void testFindAnswerByPlayQuestionIdWhenIdIsInvalid() {
        when(playQuestionService.exists(1)).thenReturn(false);

        playAnswerController.findPlayAnswerByIdPlayQuestion(1);

        verify(playAnswerService, times(0)).findAnswerByIdPlayQuestion(1);
    }
}
