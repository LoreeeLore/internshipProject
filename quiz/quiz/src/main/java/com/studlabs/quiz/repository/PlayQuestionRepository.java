package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.*;
import org.springframework.stereotype.*;

import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import static com.studlabs.quiz.repository.PlayQuizRepository.*;

@Repository
public class PlayQuestionRepository {

    public static final String ID = "id";
    public static final String ID_QUESTION = "id_question";
    public static final String ID_PLAY_QUIZ = "id_play_quiz";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String IS_CORRECT = "is_correct";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PlayQuestionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public int insert(PlayQuestion playQuestion) {

        String sql = MessageFormat.format("INSERT INTO play_question({0}, {1}) VALUES (:{0}, :{1})",
                ID_QUESTION, ID_PLAY_QUIZ);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(playQuestion), keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public PlayQuestion findPlayQuestionById(int id) {
        String sql = "SELECT * FROM play_question WHERE " + ID + " =:id";

        return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource().addValue(ID, id), new PlayQuestionRowMapper());
    }

    public List<PlayQuestion> findAllQuestionsForAQuiz(int idPlayQuiz) {
        String sql = "SELECT * FROM play_question WHERE " + ID_PLAY_QUIZ + " =:id_play_quiz";

        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID_PLAY_QUIZ, idPlayQuiz), new PlayQuestionRowMapper());
    }

    public boolean exists(int id) {
        String sql = "SELECT * from play_question WHERE " + ID + " =:id";

        List<PlayQuestion> result = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID, id), new PlayQuestionRowMapper());

        return !result.isEmpty();
    }

    private SqlParameterSource getSqlParameterSourceFromModel(PlayQuestion playQuestion) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (playQuestion != null) {

            parameterSource.addValue(ID, playQuestion.getIdPlayQuestion());
            parameterSource.addValue(ID_QUESTION, playQuestion.getIdQuestion());
            parameterSource.addValue(ID_PLAY_QUIZ, playQuestion.getIdPlayQuiz());

        }

        return parameterSource;
    }

    public String getUser(int id) {

        String sql = "SELECT " + ID_USER + " from play_quiz WHERE " + PlayQuizRepository.ID + " IN (SELECT " + ID_PLAY_QUIZ + " FROM play_question WHERE " + PlayQuestionRepository.ID + "=:id)";

        String result = namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource().addValue(PlayQuestionRepository.ID, id), String.class);

        return result;
    }
}
