package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;

public class QuizRatingRowMapper implements RowMapper<QuizRating> {

    public QuizRating mapRow(ResultSet resultSet, int i) throws SQLException {

        QuizRating quizRating = new QuizRating();
        quizRating.setIdQuiz(resultSet.getInt(QuizRatingRepository.ID_QUIZ));
        quizRating.setIdUser(resultSet.getString(QuizRatingRepository.ID_USER));
        quizRating.setLike(resultSet.getBoolean(QuizRatingRepository.LIKE));

        return quizRating;
    }
}
