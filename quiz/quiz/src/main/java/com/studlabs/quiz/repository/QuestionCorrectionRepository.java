package com.studlabs.quiz.repository;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.mapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class QuestionCorrectionRepository {

    public static final String ID_USER = "id_user";
    public static final String ID_QUESTION = "id_question";
    public static final String QUESTION_TEXT = "question_text";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public QuestionCorrectionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<QuestionCorrection> findAll() {
        String sql = String.format("SELECT %s, %s, %s FROM question_correction", ID_USER, ID_QUESTION, QUESTION_TEXT);
        return namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromModel(null), new QuestionCorrectionRowMapper());
    }

    public void insertQuestionCorrection(QuestionCorrection questionCorrection) {
        String sql = String.format("INSERT INTO question_correction (%s, %s, %s) VALUES(:%s, :%s, :%s)", ID_USER, ID_QUESTION, QUESTION_TEXT, ID_USER, ID_QUESTION, QUESTION_TEXT);
        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(questionCorrection));
    }

    public void updateQuestionCorrection(QuestionCorrection questionCorrection) {
        String sql = String.format("UPDATE question_correction SET %s=:%s WHERE %s=:%s and %s=:%s", QUESTION_TEXT, QUESTION_TEXT, ID_USER, ID_USER, ID_QUESTION, ID_QUESTION);
        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromModel(questionCorrection));
    }

    public void deleteQuestionCorrection(String idUser, int idQuestion) {
        String sql = String.format("DELETE FROM question_correction WHERE %s=:%s and %s=:%s", ID_USER, ID_USER, ID_QUESTION, ID_QUESTION);
        namedParameterJdbcTemplate.update(sql, getSqlParameterSourceFromParameters(idUser, idQuestion));
    }

    public boolean exists(String idUser, int idQuestion) {
        String sql = String.format("SELECT * FROM question_correction WHERE %s=:%s and %s=:%s", ID_USER, ID_USER, ID_QUESTION, ID_QUESTION);

        List<QuestionCorrection> questionCorrectionList = namedParameterJdbcTemplate.query(sql, getSqlParameterSourceFromParameters(idUser, idQuestion), new QuestionCorrectionRowMapper());
        return !questionCorrectionList.isEmpty();
    }

    private SqlParameterSource getSqlParameterSourceFromModel(QuestionCorrection questionCorrection) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        if (questionCorrection != null) {
            parameterSource.addValue(ID_USER, questionCorrection.getIdUser());
            parameterSource.addValue(ID_QUESTION, questionCorrection.getIdQuestion());
            parameterSource.addValue(QUESTION_TEXT, questionCorrection.getCorrectionText());

        }
        return parameterSource;
    }

    private SqlParameterSource getSqlParameterSourceFromParameters(String idUser, int idQuestion) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        parameterSource.addValue(ID_USER, idUser);
        parameterSource.addValue(ID_QUESTION, idQuestion);

        return parameterSource;
    }
}
