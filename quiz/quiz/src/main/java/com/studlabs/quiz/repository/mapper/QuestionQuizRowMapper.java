package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;

public class QuestionQuizRowMapper implements RowMapper<QuestionQuiz> {

    @Override
    public QuestionQuiz mapRow(ResultSet resultSet, int i) throws SQLException {
        QuestionQuiz questionQuiz = new QuestionQuiz();

        questionQuiz.setIdQuiz(resultSet.getInt(QuestionQuizRepository.ID_QUIZ));
        questionQuiz.setIdQuestion(resultSet.getInt(QuestionQuizRepository.ID_QUESTION));

        return questionQuiz;
    }
}
