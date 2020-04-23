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
public class QuestionSuggestionServiceTest {

    private QuestionSuggestionService questionSuggestionService;

    @Mock
    private QuestionSuggestionRepository questionSuggestionRepository;

    @Before
    public void setUp() throws Exception {
        questionSuggestionService = new QuestionSuggestionService(questionSuggestionRepository);
    }

    @Test
    public void exists() {
        when(questionSuggestionRepository.exists(1)).thenReturn(true);

        boolean result = questionSuggestionService.exists(1);
        assertTrue(result);
    }

    @Test
    public void getAll() {
        List<QuestionSuggestion> questionSuggestions = new ArrayList<>();

        QuestionSuggestion questionSuggestion1 = new QuestionSuggestion("1", "?");
        QuestionSuggestion questionSuggestion2 = new QuestionSuggestion("1", "??");

        questionSuggestions.add(questionSuggestion1);
        questionSuggestions.add(questionSuggestion2);

        when(questionSuggestionRepository.findAll()).thenReturn(questionSuggestions);

        assertEquals(questionSuggestions, questionSuggestionService.getAll());
    }

    @Test
    public void insert() {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion("1", "?");

        questionSuggestionService.insert(questionSuggestion);
        questionSuggestionService.insert(questionSuggestion);

        verify(questionSuggestionRepository, times(2)).insertSuggestion(questionSuggestion);
    }

    @Test
    public void update() {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion("1", "?");

        questionSuggestionService.update(1, questionSuggestion);

        verify(questionSuggestionRepository).update(1, questionSuggestion);
    }

    @Test
    public void delete() {
        questionSuggestionService.delete(1);

        verify(questionSuggestionRepository).delete(1);
    }
}