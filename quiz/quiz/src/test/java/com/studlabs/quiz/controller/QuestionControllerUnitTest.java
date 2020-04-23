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
public class QuestionControllerUnitTest {

    @Mock
    private QuestionService questionService;

    private QuestionController questionController;

    private QuestionMapper questionMapper = new QuestionMapper();

    @Before
    public void setUp() {
        questionController = new QuestionController(questionService);
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    @Test
    public void testGetAllQuestions() {
        List<Question> questions = new ArrayList<>();
        Question question1 = new Question(3, "desc1", "cat1", "", QuestionDifficulty.EASY,  false);
        Question question2 = new Question(4, "desc2", "cat1", "", QuestionDifficulty.MODERATE,  false);

        questions.add(question1);
        questions.add(question2);

        when(questionService.findAll()).thenReturn(questions);

        List<QuestionDto> questionDtos = questionController.listAllQuestions().getBody();
        List<Question> resultQuestions = new ArrayList<>();

        if (questionDtos != null) {
            for (QuestionDto questionDto : questionDtos) {
                resultQuestions.add(questionMapper.convertToEntity(questionDto));
            }
        }

        assertEquals(resultQuestions, questions);
    }

    @Test
    public void testAddQuestion() {
        Question question = new Question(0, "de", "cat", "", QuestionDifficulty.EASY,  false);

        questionController.insertQuestion(questionMapper.convertToDTO(question));

        verify(questionService, times(1)).insert(question);
    }

    @Test
    public void testDeleteQuestionValidId() {
        when(questionService.exists(1)).thenReturn(true);

        questionController.delete(1);

        verify(questionService).delete(1);
    }

    @Test
    public void testDeleteQuestionInvalidId() {
        when(questionService.exists(1)).thenReturn(false);

        questionController.delete(1);

        verify(questionService, times(0)).delete(1);
    }

    @Test
    public void testUpdateQuestionValidId() {
        Question question = new Question(3, "description", "cat", "", QuestionDifficulty.EASY, false);

        when(questionService.exists(question.getIdQuestion())).thenReturn(true);
        questionController.update(question.getIdQuestion(), questionMapper.convertToDTO(question));

        verify(questionService).updateQuestion(question);
    }

    @Test
    public void testUpdateQuestionInvalidId() {
        Question question = new Question(3, "description", "cat", "", QuestionDifficulty.EASY,  false);

        when(questionService.exists(question.getIdQuestion())).thenReturn(false);
        questionController.update(question.getIdQuestion(), questionMapper.convertToDTO(question));

        verify(questionService, times(0)).updateQuestion(question);
    }

    @Test
    public void testFindQuestionByIdValid() {
        Question question = new Question(3, "desc1", "cat1", "", QuestionDifficulty.EASY,  false);

        when(questionService.exists(question.getIdQuestion())).thenReturn(true);
        when(questionService.findQuestionById(question.getIdQuestion())).thenReturn(question);

        Assert.assertEquals(questionMapper.convertToEntity(questionController.findQuestionById(question.getIdQuestion()).getBody()), question);
    }

    @Test
    public void testFindQuestionByIdInvalid() {
        when(questionService.exists(1)).thenReturn(false);

        questionController.findQuestionById(1);

        verify(questionService, times(0)).findQuestionById(1);
    }

    @Test
    public void testSetDeprecatedQuestionExists() {
        when(questionService.exists(1)).thenReturn(true);

        questionController.setDeprecated(1, true);

        verify(questionService, times(1)).exists(1);
        verify(questionService, times(1)).setDeprecated(1, true);
    }

    @Test
    public void testSetDeprecatedQuestionDoesNotExist() {
        when(questionService.exists(1)).thenReturn(false);

        questionController.setDeprecated(1, true);

        verify(questionService, times(1)).exists(1);
        verify(questionService, times(0)).setDeprecated(1, true);
    }
}