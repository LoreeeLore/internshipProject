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
public class QuestionServiceTest {

    private QuestionService questionService;

    @Mock
    private QuestionRepository questionRepository;

    @Before
    public void setUp() {
        questionService = new QuestionService(questionRepository);
    }

    @Test
    public void testGetAllQuestions() {
        List<Question> questions = new ArrayList<>();

        Question question1 = new Question(3, "desc1", "cat1", "",
                QuestionDifficulty.EASY, false);
        Question question2 = new Question(4, "desc2", "cat1", "",
                QuestionDifficulty.MODERATE, false);

        questions.add(question1);
        questions.add(question2);

        when(questionRepository.findAll()).thenReturn(questions);

        assertEquals(questions, questionService.findAll());
    }

    @Test
    public void testAddQuestion() {
        Question question = new Question(5, "de", "cat", "",
                QuestionDifficulty.EASY,  false);

        questionService.insert(question);

        verify(questionRepository, times(1)).insert(question);
    }

    @Test
    public void testDeleteQuestion() {
        questionService.delete(1);

        verify(questionRepository).delete(1);
    }

    public void testUpdateQuestion() {
        Question question4 = new Question(5, "deeeeeeeeeeeeeeee", "cat", "", QuestionDifficulty.EASY, false);

        questionService.updateQuestion(question4);

        verify(questionRepository).updateQuestion(question4);
    }

    @Test
    public void testFindQuestionById() {
        Question question6 = new Question(3, "desc1", "cat1", "", QuestionDifficulty.EASY,  false);

        when(questionRepository.findQuestionById(question6.getIdQuestion())).thenReturn(question6);

        Question result = questionService.findQuestionById(question6.getIdQuestion());
        assertEquals(result.getIdQuestion(), question6.getIdQuestion());
    }

    @Test
    public void testSetQuestionDeprecated() {
        questionService.setDeprecated(1, true);

        verify(questionRepository).setDeprecated(1, true);
    }

    @Test
    public void testQuestionExists() {
        questionService.exists(1);

        verify(questionRepository).exists(1);
    }

    @Test
    public void testFindQuestionsByCategory() {
        List<Question> questions = new ArrayList<>();

        Question question1 = new Question(3, "desc1", "cat1", "",
                QuestionDifficulty.EASY, false);
        Question question2 = new Question(4, "desc2", "cat1", "",
                QuestionDifficulty.MODERATE, false);

        questions.add(question1);
        questions.add(question2);

        when(questionRepository.findQuestionsByCategory("cat1")).thenReturn(questions);

        List<Question> result = questionService.findQuestionsByCategory("cat1");
        assertEquals(result, questions);
    }
}
