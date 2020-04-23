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
public class QuestionCorrectionServiceTest {

    private QuestionCorrectionService questionCorrectionService;

    @Mock
    private QuestionCorrectionRepository questionCorrectionRepository;

    @Before
    public void setUp() {
        questionCorrectionService = new QuestionCorrectionService(questionCorrectionRepository);
    }

    @Test
    public void findAll() {
        List<QuestionCorrection> questionCorrectionList = new ArrayList<>();

        QuestionCorrection questionCorrection1 = new QuestionCorrection("3", 1, "?");
        QuestionCorrection questionCorrection2 = new QuestionCorrection("4", 1, "??");
        questionCorrectionList.add(questionCorrection1);
        questionCorrectionList.add(questionCorrection2);

        when(questionCorrectionRepository.findAll()).thenReturn(questionCorrectionList);

        List<QuestionCorrection> result = questionCorrectionService.findAll();

        assertEquals(2, result.size());
        assertEquals(result.get(0), questionCorrection1);
        assertEquals(result.get(1), questionCorrection2);
    }

    @Test
    public void insertQuestionCorrection() {
        QuestionCorrection questionCorrection = new QuestionCorrection("1", 1, "??");

        questionCorrectionService.insertQuestionCorrection(questionCorrection);

        verify(questionCorrectionRepository).insertQuestionCorrection(questionCorrection);
    }

    @Test
    public void updateQuestionCorrection() {
        QuestionCorrection questionCorrection = new QuestionCorrection("1", 1, "??");

        questionCorrectionService.updateQuestionCorrection(questionCorrection);

        verify(questionCorrectionRepository).updateQuestionCorrection(questionCorrection);
    }

    @Test
    public void deleteQuestionCorrection() {
        questionCorrectionService.deleteQuestionCorrection("1", 1);

        verify(questionCorrectionRepository).deleteQuestionCorrection("1", 1);
    }
}
