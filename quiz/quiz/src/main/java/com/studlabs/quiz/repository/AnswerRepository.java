package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class AnswerRepository {

    public static final String ID = "id";
    public static final String ID_QUESTION = "id_question";
    public static final String IS_CORRECT = "is_correct";
    public static final String TEXT = "text";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public AnswerRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<Answer> findAllAnswers() {
        String sql = String.format("SELECT %s, %s, %s, %s FROM answer", ID, ID_QUESTION, IS_CORRECT, TEXT);
        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromModel(null), new AnswerRowMapper());
    }

    public Answer findAnswerById(int answerId) {
        String sql = String.format("SELECT %s, %s, %s, %s FROM answer WHERE %s=%d", ID, ID_QUESTION, IS_CORRECT, TEXT, ID, answerId);
        return namedParameterJdbcTemplate.queryForObject(sql, getSqlParameterSourceFromModel(null), new AnswerRowMapper());
    }

    public List<Answer> findAnswersByIds(List<Integer> answerIds) {
        if (answerIds.isEmpty()) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM answer WHERE " + ID + " IN (:id)";
        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID, answerIds), new AnswerRowMapper());
    }

    public List<Answer> findCorrectAnswersByQuestionId(int questionId) {
        String sql = "SELECT * FROM answer WHERE " + ID_QUESTION + "=:id_question AND " + IS_CORRECT + " is true";
        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID_QUESTION, questionId), new AnswerRowMapper());
    }

    public List<Answer> findAllByQuestionId(int questionId) {
        String sql = String.format("SELECT %s, %s, %s, %s FROM answer WHERE %s=%d", ID, ID_QUESTION, IS_CORRECT, TEXT, ID_QUESTION, questionId);
        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromModel(null), new AnswerRowMapper());
    }

    public int insertAnswer(Answer answer) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = String.format("INSERT INTO answer (%s, %s, %s) VALUES(:%s, :%s, :%s)", ID_QUESTION, IS_CORRECT, TEXT, ID_QUESTION, IS_CORRECT, TEXT);
        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModelWithoutId(answer), keyHolder);

        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public void updateAnswer(Answer answer) {
        String sql = String.format("UPDATE answer SET %s=:%s, %s=:%s, %s=:%s WHERE %s=:%s", ID_QUESTION, ID_QUESTION, IS_CORRECT, IS_CORRECT, TEXT, TEXT, ID, ID);
        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(answer));
    }

    public void deleteAnswer(int answerId) {
        String sql = "DELETE FROM answer WHERE " + ID + " =:id";

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource().addValue(ID, answerId));
    }

    private SqlParameterSource getSqlParameterSourceFromModel(Answer answer) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (answer != null) {
            parameterSource.addValue("id", answer.getIdAnswer());
            parameterSource.addValue("id_question", answer.getIdQuestion());
            parameterSource.addValue("is_correct", answer.isCorrect());
            parameterSource.addValue("text", answer.getText());
        }
        return parameterSource;
    }

    public boolean exists(int id) {
        String sql = "SELECT * from answer WHERE " + ID + " =:id";

        List<Answer> result = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID, id), new AnswerRowMapper());

        return !result.isEmpty();
    }

    private SqlParameterSource getSqlParameterSourceFromModelWithoutId(Answer answer) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (answer != null) {
            parameterSource.addValue("id_question", answer.getIdQuestion());
            parameterSource.addValue("is_correct", answer.isCorrect());
            parameterSource.addValue("text", answer.getText());
        }
        return parameterSource;
    }
}
