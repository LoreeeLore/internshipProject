package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class QuizRatingRepository {

    public static final String ID_USER = "id_user";
    public static final String ID_QUIZ = "id_quiz";
    public static final String LIKE = "rate";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public QuizRatingRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<QuizRating> findAll() {
        return namedParameterJdbcTemplate.query("SELECT * FROM quiz_rating", getSqlParameterSourceFromModel(null), new QuizRatingRowMapper());
    }

    public void addQuizRating(QuizRating quizRating) {
        String sql = "INSERT INTO quiz_rating(" + ID_USER + "," + ID_QUIZ + "," + LIKE + ") VALUES(:idUser,:idQuiz,:rate)";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(quizRating));
    }

    public void deleteQuizRating(int idQuiz, String idUser) {

        QuizRating quizRating = new QuizRating(idUser, idQuiz, true);
        String sql = "DELETE FROM quiz_rating WHERE " + ID_USER + " =:idUser and " + ID_QUIZ + " =:idQuiz";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(quizRating));
    }

    public void updateQuizRating(QuizRating quizRating) {
        String sql = "UPDATE quiz_rating SET " + LIKE + "=:rate WHERE " + ID_QUIZ + " =:idQuiz and " + ID_USER + " =:idUser";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(quizRating));
    }

    public QuizRating findQuizRatingById(int idQuiz, String idUser) {

        QuizRating quizRating = new QuizRating(idUser, idQuiz, true);
        String sql = "SELECT * FROM quiz_rating WHERE " + ID_QUIZ + " =:idQuiz and " + ID_USER + " =:idUser";

        return namedParameterJdbcTemplate.queryForObject(sql, getSqlParameterSourceFromModel(quizRating), new QuizRatingRowMapper());
    }

    public boolean exists(String idUser, int idQuiz) {
        String sql = "SELECT * FROM quiz_rating WHERE " + ID_USER + "= :id_user AND " +
                ID_QUIZ + " = :id_quiz";

        List<QuizRating> result = namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromParameters(idUser, idQuiz), new QuizRatingRowMapper());

        return !result.isEmpty();
    }

    private SqlParameterSource getSqlParameterSourceFromParameters(String idUser, int idQuiz) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue(ID_USER, idUser);
        parameterSource.addValue(ID_QUIZ, idQuiz);

        return parameterSource;
    }

    private SqlParameterSource getSqlParameterSourceFromModel(QuizRating quizRating) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        if (quizRating != null) {
            parameterSource.addValue("idUser", quizRating.getIdUser());
            parameterSource.addValue("idQuiz", quizRating.getIdQuiz());
            parameterSource.addValue("rate", quizRating.isLike());

        }
        return parameterSource;
    }
}
