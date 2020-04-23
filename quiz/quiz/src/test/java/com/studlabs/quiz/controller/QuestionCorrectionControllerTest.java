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
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QuestionCorrectionControllerTest {

    private QuestionCorrectionController questionCorrectionController;
    private QuestionCorrectionMapper questionCorrectionMapper;

    @Mock
    private QuestionCorrectionService questionCorrectionService;

    @Before
    public void setUp() {
        questionCorrectionController = new QuestionCorrectionController(questionCorrectionService);
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    public QuestionCorrectionControllerTest() {
        questionCorrectionMapper = new QuestionCorrectionMapper();
    }

    @Test
    public void findAll() {
        List<QuestionCorrection> questionCorrectionList1 = new ArrayList<>();

        QuestionCorrection questionCorrection1 = new QuestionCorrection("3", 1, "?");
        QuestionCorrection questionCorrection2 = new QuestionCorrection("4", 1, "??");

        questionCorrectionList1.add(questionCorrection1);
        questionCorrectionList1.add(questionCorrection2);

        when(questionCorrectionService.findAll()).thenReturn(questionCorrectionList1);

        List<QuestionCorrection> questionCorrectionList2 = new ArrayList<>();
        List<QuestionCorrectionDto> result = questionCorrectionController.findAll().getBody();

        assert result != null;
        for (QuestionCorrectionDto questionCorrectionDto : result) {
            questionCorrectionList2.add(questionCorrectionMapper.convertToEntity(questionCorrectionDto));
        }

        assertNotNull(questionCorrectionList2);
        assertEquals(questionCorrectionList1.get(0), questionCorrectionList2.get(0));
        assertEquals(questionCorrectionList1.get(1), questionCorrectionList2.get(1));
        assertEquals(2, questionCorrectionList2.size());
    }

    @Test
    public void insertQuestionCorrectionWhenCreated() {
        QuestionCorrectionDto questionCorrectionDto = new QuestionCorrectionDto("1", 1, "??");

        when(questionCorrectionService.exists("1", 1)).thenReturn(false);
        questionCorrectionController.insertQuestionCorrection(questionCorrectionDto);

        verify(questionCorrectionService).insertQuestionCorrection(questionCorrectionMapper.convertToEntity(questionCorrectionDto));
    }

    @Test
    public void insertQuestionCorrectionWhenBadRequest() {
        QuestionCorrectionDto questionCorrectionDto = new QuestionCorrectionDto("1", 1, "??");

        when(questionCorrectionService.exists("1", 1)).thenReturn(true);
        questionCorrectionController.insertQuestionCorrection(questionCorrectionDto);

        verify(questionCorrectionService, times(0)).insertQuestionCorrection(questionCorrectionMapper.convertToEntity(questionCorrectionDto));
    }

    @Test
    public void updateQuestionCorrectionWhenOk() {
        QuestionCorrectionDto questionCorrectionDto = new QuestionCorrectionDto("1", 1, "??");

        when(questionCorrectionService.exists("1", 1)).thenReturn(true);
        questionCorrectionController.updateQuestionCorrection(1, questionCorrectionDto);

        verify(questionCorrectionService).updateQuestionCorrection(questionCorrectionMapper.convertToEntity(questionCorrectionDto));
    }

    @Test
    public void updateQuestionCorrectionWhenNotFound() {
        QuestionCorrectionDto questionCorrectionDto = new QuestionCorrectionDto("1", 1, "??");

        when(questionCorrectionService.exists("1", 1)).thenReturn(false);
        questionCorrectionController.updateQuestionCorrection(1, questionCorrectionDto);

        verify(questionCorrectionService, times(0)).updateQuestionCorrection(questionCorrectionMapper.convertToEntity(questionCorrectionDto));
    }

    @Test
    public void deleteQuestionCorrectionWhenOk() {
        when(questionCorrectionService.exists("1", 1)).thenReturn(true);
        questionCorrectionController.deleteQuestionCorrection(1);

        verify(questionCorrectionService).deleteQuestionCorrection("1", 1);
    }

    @Test
    public void deleteQuestionCorrectionWhenNotFound() {
        when(questionCorrectionService.exists("1", 1)).thenReturn(false);
        questionCorrectionController.deleteQuestionCorrection(1);

        verify(questionCorrectionService, times(0)).deleteQuestionCorrection("1", 1);
    }
}
