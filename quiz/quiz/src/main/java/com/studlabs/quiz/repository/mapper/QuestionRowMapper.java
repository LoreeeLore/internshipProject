package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;

public class QuestionRowMapper implements RowMapper<Question> {

    public Question mapRow(ResultSet resultSet, int i) throws SQLException {
        Question question = new Question();
        question.setIdQuestion(resultSet.getInt(QuestionRepository.ID));
        question.setDescription(resultSet.getString(QuestionRepository.DESCRIPTION));
        question.setImage(resultSet.getString(QuestionRepository.IMAGE));
        question.setCategory(resultSet.getString(QuestionRepository.CATEGORY));

        String difficulty = resultSet.getString(QuestionRepository.DIFFICULTY);
        if (difficulty != null) {
            question.setDifficulty(QuestionDifficulty.valueOf(difficulty));
        }

        question.setDeprecated(resultSet.getBoolean(QuestionRepository.IS_DEPRECATED));

        return question;
    }
}
