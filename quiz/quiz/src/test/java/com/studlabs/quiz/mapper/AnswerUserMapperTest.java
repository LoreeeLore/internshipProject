package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;
import org.junit.*;

import static org.junit.Assert.*;

public class AnswerUserMapperTest {

    private final AnswerUserMapper answerUserMapper = new AnswerUserMapper();

    @Test
    public void convertToDTO() {
        AnswerUser answerUser = new AnswerUser(1, "1", 2, 3, 2, "Europe");
        AnswerUserDto answerUserDto = answerUserMapper.convertToDTO(answerUser);

        assertEquals(answerUserDto.getIdAnswerUser(), 1);
        assertEquals(answerUserDto.getIdUser(), "1");
        assertEquals(answerUserDto.getIdQuiz(), 2);
        assertEquals(answerUserDto.getIdQuestion(), 3);
        assertEquals(answerUserDto.getIdAnswer(), 2);
        assertEquals(answerUserDto.getAnswerText(), "Europe");
    }

    @Test
    public void convertToEntity() {
        AnswerUserDto answerUserDto = new AnswerUserDto(1, "1", 2, 3, 2, "Europe");
        AnswerUser answerUser = answerUserMapper.convertToEntity(answerUserDto);

        assertEquals(answerUser.getIdAnswerUser(), 1);
        assertEquals(answerUser.getIdUser(), "1");
        assertEquals(answerUser.getIdQuiz(), 2);
        assertEquals(answerUser.getIdQuestion(), 3);
        assertEquals(answerUser.getIdAnswer(), 2);
        assertEquals(answerUser.getAnswerText(), "Europe");
    }
}
