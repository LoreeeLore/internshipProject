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
public class PlayQuestionControllerUnitTest {

    @Mock
    private PlayQuestionService playQuestionService;

    @Mock
    private PlayQuizService playQuizService;

    private PlayQuestionController playQuestionController;

    private PlayQuestionMapper playQuestionMapper = new PlayQuestionMapper();

    @Before
    public void setUp() {
        playQuestionController = new PlayQuestionController(playQuestionService, playQuizService);
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    @Test
    public void testFindPlayQuestionByIdValid() {
        PlayQuestion playQuestion = new PlayQuestion();

        when(playQuestionService.exists(playQuestion.getIdPlayQuestion())).thenReturn(true);
        when(playQuestionService.findPlayQuestionById(1)).thenReturn(playQuestion);

        playQuestionController.findPlayQuestionById(1);

        Assert.assertEquals(playQuestionService.findPlayQuestionById(1), playQuestion);
    }

    @Test
    public void testFindPlayQuestionByIdInvalid() {
        when(playQuestionService.exists(1)).thenReturn(false);

        playQuestionController.findPlayQuestionById(1);

        assertFalse(playQuestionService.exists(1));
    }

    @Test
    public void testFindAllQuestionsForQuiz() {
        List<PlayQuestion> playQuestions = new ArrayList<>();

        PlayQuestion playQuestion1 = new PlayQuestion();
        PlayQuestion playQuestion2 = new PlayQuestion();

        playQuestions.add(playQuestion1);
        playQuestions.add(playQuestion2);

        when(playQuestionService.findAllQuestionsForAQuiz(1)).thenReturn(playQuestions);

        List<PlayQuestionDto> playQuestionDtos = playQuestionController.findAllQuestionsForQuiz(1).getBody();
        List<PlayQuestion> resultQuestions = new ArrayList<>();

        if (playQuestionDtos != null) {
            for (PlayQuestionDto playQuestionDto : playQuestionDtos) {
                resultQuestions.add(playQuestionMapper.convertToEntity(playQuestionDto));
            }
        }

        assertEquals(resultQuestions, playQuestions);
    }

}
