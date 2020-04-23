package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class QuizAccessRepository {
    public static final String ID_USER = "id_user";
    public static final String ID_QUIZ = "id_quiz";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public QuizAccessRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void assignQuizToUser(String idUser, int idQuiz) {
        String sql = "INSERT INTO quiz_access(" + ID_USER + "," + ID_QUIZ + ") VALUES (:id_user, :id_quiz)";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromParameters(idUser, idQuiz));
    }

    public List<QuizAccess> findAll() {
        String sql = "SELECT * from quiz_access";

        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromParameters(null, null), new QuizAccessRowMapper());
    }

    public boolean exists(String idUser, int idQuiz) {
        String sql = "SELECT * from quiz_access WHERE " + ID_USER + "= :id_user AND " +
                ID_QUIZ + "= :id_quiz";

        List<QuizAccess> result = namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromParameters(idUser, idQuiz), new QuizAccessRowMapper());

        return !result.isEmpty();
    }

    public void deleteUserFromQuiz(String idUser, int idQuiz) {
        String sql = "DELETE FROM quiz_access WHERE " + ID_USER + " =:id_user and " + ID_QUIZ + " =:id_quiz";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromParameters(idUser, idQuiz));
    }

    private SqlParameterSource getSqlParameterSourceFromParameters(String idUser, Integer idQuiz) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue(ID_USER, idUser);
        parameterSource.addValue(ID_QUIZ, idQuiz);

        return parameterSource;
    }

}
