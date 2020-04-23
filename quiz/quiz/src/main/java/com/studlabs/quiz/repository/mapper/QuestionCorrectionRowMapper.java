package com.studlabs.quiz.repository.mapper;

import com.studlabs.quiz.model.*;
import com.studlabs.quiz.repository.*;
import org.springframework.jdbc.core.*;

import java.sql.*;

public class QuestionCorrectionRowMapper implements RowMapper<QuestionCorrection> {

    @Override
    public QuestionCorrection mapRow(ResultSet resultSet, int i) throws SQLException {
        QuestionCorrection questionCorrection = new QuestionCorrection();

        questionCorrection.setIdUser(resultSet.getString(QuestionCorrectionRepository.ID_USER));
        questionCorrection.setIdQuestion(resultSet.getInt(QuestionCorrectionRepository.ID_QUESTION));
        questionCorrection.setCorrectionText(resultSet.getString(QuestionCorrectionRepository.QUESTION_TEXT));

        return questionCorrection;
    }
}
