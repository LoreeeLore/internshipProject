package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;
import java.time.*;
import java.time.format.*;

public class PlayQuestionRowMapper implements RowMapper<PlayQuestion> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.n]");

    @Override
    public PlayQuestion mapRow(ResultSet resultSet, int i) throws SQLException {
        PlayQuestion playQuestion = new PlayQuestion();
        playQuestion.setIdPlayQuestion(resultSet.getInt(PlayQuestionRepository.ID));
        playQuestion.setIdQuestion(resultSet.getInt(PlayQuestionRepository.ID_QUESTION));
        playQuestion.setIdPlayQuiz(resultSet.getInt(PlayQuestionRepository.ID_PLAY_QUIZ));

        String startTime = resultSet.getString(PlayQuestionRepository.START_TIME);
        if (startTime != null) {
            playQuestion.setStartTime(LocalDateTime.parse(startTime, formatter));
        }

        String endTime = resultSet.getString(PlayQuestionRepository.END_TIME);
        if (endTime != null) {
            playQuestion.setEndTime(LocalDateTime.parse(endTime, formatter));
        }

        playQuestion.setCorrect(resultSet.getBoolean(PlayQuestionRepository.IS_CORRECT));

        return playQuestion;
    }
}
