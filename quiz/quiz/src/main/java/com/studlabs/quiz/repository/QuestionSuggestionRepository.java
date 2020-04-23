package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.support.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class QuestionSuggestionRepository {

    public static final String ID_SUGGESTION = "id";
    public static final String ID_USER = "id_user";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String CATEGORY = "category";
    public static final String DIFFICULTY = "difficulty";
    public static final String TYPE = "type";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public QuestionSuggestionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public boolean exists(int idSuggestion) {
        String sql = String.format("SELECT * from question_suggestion WHERE %s=%d", ID_SUGGESTION, idSuggestion);

        List<QuestionSuggestion> result = namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromModel(null), new QuestionSuggestionRowMapper());

        return !result.isEmpty();
    }

    public List<QuestionSuggestion> findAll() {
        String sql = "SELECT * FROM question_suggestion";

        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromModel(null), new QuestionSuggestionRowMapper());
    }

    public int insertSuggestion(QuestionSuggestion questionSuggestion) {
        String sql = String.format("INSERT INTO question_suggestion(%s,%s,%s,%s,%s,%s)" +
                        " VALUES(:id_user, :description, :image, :category, :difficulty, :type)",
                ID_USER, DESCRIPTION, IMAGE, CATEGORY, DIFFICULTY,  TYPE);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(questionSuggestion), keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : 0;
    }

    public void update(int idSuggestion, QuestionSuggestion questionSuggestion) {
        String sql = String.format("UPDATE question_suggestion SET %s=:description, %s=:category, %s=:type, %s=:image, %s=:difficulty" +
                " WHERE %s=%d", DESCRIPTION, CATEGORY, TYPE, IMAGE, DIFFICULTY, ID_SUGGESTION, idSuggestion);

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(questionSuggestion));
    }

    public void delete(int idSuggestion) {
        String sql = String.format("DELETE FROM question_suggestion WHERE %s=%d", ID_SUGGESTION, idSuggestion);

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(null));
    }

    private SqlParameterSource getSqlParameterSourceFromModel(QuestionSuggestion question) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (question != null) {
            parameterSource.addValue(ID_USER, question.getIdUser());
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

            parameterSource.addValue(TYPE, question.getType());
        }

        return parameterSource;
    }
}
