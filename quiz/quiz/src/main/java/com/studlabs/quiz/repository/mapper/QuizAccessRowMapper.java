package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;

public class QuizAccessRowMapper implements RowMapper<QuizAccess> {

    @Override
    public QuizAccess mapRow(ResultSet resultSet, int i) throws SQLException {
        QuizAccess quizAccess = new QuizAccess();

        quizAccess.setIdQuiz(resultSet.getInt(QuizAccessRepository.ID_QUIZ));
        quizAccess.setIdUser(resultSet.getString(QuizAccessRepository.ID_USER));

        return quizAccess;
    }
}
