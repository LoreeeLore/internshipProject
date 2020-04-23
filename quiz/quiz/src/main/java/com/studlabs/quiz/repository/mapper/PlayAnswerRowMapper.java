package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;

public class PlayAnswerRowMapper implements RowMapper<PlayAnswer> {

    @Override
    public PlayAnswer mapRow(ResultSet resultSet, int i) throws SQLException {
        PlayAnswer playAnswer = new PlayAnswer();

        playAnswer.setIdPlayAnswer(resultSet.getInt(PlayAnswerRepository.ID));
        playAnswer.setIdPlayQuestion(resultSet.getInt(PlayAnswerRepository.ID_PLAY_QUESTION));
        playAnswer.setIdAnswer(resultSet.getInt(PlayAnswerRepository.ID_ANSWER));
        playAnswer.setText(resultSet.getString(PlayAnswerRepository.TEXT));

        return playAnswer;
    }
}
