package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.time.*;

public class QuestionSuggestionRowMapper implements RowMapper<QuestionSuggestion> {

    @Override
    public QuestionSuggestion mapRow(ResultSet resultSet, int i) throws SQLException {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion();

        questionSuggestion.setIdQuestionSuggestion(resultSet.getInt(QuestionSuggestionRepository.ID_SUGGESTION));
        questionSuggestion.setIdUser(resultSet.getString(QuestionSuggestionRepository.ID_USER));
        questionSuggestion.setDescription(resultSet.getString(QuestionSuggestionRepository.DESCRIPTION));
        questionSuggestion.setImage(resultSet.getString(QuestionSuggestionRepository.IMAGE));
        questionSuggestion.setCategory(resultSet.getString(QuestionSuggestionRepository.CATEGORY));

        String difficulty = resultSet.getString(QuestionSuggestionRepository.DIFFICULTY);
        if (difficulty != null) {
            questionSuggestion.setDifficulty(QuestionDifficulty.valueOf(difficulty));
        }

        questionSuggestion.setType(resultSet.getString(QuestionSuggestionRepository.TYPE));

        return questionSuggestion;
    }
}
