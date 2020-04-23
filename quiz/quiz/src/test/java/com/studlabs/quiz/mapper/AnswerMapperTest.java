package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;
import org.junit.*;

import static org.junit.Assert.*;

public class AnswerMapperTest {

    private AnswerMapper answerMapper = new AnswerMapper();

    @Test
    public void convertToDto() {
        Answer answer = new Answer(1, 2, true, "Asia");
        AnswerDto answerDto = answerMapper.convertToDTO(answer);

        assertEquals(answerDto.getIdAnswer(), 1);
        assertEquals(answerDto.getIdQuestion(), 2);
        assertTrue(answerDto.isCorrect());
        assertEquals(answerDto.getText(), "Asia");
    }

    @Test
    public void convertToEntity() {
        AnswerDto answerDto = new AnswerDto(1, 2, true, "Asia");
        Answer answer = answerMapper.convertToEntity(answerDto);

        assertEquals(answer.getIdAnswer(), 1);
        assertEquals(answer.getIdQuestion(), 2);
        assertTrue(answer.isCorrect());
        assertEquals(answer.getText(), "Asia");
    }
}
