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

@RunWith(MockitoJUnitRunner.class)
public class QuestionSuggestionControllerTest {

    private QuestionSuggestionController questionSuggestionController;
    private QuestionSuggestionMapper questionSuggestionMapper = new QuestionSuggestionMapper();

    @Mock
    private QuestionSuggestionService questionSuggestionService;

    @Before
    public void setUp() {
        questionSuggestionController = new QuestionSuggestionController(questionSuggestionService);
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    @Test
    public void getAll() {
        List<QuestionSuggestion> questions = new ArrayList<>();
        QuestionSuggestion questionSuggestion1 = new QuestionSuggestion("3", "desc1");
        QuestionSuggestion questionSuggestion2 = new QuestionSuggestion("4", "desc2");

        questions.add(questionSuggestion1);
        questions.add(questionSuggestion2);

        when(questionSuggestionService.getAll()).thenReturn(questions);

        List<QuestionSuggestionDto> questionDtos = questionSuggestionController.getAll().getBody();
        List<QuestionSuggestion> resultQuestions = new ArrayList<>();

        assertNotNull(questionDtos);
        for (QuestionSuggestionDto questionDto : questionDtos) {
            resultQuestions.add(questionSuggestionMapper.convertToEntity(questionDto));
        }

        assertEquals(resultQuestions, questions);
    }

    @Test
    public void insertQuestionSuggestion() {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion("1", "?");

        questionSuggestionController.insertQuestionSuggestion(questionSuggestionMapper.convertToDTO(questionSuggestion));

        verify(questionSuggestionService, times(1)).insert(questionSuggestion);
    }

    @Test
    public void updateQuestionSuggestionWhenNotFound() {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion("1", "?");

        when(questionSuggestionService.exists(1)).thenReturn(false);
        questionSuggestionController.updateQuestionSuggestion(1, questionSuggestionMapper.convertToDTO(questionSuggestion));

        verify(questionSuggestionService, times(0)).update(1, questionSuggestion);
    }

    @Test
    public void updateQuestionSuggestionWhenOk() {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion("1", "?");

        when(questionSuggestionService.exists(1)).thenReturn(true);
        questionSuggestionController.updateQuestionSuggestion(1, questionSuggestionMapper.convertToDTO(questionSuggestion));

        verify(questionSuggestionService).update(1, questionSuggestion);
    }

    @Test
    public void deleteQuestionSuggestionWhenNotFound() {
        when(questionSuggestionService.exists(1)).thenReturn(false);
        questionSuggestionController.deleteQuestionSuggestion(1);

        verify(questionSuggestionService, times(0)).delete(1);
    }

    @Test
    public void deleteQuestionSuggestionWhenOk() {
        when(questionSuggestionService.exists(1)).thenReturn(true);
        questionSuggestionController.deleteQuestionSuggestion(1);

        verify(questionSuggestionService).delete(1);
    }
}