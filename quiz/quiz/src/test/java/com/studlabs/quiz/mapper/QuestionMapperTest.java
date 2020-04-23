package com.studlabs.quiz.mapper;

import com.studlabs.quiz.dto.*;
import com.studlabs.quiz.model.*;
import org.junit.*;

import static org.junit.Assert.*;

public class QuestionMapperTest {

    private QuestionMapper questionMapper = new QuestionMapper();

    @Test
    public void convertToDTO() {
        Question question = new Question(1, "Which is the widest continent?", "Geography", null, QuestionDifficulty.EASY, false);
        QuestionDto questionDto = questionMapper.convertToDTO(question);

        assertEquals(questionDto.getIdQuestion(), 1);
        assertEquals(questionDto.getDescription(), "Which is the widest continent?");
        assertEquals(questionDto.getCategory(), "Geography");
        assertNull(questionDto.getImage());
        assertFalse(questionDto.isDeprecated());
    }

    @Test
    public void convertToEntity() {
        QuestionDto questionDto = new QuestionDto(1, "Which is the widest continent?", "Geography", null, QuestionDifficulty.EASY, false);
        Question question = questionMapper.convertToEntity(questionDto);

        assertEquals(question.getIdQuestion(), 1);
        assertEquals(question.getDescription(), "Which is the widest continent?");
        assertEquals(question.getCategory(), "Geography");
        assertNull(question.getImage());
        assertFalse(question.isDeprecated());
    }
}
