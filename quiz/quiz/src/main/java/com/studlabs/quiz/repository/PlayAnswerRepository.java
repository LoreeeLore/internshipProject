package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.*;
import org.springframework.stereotype.*;

import java.text.*;
import java.util.*;

@Repository
public class PlayAnswerRepository {

    public static final String ID = "id";
    public static final String ID_PLAY_QUESTION = "id_play_question";
    public static final String ID_ANSWER = "id_answer";
    public static final String TEXT = "text";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public PlayAnswerRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public int insert(PlayAnswer playAnswer) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = MessageFormat.format("INSERT INTO play_answer({0},{1},{2}) VALUES (:{0},:{1},:{2})",
                ID_PLAY_QUESTION, ID_ANSWER, TEXT);

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(playAnswer));

        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public List<PlayAnswer> findAllByPlayQuestionId(int playQuestionId) {
        String sql = String.format("SELECT * FROM play_answer WHERE %s=%d", ID_PLAY_QUESTION, playQuestionId);
        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromModel(null), new PlayAnswerRowMapper());
    }

    public PlayAnswer findAnswerByIdPlayQuestion(int idPlayQuestion) {
        String sql = "SELECT * FROM play_answer WHERE " + ID_PLAY_QUESTION + "=:id_play_question";

        return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource().addValue(ID_PLAY_QUESTION, idPlayQuestion), new PlayAnswerRowMapper());
    }

    private SqlParameterSource getSqlParameterSourceFromModel(PlayAnswer playAnswer) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (playAnswer != null) {
            parameterSource.addValue(ID, playAnswer.getIdPlayAnswer());
            parameterSource.addValue(ID_PLAY_QUESTION, playAnswer.getIdPlayQuestion());
            parameterSource.addValue(ID_ANSWER, playAnswer.getIdAnswer());
            parameterSource.addValue(TEXT, playAnswer.getText());
        }
        return parameterSource;
    }
}
