package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;

public class QuizRowMapper implements RowMapper<Quiz> {

    @Override
    public Quiz mapRow(ResultSet resultSet, int i) throws SQLException {
        Quiz quiz = new Quiz();

        quiz.setIdQuiz(resultSet.getInt(QuizRepository.ID));
        quiz.setCategory(resultSet.getString(QuizRepository.CATEGORY));
        String difficulty = resultSet.getString(QuizRepository.DIFFICULTY);

        if (difficulty != null) {
            quiz.setDifficulty(QuizDifficulty.valueOf(resultSet.getString(QuizRepository.DIFFICULTY)));
        }

        quiz.setPublic(resultSet.getBoolean(QuizRepository.IS_PUBLIC));
        quiz.setTimeInMinutes(resultSet.getInt(QuizRepository.TIME));
        quiz.setCompletionRate(resultSet.getDouble(QuizRepository.RATE));
        quiz.setRandom(resultSet.getBoolean(QuizRepository.IS_RANDOM));

        return quiz;
    }


}
