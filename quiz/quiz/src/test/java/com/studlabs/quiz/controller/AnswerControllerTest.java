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
public class AnswerControllerTest {

    private AnswerMapper answerMapper;

    private AnswerController answerController;

    @Mock
    private AnswerService answerService;

    @Before
    public void setUp() {
        answerController = new AnswerController(answerService);
        configureTestAuthentication("1", Roles.ROLE_ADMINISTRATOR);
    }

    public AnswerControllerTest() {
        this.answerMapper = new AnswerMapper();
    }

    @Test
    public void findAllAnswers() {
        List<Answer> answers = new ArrayList<>();

        Answer answer1 = new Answer(3, 1, true, "Europe");
        Answer answer2 = new Answer(4, 1, false, "Asia");
        answers.add(answer1);
        answers.add(answer2);

        when(answerService.findAllAnswers()).thenReturn(answers);

        List<AnswerDto> result = answerController.findAllAnswers().getBody();

        assertNotNull(result);
        assertEquals(result.size(), 2);
        Assert.assertEquals(result.get(0), answerMapper.convertToDTO(answer1));
        Assert.assertEquals(result.get(1), answerMapper.convertToDTO(answer2));
    }

    @Test
    public void findAnswerByIdValid() {
        Answer answer = new Answer(1, 1, true, "Asia");

        when(answerService.exists(answer.getIdAnswer())).thenReturn(true);
        when(answerService.findAnswerById(1)).thenReturn(answer);

        AnswerDto answerResult = answerController.findAnswerById(1).getBody();

        Assert.assertEquals(answerMapper.convertToDTO(answer), answerResult);
    }

    @Test
    public void findAnswerByIdInvalid() {
        when(answerService.exists(1)).thenReturn(false);
        answerController.findAnswerById(1);

        verify(answerService, times(0)).findAnswerById(1);
    }

    @Test
    public void insertAnswer() {
        when(answerService.checkIfTooManyAnswers(1)).thenReturn(true);

        AnswerDto answerDto = new AnswerDto(1, 1, true, "Asia");
        answerController.insertAnswer(answerDto);
        Answer answer = answerMapper.convertToEntity(answerDto);

        ArgumentCaptor<Answer> captor = ArgumentCaptor.forClass(Answer.class);

        verify(answerService).insertAnswer(captor.capture());

        assertEquals(captor.getValue(), answer);
    }

    @Test
    public void insertAnswerTooManyAnswers() {
        when(answerService.checkIfTooManyAnswers(1)).thenReturn(false);

        AnswerDto answerDto = new AnswerDto(1, 1, true, "Asia");
        answerController.insertAnswer(answerDto);

        verify(answerService, times(0)).insertAnswer(new Answer());
    }

    @Test
    public void updateAnswerValidId() {
        AnswerDto answerDto = new AnswerDto(1, 1, false, "Asia");

        when(answerService.exists(1)).thenReturn(true);

        answerController.updateAnswer(1, answerDto);
        Answer answer = answerMapper.convertToEntity(answerDto);

        ArgumentCaptor<Answer> captor = ArgumentCaptor.forClass(Answer.class);

        verify(answerService).updateAnswer(captor.capture());

        assertEquals(captor.getValue(), answer);
    }

    @Test
    public void updateAnswerInvalidId() {
        AnswerDto answerDto = new AnswerDto(1, 1, false, "Asia");

        when(answerService.exists(answerDto.getIdAnswer())).thenReturn(false);

        answerController.updateAnswer(1, answerDto);

        verify(answerService, times(0)).updateAnswer(answerMapper.convertToEntity(answerDto));
    }

    @Test
    public void deleteAnswerValidId() {
        when(answerService.exists(1)).thenReturn(true);

        answerController.deleteAnswer(1);

        verify(answerService).deleteAnswer(1);
    }

    @Test
    public void deleteAnswerInvalidId() {
        when(answerService.exists(1)).thenReturn(false);

        answerController.deleteAnswer(1);

        verify(answerService, times(0)).deleteAnswer(1);
    }
}
