package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.time.*;
import java.time.format.*;

public class PlayQuizRowMapper implements RowMapper<PlayQuiz> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.n]");

    @Override
    public PlayQuiz mapRow(ResultSet resultSet, int i) throws SQLException {
        PlayQuiz playQuiz = new PlayQuiz();

        playQuiz.setIdPlayQuiz(resultSet.getInt(PlayQuizRepository.ID));
        playQuiz.setIdUser(resultSet.getString(PlayQuizRepository.ID_USER));
        playQuiz.setIdQuiz(resultSet.getInt(PlayQuizRepository.ID_QUIZ));

        String startTime = resultSet.getString(PlayQuizRepository.START_TIME);
        if (startTime != null) {
            playQuiz.setStartTime(LocalDateTime.parse(startTime, formatter));
        }

        String endTime = resultSet.getString(PlayQuizRepository.END_TIME);
        if (endTime != null) {
            playQuiz.setEndTime(LocalDateTime.parse(endTime, formatter));
        }

        playQuiz.setRate(resultSet.getInt(PlayQuizRepository.RATE));
        String status = resultSet.getString(PlayQuizRepository.STATUS);
        if (status != null) {
            playQuiz.setStatus(PlayQuizStatus.valueOf(status));
        }

        return playQuiz;
    }
}
