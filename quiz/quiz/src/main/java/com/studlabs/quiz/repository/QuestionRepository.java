package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class QuestionRepository {

    public static final String ID = "id";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String CATEGORY = "category";
    public static final String DIFFICULTY = "difficulty";
    public static final String IS_DEPRECATED = "is_deprecated";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public QuestionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<Question> findAll() {
        String sql = "SELECT * FROM question";

        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromModel(null), new QuestionRowMapper());
    }

    public List<Question> findQuestionsByCategory(String category) {
        String sql = "SELECT * FROM question WHERE " + CATEGORY + " =:category";

        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(CATEGORY, category), new QuestionRowMapper());
    }

    public int insert(Question question) {

        String sql = "INSERT INTO question(" + DESCRIPTION + "," + IMAGE + "," + CATEGORY + "," + DIFFICULTY + "," +
                IS_DEPRECATED + ") " +
                "VALUES(:description, :image, :category, :difficulty, :is_deprecated)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(question), keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public void delete(int id) {

        String sql = "DELETE FROM question WHERE " + ID + "= :id";

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource().addValue(ID, id));
    }

    public void updateQuestion(Question question) {
        String sql = "UPDATE question SET " + DESCRIPTION + "=:description, " + CATEGORY + "=:category, "
                + IMAGE + "=:image, " + DIFFICULTY + "=:difficulty, " + IS_DEPRECATED + "=:is_deprecated WHERE " + ID + " =:id";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(question));
    }

    public Question findQuestionById(int id) {
        String sql = "SELECT * FROM question WHERE " + ID + " =:id";

        return namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource().addValue(ID, id), new QuestionRowMapper());
    }

    public void setDeprecated(int id, boolean isDeprecated) {
        String sql = "UPDATE question SET " + IS_DEPRECATED + "=:is_deprecated WHERE " + ID + " =:id";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromParameters(id, isDeprecated));
    }

    public boolean exists(int id) {
        String sql = "SELECT * from question WHERE " + ID + " =:id";

        List<Question> result = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID, id), new QuestionRowMapper());

        return !result.isEmpty();
    }

    private SqlParameterSource getSqlParameterSourceFromParameters(int idQuestion, boolean isDeprecated) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue(ID, idQuestion);
        parameterSource.addValue(IS_DEPRECATED, isDeprecated);

        return parameterSource;
    }

    private SqlParameterSource getSqlParameterSourceFromModel(Question question) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (question != null) {
            parameterSource.addValue(ID, question.getIdQuestion());
            parameterSource.addValue(DESCRIPTION, question.getDescription());

            if (question.getImage() != null) {
                parameterSource.addValue(IMAGE, ImageConverter.convertStringBase64ToBlob(question.getImage()));
            } else {
                parameterSource.addValue(IMAGE, question.getImage());
            }

            parameterSource.addValue(CATEGORY, question.getCategory());

            if (question.getDifficulty() != null) {
                parameterSource.addValue(DIFFICULTY, question.getDifficulty().toString());
            } else {
                parameterSource.addValue(DIFFICULTY, question.getDifficulty());
            }

            parameterSource.addValue(IS_DEPRECATED, question.isDeprecated());
        }

        return parameterSource;
    }
}