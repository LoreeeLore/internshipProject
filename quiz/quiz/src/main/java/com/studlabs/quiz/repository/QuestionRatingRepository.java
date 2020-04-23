package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class QuestionRatingRepository {

    public static final String ID_QUESTION = "id_question";
    public static final String ID_USER = "id_user";
    public static final String RATE = "rate";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public QuestionRatingRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void insert(QuestionRating questionRating) {
        String sql = "INSERT INTO question_rating(" + ID_QUESTION + "," + ID_USER +
                "," + RATE + ") VALUES(:id_question, :id_user, :rate)";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(questionRating));
    }

    public void delete(String idUser, int idQuestion) {
        String sql = "DELETE FROM question_rating WHERE " + ID_USER + "= :id_user AND " +
                ID_QUESTION + " = :id_question";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromParameters(idUser, idQuestion));
    }

    public List<QuestionRating> findAll() {
        String sql = "SELECT * FROM question_rating";

        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromModel(null), new QuestionRatingRowMapper());
    }

    public boolean exists(String idUser, int idQuestion) {
        String sql = "SELECT * FROM question_rating WHERE " + ID_USER + "= :id_user AND " +
                ID_QUESTION + " = :id_question";

        List<QuestionRating> result = namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromParameters(idUser, idQuestion), new QuestionRatingRowMapper());

        return !result.isEmpty();
    }

    private SqlParameterSource getSqlParameterSourceFromParameters(String idUser, int idQuestion) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue(ID_USER, idUser);
        parameterSource.addValue(ID_QUESTION, idQuestion);

        return parameterSource;
    }

    private SqlParameterSource getSqlParameterSourceFromModel(QuestionRating questionRating) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (questionRating != null) {
            parameterSource.addValue(ID_QUESTION, questionRating.getIdQuestion());
            parameterSource.addValue(ID_USER, questionRating.getIdUser());
            parameterSource.addValue(RATE, questionRating.isLike());
        }

        return parameterSource;
    }
}
