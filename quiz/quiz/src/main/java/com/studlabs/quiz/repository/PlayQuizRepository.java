package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.time.format.*;
import java.util.*;

@Repository
public class PlayQuizRepository {

    public static final String ID = "id";
    public static final String ID_USER = "id_user";
    public static final String ID_QUIZ = "id_quiz";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String RATE = "rate";
    public static final String STATUS = "status";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public PlayQuizRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<PlayQuiz> findAllByUserIdAndQuizId(String idUser, int idQuiz) {
        String sql = "SELECT * FROM play_quiz WHERE " + ID_USER + " =:id_user AND " + ID_QUIZ + " =:id_quiz";

        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromParameters(idUser, idQuiz), new PlayQuizRowMapper());
    }

    public PlayQuiz findById(int id) {
        String sql = "SELECT * from play_quiz WHERE " + ID + "=:id";

        return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource().addValue(ID, id), new PlayQuizRowMapper());
    }

    public void updateStartTime(int idPlayQuiz) {
        String sql = "UPDATE play_quiz SET " + START_TIME + "=:start_time WHERE " + ID + " =:id";

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource().addValue(START_TIME, LocalDateTime.now().format(formatter)).addValue(ID, idPlayQuiz));
    }

    public void updateEndTime(int idPlayQuiz) {
        String sql = String.format("UPDATE play_quiz SET %s=:end_time WHERE id=%d", END_TIME, idPlayQuiz);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue(END_TIME, LocalDateTime.now().format(formatter)));
    }

    public boolean exists(int id) {
        String sql = "SELECT * from play_quiz WHERE " + ID + " =:id";

        List<PlayQuiz> result = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID, id), new PlayQuizRowMapper());

        return !result.isEmpty();
    }

    public int insert(String idUser, int idQuiz) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO play_quiz(" + ID_USER + "," + ID_QUIZ + "," + START_TIME + "," + RATE + "," + STATUS + ") " +
                "VALUES(:id_user, :id_quiz, :start_time, :rate, :status)";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromParameters(idUser, idQuiz), keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public void updateQuizStatus(int id, PlayQuizStatus status, double completionRate) {
        String sql = "UPDATE play_quiz SET " + STATUS + "=:status ," + END_TIME + "=:end_time, " + RATE + "=:rate " + " WHERE " + ID + " =:id";
        namedParameterJdbcTemplate.update(sql, getSqlParameterByParameter(id, status, completionRate));
    }

    public List<PlayQuiz> findAllQuizzesForAUser(String idUser) {
        String sql = "SELECT * from play_quiz WHERE " + ID_USER + "=:id_user";

        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID_USER, idUser), new PlayQuizRowMapper());
    }

    public void updateRate(int playQuizId, double rate) {
        String sql = String.format("UPDATE play_quiz SET %s=:rate  WHERE id=%d", RATE, playQuizId);

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource().addValue(RATE, rate));
    }

    private SqlParameterSource getSqlParameterSourceFromParameters(String idUser, int idQuiz) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue(ID_USER, idUser);
        parameterSource.addValue(ID_QUIZ, idQuiz);
        parameterSource.addValue(START_TIME, LocalDateTime.now().format(PlayQuestionRepository.formatter));
        parameterSource.addValue(RATE, 0);
        parameterSource.addValue(STATUS, PlayQuizStatus.IN_PROGRESS.name());

        return parameterSource;
    }

    private SqlParameterSource getSqlParameterByParameter(int idPlayQuiz, PlayQuizStatus status, double rate) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        parameterSource.addValue(ID, idPlayQuiz);
        parameterSource.addValue(END_TIME, LocalDateTime.now().format(formatter));
        parameterSource.addValue(STATUS, status.name());
        parameterSource.addValue(RATE, rate);
        return parameterSource;
    }

    public String getUser(int id) {
        System.out.println("Id " + id);
        String sql = "SELECT " + ID_USER + " from play_quiz WHERE " + ID + "=:id";

        String result = namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource().addValue(ID, id), String.class);

        return result;
    }
}
