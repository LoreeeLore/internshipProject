package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class QuestionQuizRepository {

    public static final String ID_QUIZ = "id_quiz";
    public static final String ID_QUESTION = "id_question";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public QuestionQuizRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<QuestionQuiz> findAllByQuizId(int id) {
        String sql = "SELECT * FROM question_quiz WHERE " + ID_QUIZ + " =:id_quiz";

        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource().addValue(ID_QUIZ, id), new QuestionQuizRowMapper());
    }

    public List<QuestionQuiz> findAll() {
        String sql = "SELECT * FROM question_quiz";

        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromParameters(null, null), new QuestionQuizRowMapper());
    }

    public void assignQuestionToQuiz(int idQuiz, int idQuestion) {
        String sql = "INSERT INTO question_quiz(" + ID_QUIZ + ", " + ID_QUESTION + ") VALUES (:id_quiz, :id_question)";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromParameters(idQuiz, idQuestion));
    }

    public void deleteQuestionFromQuiz(int idQuiz, int idQuestion) {
        String sql = "DELETE FROM question_quiz WHERE " + ID_QUIZ + " =:id_quiz and " + ID_QUESTION + " =:id_question";

        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromParameters(idQuiz, idQuestion));
    }

    public boolean exists(int idQuestion, int idQuiz) {
        String sql = "SELECT * FROM question_quiz WHERE " + ID_QUIZ + " =:id_quiz and " + ID_QUESTION + " =:id_question";

        List<QuestionQuiz> result = namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromParameters(idQuiz, idQuestion), new QuestionQuizRowMapper());

        return !result.isEmpty();
    }

    private SqlParameterSource getSqlParameterSourceFromParameters(Integer idQuiz, Integer idQuestion) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue(ID_QUIZ, idQuiz);
        parameterSource.addValue(ID_QUESTION, idQuestion);

        return parameterSource;
    }
}
