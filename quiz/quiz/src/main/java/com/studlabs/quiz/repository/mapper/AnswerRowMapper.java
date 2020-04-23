package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;

public class AnswerRowMapper implements RowMapper<Answer> {

    @Override
    public Answer mapRow(ResultSet resultSet, int i) throws SQLException {
        Answer answer = new Answer();

        answer.setIdAnswer(resultSet.getInt(AnswerRepository.ID));
        answer.setIdQuestion(resultSet.getInt(AnswerRepository.ID_QUESTION));
        answer.setCorrect(resultSet.getBoolean(AnswerRepository.IS_CORRECT));
        answer.setText(resultSet.getString(AnswerRepository.TEXT));

        return answer;
    }
}
