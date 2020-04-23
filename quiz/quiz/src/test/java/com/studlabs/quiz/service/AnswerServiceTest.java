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
public class AnswerServiceTest {

    private AnswerService answerService;

    @Mock
    private AnswerRepository answerRepository;

    @Before
    public void setUp() {
        answerService = new AnswerService(answerRepository);
    }

    @Test
    public void findAllAnswers() {
        List<Answer> answers = new ArrayList<>();

        Answer answer1 = new Answer(3, 1, true, "Europe");
        Answer answer2 = new Answer(4, 1, false, "Asia");
        answers.add(answer1);
        answers.add(answer2);

        when(answerRepository.findAllAnswers()).thenReturn(answers);

        List<Answer> result = answerService.findAllAnswers();

        assertEquals(result.get(0), answer1);
        assertEquals(result.get(1), answer2);
    }

    @Test
    public void findAnswerById() {
        Answer answer = new Answer(1, 1, true, "Asia");

        when(answerRepository.findAnswerById(1)).thenReturn(answer);
        Answer answerResult = answerService.findAnswerById(1);

        assertEquals(answer, answerResult);
    }

    @Test
    public void insertAnswer() {
        Answer answer = new Answer(1, 1, true, "Asia");
        answerService.insertAnswer(answer);

        verify(answerRepository).insertAnswer(answer);
    }

    @Test
    public void testCheckIfTooManyAnswers() {
        List<Answer> answers = new ArrayList<>();

        when(answerRepository.findAllByQuestionId(1)).thenReturn(answers);

        assertTrue(answerService.checkIfTooManyAnswers(1));

        verify(answerRepository).findAllByQuestionId(1);
    }

    @Test
    public void updateAnswer() {
        Answer answer = new Answer(1, 1, false, "Asia");
        answerService.updateAnswer(answer);

        verify(answerRepository).updateAnswer(answer);
    }

    @Test
    public void deleteAnswer() {
        answerService.deleteAnswer(1);

        verify(answerRepository).deleteAnswer(1);
    }

    @Test
    public void testAnswerExists() {
        answerService.exists(1);

        verify(answerRepository).exists(1);
    }
}
