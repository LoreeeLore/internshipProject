package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;

public class QuestionRatingRowMapper implements RowMapper<QuestionRating> {

    public QuestionRating mapRow(ResultSet resultSet, int i) throws SQLException {

        QuestionRating questionRating = new QuestionRating();

        questionRating.setIdQuestion(resultSet.getInt(QuestionRatingRepository.ID_QUESTION));
        questionRating.setIdUser(resultSet.getString(QuestionRatingRepository.ID_USER));
        questionRating.setLike(resultSet.getBoolean(QuestionRatingRepository.RATE));

        return questionRating;
    }
}
