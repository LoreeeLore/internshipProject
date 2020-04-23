package com.studlabs.quiz.service;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;

import java.time.*;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PlayQuestionServiceTest {

    private PlayQuestionService playQuestionService;

    @Mock
    private PlayQuestionRepository playQuestionRepository;

    @Before
    public void setUp() {
        playQuestionService = new PlayQuestionService(playQuestionRepository);
    }

    @Test
    public void testAddPlayQuestion() {
        PlayQuestion playQuestion = new PlayQuestion(1, 1, LocalDateTime.now(), LocalDateTime.now().minusMinutes(1), true);

        when(playQuestionRepository.insert(playQuestion)).thenReturn(1);

        int id = playQuestionService.insert(playQuestion);

        assertEquals(1, id);
    }

    @Test
    public void testFindPlayQuestionById() {
        PlayQuestion playQuestion = new PlayQuestion();
        playQuestion.setIdPlayQuestion(1);

        when(playQuestionRepository.findPlayQuestionById(1)).thenReturn(playQuestion);

        Assert.assertEquals(playQuestion, playQuestionService.findPlayQuestionById(playQuestion.getIdPlayQuestion()));
    }

    @Test
    public void testExistsPlayQuiz() {
        when(playQuestionRepository.exists(1)).thenReturn(true);

        assertTrue(playQuestionRepository.exists(1));

        verify(playQuestionRepository).exists(1);
    }

    @Test
    public void testFindAllQuestionsForQuiz() {
        List<PlayQuestion> playQuestions = new ArrayList<>();

        PlayQuestion playQuestion1 = new PlayQuestion();
        PlayQuestion playQuestion2 = new PlayQuestion();

        playQuestions.add(playQuestion1);
        playQuestions.add(playQuestion2);

        when(playQuestionRepository.findAllQuestionsForAQuiz(1)).thenReturn(playQuestions);

        assertEquals(playQuestions, playQuestionService.findAllQuestionsForAQuiz(1));
    }

}
